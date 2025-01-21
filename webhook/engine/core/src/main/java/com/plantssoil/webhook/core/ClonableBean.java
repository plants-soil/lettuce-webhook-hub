package com.plantssoil.webhook.core;

/**
 * All in-memory objects should extends from this class, otherwise any change on
 * the objects which find from registry will be updated into the registry
 * without expected
 * 
 * @author danialdy
 * @Date 18 Jan 2025 4:18:33 pm
 */
public class ClonableBean implements Cloneable {
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
