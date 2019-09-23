/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raas.r3server.smr;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import raas.r3lib.communication.Command;
import raas.r3lib.communication.Response;
import raas.r3server.config.ConfigManager;
import raas.r3server.model.SmrApplication;
import raas.r3server.model.SmrLibrary;
import raas.r3server.smr.delivery.ConsensusDelivery;
import raas.r3server.smr.delivery.CommandListener;
import raas.r3server.smr.delivery.DeliveryFactory;
import raas.r3server.loader.SmrLoader;

/**
 *
 * @author paola
 */
public class SmrManager implements CommandListener {

    private HashMap<String, SmrApplication> applications;
    private HashMap<String, SmrLibrary> smrLibraries;
    private ConsensusDelivery delivery;
    private final int replicaid;
    private static SmrManager instance;

    public static SmrManager getInstance() {
        if (instance == null) {
            instance = new SmrManager();
        }
        return instance;
    }

    private SmrManager() {
        this.applications = new HashMap<>();
        this.smrLibraries = new HashMap<>();

        this.replicaid = ConfigManager.getInstance().getReplicaId();
        int quantApps = ConfigManager.getInstance().getQuantApps();
        try {
            delivery = DeliveryFactory.createDelivery(ConfigManager.getInstance().getProtocol(), this, replicaid);
            delivery.configure();

            //PARA O EXPERIMENTO
            Path p = Paths.get("services/KeyValueService.jar");
            //Path p = Paths.get("/home/paola/Documents/tcc/genericrsm/KeyValueService.jar");

            byte[] content = Files.readAllBytes(p);
            addSmrLibrary("keyvalueservice", content, replicaid);
            addSmrInstance("keyvalueservice", "kv0");

            if (quantApps == 2) {
                addSmrInstance("keyvalueservice", "kv1");
            } else if (quantApps == 3) {
                addSmrInstance("keyvalueservice", "kv1");
                addSmrInstance("keyvalueservice", "kv2");
            }

        } catch (Exception ex) {
            Logger.getLogger(SmrManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startSMR() {
        delivery.start();
    }

    @Override
    public Response listenCommand(Command cmd) {
        Response response;

        try {
            //System.out.println(cmd.smrCmd);
            switch (cmd.smrCmd) {
                case "invoke_method":

                    Object result = invoke(cmd.str0, cmd.params);
                    response = new Response(result, true, "success");

                    break;

                case "register_type":

                    System.out.println("register_type");
                    addSmrLibrary(cmd.str0, cmd.libSource, this.replicaid);
                    //APENAS PARA O EXPERIMENTO
                    addSmrInstance(cmd.str0, getRandomString(10));
                    response = new Response(null, true, "success");

                    break;
                case "new_app_instance":

                    // str0 libName
                    // str1 instanceName
                    addSmrInstance(cmd.str0, cmd.str1);
                    response = new Response(null, true, "success");

                    break;
                default:
                    response = new Response(new Exception("r3lib.notSuportedCommand"), false, "This action does not exists");
            }
        } catch (Exception ex) {
            Logger.getLogger(SmrManager.class.getName()).log(Level.SEVERE, null, ex);
            response = new Response(ex, false, "Error");
        }

        return response;
    }

    private String getRandomString(int size) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVXZ";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < size) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    private Object invoke(String url, Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {

        //System.out.println("debug [invoke]: url = " + url);
        String[] parts = url.split("\\.");
        int length = parts.length;
        String appName = parts[0];

        String className = String.join(".", Arrays.copyOfRange(parts, 1, length - 1));

        String methodName = parts[length - 1];
        Method m = getApp(appName).getMethod(className, methodName);

        //System.out.println(Arrays.toString(args));
        return m.invoke(this.getInstance(appName, className), args);
    }

    private void addSmrLibrary(String id, byte[] content, int replica) throws IOException, Exception {

        if (this.smrLibraries.containsKey(id)) {
            throw new Exception("Id já existente");
        }
        String name = "./services_jars/" + replica + "/" + id + ".jar";
        File f = new File(name);
        f.createNewFile();
        Files.write(f.toPath(), content);

        SmrLibrary lib = new SmrLibrary(new JarFile(name), id);
        lib.setClonable(SmrLoader.getInstance().loadLibrary(lib));
        lib.setDoc(lib.getClonable().getAvailableUrls());
        this.smrLibraries.put(id, lib);

    }

    private void addSmrInstance(String sourceId, String appId) throws Exception {

        SmrLibrary sourceCode = smrLibraries.get(sourceId);
        if (sourceCode == null) {
            throw new Exception("source code inválido");
        }

        applications.put(appId, SmrLoader.getInstance().loadApplication(sourceCode, appId));
        //applications.put(appId, smrLibraries.get(sourceId).getClonable().clone());

        /*System.out.println("apps");
        for (String a : applications.keySet()) {
            System.out.println(applications.get(a).toString());
        }*/
    }

    private String getAvaiableLibraries() {
        String libs = "";
        for (SmrLibrary l : smrLibraries.values()) {
            libs += l.getId() + ";\n";
        }
        return libs;
    }

    private String getAvaiableApps() {
        String apps = "";
        for (SmrApplication a : this.applications.values()) {
            apps += a.getId() + " [" + a.getSmrLib().getId() + "];\n";
        }
        return apps;
    }

    private String getLibraryDoc(String libId) throws Exception {
        if (this.smrLibraries.containsKey(libId)) {
            return this.smrLibraries.get(libId).getDoc();
        }
        throw new Exception("Biblioteca inexistente");
    }

    private String getLibApp(String appName) {
        SmrApplication app = getApp(appName);
        if (app == null) {
            return "";
        }
        return app.getSmrLib().getId();
    }

    private SmrApplication getApp(String appId) {
        return this.applications.get(appId);
    }

    private Object getInstance(String appName, String className) throws InstantiationException, IllegalAccessException {
        return getApp(appName).getInstance(className);
    }

    // getters and setters
    private HashMap<String, SmrApplication> getApplications() {
        return applications;
    }

    private void setApplications(HashMap<String, SmrApplication> applications) {
        this.applications = applications;
    }

    private HashMap<String, SmrLibrary> getSmrLibraries() {
        return smrLibraries;
    }

    private void setSmrLibraries(HashMap<String, SmrLibrary> sourceCodes) {
        this.smrLibraries = sourceCodes;
    }

}
