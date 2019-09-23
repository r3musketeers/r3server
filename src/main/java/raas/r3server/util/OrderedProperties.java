/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raas.r3server.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;

/**
 *
 * @author paola
 */
public class OrderedProperties extends Properties {
    private final LinkedHashSet<Object> keyOrder = new LinkedHashSet<>();

    @Override
    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(keyOrder);
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        keyOrder.add(key);
        return super.put(key, value);
    }
}