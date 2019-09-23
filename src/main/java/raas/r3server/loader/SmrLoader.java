/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raas.r3server.loader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.logging.Level;
import java.util.logging.Logger;

import raas.r3lib.annotations.SmrMethod;
import raas.r3server.model.SmrApplication;
import raas.r3server.model.SmrClass;
import raas.r3server.model.SmrLibrary;

/**
 *
 * @author paola
 */
public class SmrLoader {

    private static SmrLoader instance;
    private final HashMap<String, URLClassLoader> libLoaders;
    private final HashMap<String, URLClassLoader> appLoaders;

    private SmrLoader() {
        libLoaders = new HashMap<>();
        appLoaders = new HashMap<>();
    }

    public static SmrLoader getInstance() {
        if (instance == null) {
            instance = new SmrLoader();
        }
        return instance;
    }

    public HashMap<String, URLClassLoader> getLibLoaders() {
        return libLoaders;
    }

    public HashMap<String, URLClassLoader> getAppLoaders() {
        return appLoaders;
    }

    private void addLibLoader(String libId, URLClassLoader loader) {
        getLibLoaders().put(libId, loader);
    }

    private void addAppLoader(String libId, URLClassLoader loader) {
        getAppLoaders().put(libId, loader);
    }

    public URLClassLoader getAppLoader(String libId) {
        return getAppLoaders().get(libId);
    }

    public URLClassLoader getLibLoader(String libId) {
        return getLibLoaders().get(libId);
    }

    // le todo o jar, salva todas classes SMR e seus métodos
    public SmrApplication loadLibrary(SmrLibrary source) throws MalformedURLException {

        URLClassLoader cl = getLibLoader(source.getId());
        if (cl == null) {
            URL[] urls = { new URL("jar:file:" + source.getJar().getName() + "!/") };
            cl = URLClassLoader.newInstance(urls);
            this.addLibLoader(source.getId(), cl);
        }

        return this.readJar(cl, source.getId(), source);
    }

    // não precisa ler todo o jar, apenas lê as classes salvas pelo "loadLibrary()"
    public SmrApplication loadApplication(SmrLibrary source, String appId) {

        SmrApplication smrApp = new SmrApplication(source, appId);
        try {
            URL[] urls = { new URL("jar:file:" + source.getJar().getName() + "!/") };
            URLClassLoader cl = URLClassLoader.newInstance(urls);
            addAppLoader(appId, cl);

            for (SmrClass smrClass : source.getClonable().getInstances().values()) {
                Class c = cl.loadClass(smrClass.getNameSpace());
                SmrClass newSmrClass = new SmrClass(c, smrClass.getNameSpace());

                for (Method m : c.getMethods()) {
                    if (m.isAnnotationPresent((Class<? extends Annotation>) SmrMethod.class)) {
                        newSmrClass.addMethod(m);
                    }
                }
                smrApp.addClass(newSmrClass);
            }
            
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | MalformedURLException ex) {
            Logger.getLogger(SmrLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return smrApp;
    }

    private SmrApplication readJar(URLClassLoader cl, String appId, SmrLibrary source) {

        Enumeration e = source.getJar().entries();
        SmrApplication smrApp = new SmrApplication(source, appId);

        while (e.hasMoreElements()) {

            JarEntry je = (JarEntry) e.nextElement();
            if (je.isDirectory() || !je.getName().endsWith(".class")) {
                continue;
            }

            // -6 because of .class
            String className = je.getName().substring(0, je.getName().length() - 6);
            className = className.replace('/', '.');

            try {
                Class c = cl.loadClass(className);
                SmrClass smrClass = new SmrClass(c, className);

                boolean isClientClass = false;

                //Object o = c.newInstance();
                for (Method m : c.getMethods()) {
                    if (m.isAnnotationPresent((Class<? extends Annotation>) SmrMethod.class)) {

                        isClientClass = true;
                        smrClass.addMethod(m);

                    }

                }
                if (isClientClass) {
                    smrApp.addClass(smrClass);
                }

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(SmrLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //System.out.println(smrApp.getAvailableUrls());
        return smrApp;
    }

}
