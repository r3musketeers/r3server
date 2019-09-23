package raas.r3server.smr.delivery;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultRecoverable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import raas.r3lib.communication.Command;
import raas.r3lib.communication.Response;
import raas.r3server.config.ConfigManager;
import raas.r3server.performance.control.ThroughputController;
import raas.r3server.loader.CustomObjectInputStream;
import raas.r3server.util.OrderedProperties;

/**
 *
 * @author paola
 */
public class BFTDelivery extends ConsensusDelivery {

    private BFTprotocol bft;

    public BFTDelivery(CommandListener listener, int replicaId) {
        super(listener, replicaId);
    }

    @Override
    public void start() {
        bft = new BFTprotocol(this.listener, this.replicaId);
    }

    @Override
    public void configure() {
        //botando '=' nos hosts config.. resolver
        /*try {
            updateHostsConfig();
            updateSystemConfig();
        } catch (IOException ex) {
            Logger.getLogger(BFTDelivery.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    private void updateHostsConfig() throws FileNotFoundException, IOException {

        FileInputStream fis = new FileInputStream("config/hosts.config");
        OrderedProperties prop = new OrderedProperties();
        prop.load(fis);

        ConfigManager systemConfig = ConfigManager.getInstance();
        int numReplicas = systemConfig.getNumReplicas();

        for (int i = 0; i < numReplicas; i++) {
            prop.setProperty(i + "", systemConfig.getReplica(i));
        }
        try (FileOutputStream out = new FileOutputStream("config/hosts.config")) {
            prop.store(out, null);
        }
    }

    private void updateSystemConfig() throws FileNotFoundException, IOException {

        FileInputStream fis = new FileInputStream("config/system.config");
        OrderedProperties prop = new OrderedProperties();
        prop.load(fis);

        ConfigManager systemConfig = ConfigManager.getInstance();
        int numReplicas = systemConfig.getNumReplicas();
        int f = (numReplicas - 1) / 2;

        prop.setProperty("system.servers.num", numReplicas + "");
        prop.setProperty("system.servers.f", f + "");

        try (FileOutputStream out = new FileOutputStream("config/system.config")) {
            prop.store(out, null);
        }
    }

}

class BFTprotocol extends DefaultRecoverable {

    private final ServiceReplica replica;
    private final CommandListener listener;

    public BFTprotocol(CommandListener listener, int replicaId) {
        this.listener = listener;
        System.out.println("replica id " + replicaId);
        replica = new ServiceReplica(replicaId, this, this);
    }

    @Override
    public byte[][] appExecuteBatch(byte[][] commands, MessageContext[] msgCtxs) {
        byte[][] replies = new byte[commands.length][];
        for (int i = 0; i < commands.length; i++) {
            if (msgCtxs != null && msgCtxs[i] != null) {
                replies[i] = executeSingle(commands[i], msgCtxs[i]);
            } else {
                executeSingle(commands[i], null);
            }
        }

        return replies;
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
        return "TODO".getBytes();
    }

    private byte[] executeSingle(byte[] command, MessageContext msgCtx) {
        try {
            ThroughputController.getInstance().addRequisition();

            ByteArrayInputStream in = new ByteArrayInputStream(command);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] reply = null;

            CustomObjectInputStream cois = new CustomObjectInputStream(in);

            String loaderId = cois.readUTF();
            cois.setLoaderId(loaderId);

            //System.out.println("loader name: " + loaderId);
            Command cmd = (Command) cois.readObject();

            Response response = new Response(new Exception("r3lib.notSuportedCommand"), false, "This action does not exists");
            response = this.listener.listenCommand(cmd);
            //System.out.println("command: " + cmd.smrCmd + " url: " + cmd.str0);

            new ObjectOutputStream(out).writeObject(response);
            return out.toByteArray();

        } catch (IOException ioE) {
            ioE.printStackTrace();
            return new byte[0];
        } catch (Exception e) {
            //erro ao executar comando da rsm
            e.printStackTrace();
            return new byte[0];
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void installSnapshot(byte[] state) {

    }

    @Override
    public byte[] getSnapshot() {
        return "NOT TODAY".getBytes();
    }

}
