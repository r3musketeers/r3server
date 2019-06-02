/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package r3libserver.model;

import r3lib.annotations.SmrArg;
import r3lib.annotations.SmrMethod;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

/**
 *
 * @author paola
 */
public class SmrApplication implements Cloneable {

    // instancias das classes que podem ser chamadas pelos clientes
    private HashMap<String, SmrClass> instances;
    private SmrLibrary smrLib;
    private String id;

    public SmrApplication(SmrLibrary smrLib, String id) {
        this.instances = new HashMap<>();
        this.smrLib = smrLib;
        this.id = id;
    }

    @Override
    public SmrApplication clone() throws CloneNotSupportedException {
        SmrApplication a = (SmrApplication) super.clone();

        HashMap<String, SmrClass> newInstances = new HashMap<>();
        for (String c : a.instances.keySet()) {
            newInstances.put(c, a.instances.get(c).clone());
        }
        a.instances = newInstances;

        return a;
    }

    public void addClass(SmrClass smrClass) throws InstantiationException, IllegalAccessException {
        smrClass.instantiate();
        this.instances.put(smrClass.getClassType().getName(), smrClass);
    }

    public Method getMethod(String className, String methodName) {
        return this.instances.get(className).getMethod(methodName);
    }

    public Object getInstance(String instanceId) throws InstantiationException, IllegalAccessException {
        return this.instances.get(instanceId).getInstance();
    }

    public String getAvailableUrls() {
        String urls = "";

        for (SmrClass i : instances.values()) {
            String className = i.getClassType().getName();
            for (Method m : i.getMethods().values()) {
                urls += this.getId() + "." + className + "." + m.getName();
                if (m.isAnnotationPresent(SmrMethod.class)) {
                    SmrMethod a = m.getAnnotation(SmrMethod.class);
                    urls += " (" + a.description() + "): ";
                } else {
                    urls += " : ";
                }

                for (Parameter p : m.getParameters()) {

                    if (p.isAnnotationPresent(SmrArg.class)) {
                        SmrArg a = p.getAnnotation(SmrArg.class);
                        urls += ("@param " + a.name() + ":" + p.getType() + " (" + a.description() + "); ");
                    } else {
                        urls += ("@param " + p.getName() + " " + p.getType() + "; ");
                    }
                }
                urls += "@return " + m.getReturnType() + ";\n";
            }

        }
        return urls;
    }

    public HashMap<String, SmrClass> getInstances() {
        return instances;
    }

    public void setInstances(HashMap<String, SmrClass> instances) {
        this.instances = instances;
    }

    public SmrLibrary getSmrLib() {
        return smrLib;
    }

    public void setSmrLib(SmrLibrary smrSourceCode) {
        this.smrLib = smrSourceCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
