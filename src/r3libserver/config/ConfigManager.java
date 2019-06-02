/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package r3libserver.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import r3libserver.util.OrderedProperties;

/**
 *
 * @author paola
 */
public final class ConfigManager {

    private static ConfigManager instance;
    private final int replicaid;
    private final OrderedProperties properties;
    // para experimentos
    private final int quantApps;

    public static ConfigManager getInstance() {
        return instance;
    }

    private ConfigManager(int replicaid, int quantApps) {
        this.replicaid = replicaid;
        this.properties = loadConfig();
        this.quantApps = quantApps;
    }

    public static void init(int replicaid, int quantApps) {
        instance = new ConfigManager(replicaid, quantApps);
    }

    public int getReplicaId() {
        return this.replicaid;
    }

    private OrderedProperties loadConfig() {
        
        OrderedProperties prop = new OrderedProperties();
        try {
            FileInputStream fis = new FileInputStream("config.properties");
            prop.load(fis);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return prop;
    }

    public int getQuantApps(){
        return this.quantApps;
    }
    
    public String getProtocol() {
        return this.properties.getProperty("agreement.protocol");
    }
    
    public int getNumReplicas() {
        return Integer.parseInt(this.properties.getProperty("num.replicas"));
    }
    
    public String getReplica(int replicaId) {
        return this.properties.getProperty(replicaId + "");
    }
    
    
}
