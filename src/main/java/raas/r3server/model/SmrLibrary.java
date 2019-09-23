/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raas.r3server.model;

import java.util.jar.JarFile;

/**
 *
 * @author paola
 */
public class SmrLibrary {
    private JarFile jar;
    private String id;
    private String doc;
    private SmrApplication clonable;

    public SmrLibrary(JarFile jar, String id) {
        this.jar = jar;
        this.id = id;
    }

    public SmrApplication getClonable() {
        return clonable;
    }

    public void setClonable(SmrApplication clonable) {
        this.clonable = clonable;
    }

    
    public JarFile getJar() {
        return jar;
    }

    public void setJar(JarFile jar) {
        this.jar = jar;
    }

    public String getId() {
        return id;
    }

    public void setId(String name) {
        this.id = name;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }
    
    
}
