/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package r3libserver.smr.delivery;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import r3libserver.loader.CustomObjectInputStream;
import lsr.common.Configuration;
import lsr.paxos.replica.Replica;
import lsr.service.SimplifiedService;
import r3libserver.performance.control.ThroughputController;
import r3lib.communication.Command;
import r3lib.communication.Response;

/**
 *
 * @author paola
 */
public class SDelivery extends ConsensusDelivery {

    private final Replica spaxos;

    public SDelivery(CommandListener listener, int replicaId) throws IOException {
        super(listener, replicaId);
        spaxos = new Replica(new Configuration(), replicaId, new SPaxosProtocol(listener));
    }

    @Override
    public void start() {
        try {
            spaxos.start();
        } catch (IOException ex) {
            Logger.getLogger(SDelivery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void configure() {
        System.out.println("..configure..");
    }

}

class SPaxosProtocol extends SimplifiedService {

    private final CommandListener listener;

    public SPaxosProtocol(CommandListener listener) {
        this.listener = listener;
    }

    @Override
    protected byte[] execute(byte[] bytes) {
        try {
            ThroughputController.getInstance().addRequisition();

            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] reply = null;

            CustomObjectInputStream cois;
            cois = new CustomObjectInputStream(in);

            String loaderId = cois.readUTF();
            cois.setLoaderId(loaderId);

            //System.out.println("loader name: " + loaderId);
            Command cmd = (Command) cois.readObject();

            Response response = new Response(new Exception("r3lib.notSuportedCommand"), false, "This action does not exists");
            response = this.listener.listenCommand(cmd);
            //System.out.println("command: " + cmd.smrCmd + " url: " + cmd.str0);

            new ObjectOutputStream(out).writeObject(response);
            return out.toByteArray();

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(SPaxosProtocol.class.getName()).log(Level.SEVERE, null, ex);
            return new byte[0];
        }

    }

    @Override
    protected byte[] makeSnapshot() {
        return "TODO".getBytes();
    }

    /**
     * Brings the system up-to-date from a snapshot *
     */
    @Override
    protected void updateToSnapshot(byte[] snapshot) {

    }

}
