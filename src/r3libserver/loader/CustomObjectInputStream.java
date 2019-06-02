/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package r3libserver.loader;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.net.URL;

/**
 *
 * @author paola
 */
public class CustomObjectInputStream extends ObjectInputStream {

    private URL[] urls;
    private String loaderId;

    public CustomObjectInputStream(InputStream in) throws IOException {
        super(in);
        this.urls = new URL[1];
    }

    public void setLoaderId(String loaderId) {
        this.loaderId = loaderId;
    }

    public URL[] getUrls() {
        return urls;
    }

    public void setUrls(URL[] urls) {
        this.urls = urls;
    }

    @Override
    public Class resolveClass(ObjectStreamClass desc) throws IOException,
            ClassNotFoundException {

        try {
            //System.out.println("debug: " + desc.getName());
            return super.resolveClass(desc);

        } catch (Exception e) {
            /*System.out.println("debug [CustomObjectInputStream]: " + desc.toString());
            System.out.println("debug [CustomObjectInputStream]: " + urls[0].toString());
            System.out.println("debug [CustomObjectInputStream]: " + cl.toString());*/

            return SmrLoader.getInstance().getAppLoader(loaderId).loadClass(desc.getName());

        }

    }

}
