/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package r3libserver;

import r3libserver.config.ConfigManager;
import r3libserver.performance.control.ThroughputController;

// importando o framework
import r3libserver.smr.SmrManager;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class R3LibServer {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Use: java KeyValueServer <processId>");
            System.exit(-1);
        }

        try {
            int replicaid = Integer.parseInt(args[0]);
            int quantApps = 1;
            if(args.length == 2){
                quantApps = Integer.parseInt(args[1]);
            }
            ConfigManager.init(replicaid, quantApps);

            // initialing flow thread
            FlowThread flowThread = new FlowThread();
            new Thread(flowThread).start();

            // initialing the system
            SmrManager manager = SmrManager.getInstance();
            manager.startSMR();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }

}

class FlowThread implements Runnable {

    public FlowThread() {

    }

    public void run() {

        while (true) {
            try {
                Thread.sleep(245000);
            } catch (InterruptedException ex) {
                Logger.getLogger(FlowThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            int res = ThroughputController.getInstance().addFlow();
	    System.out.println(res / 240);
            
            /*try {
                Thread.sleep(1000);
                int res = ThroughputController.getInstance().addFlow();
                System.out.println(res);
            } catch (InterruptedException ex) {
                Logger.getLogger(FlowThread.class.getName()).log(Level.SEVERE, null, ex);
            }*/

        }
    }
}
