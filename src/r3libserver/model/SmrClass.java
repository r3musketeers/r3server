/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package r3libserver.model;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paola
 */
public class SmrClass implements Cloneable {

    private Class classType;
    private Object instance;
    private HashMap<String, Method> methods;
    private String nameSpace;

    public SmrClass(Class classType, String nameSpace) {
        this.methods = new HashMap<>();
        this.classType = classType;
        this.nameSpace = nameSpace;
    }

    @Override
    public SmrClass clone() throws CloneNotSupportedException {
        SmrClass c = (SmrClass) super.clone();

        try {
            c.instantiate();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
            Logger.getLogger(SmrClass.class.getName()).log(Level.SEVERE, null, ex);
        }

        return c;
    }

    public void instantiate() throws InstantiationException, IllegalAccessException {

        this.instance = this.classType.newInstance();

    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public Object getInstance() {
        return this.instance;
    }

    public void addMethod(Method m) {
        this.methods.put(m.getName(), m);
    }

    public Method getMethod(String name) {
        return this.methods.get(name);
    }

    // getters and setters
    public Class getClassType() {
        return classType;
    }

    public void setClassType(Class classType) {
        this.classType = classType;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public HashMap<String, Method> getMethods() {
        return methods;
    }

    public void setMethods(HashMap<String, Method> methods) {
        this.methods = methods;
    }

}
