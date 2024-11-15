package com.plantssoil.common.config;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;

/**
 * The delegate of apache commons configuration
 * 
 * @author danialdy
 * @Date 14 Nov 2024 8:31:40 pm
 */
public class ConfigurationDelegate implements IConfiguration {
    private Configuration configuration;

    public ConfigurationDelegate(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public IConfiguration subset(String prefix) {
        return new ConfigurationDelegate(this.configuration.subset(prefix));
    }

    @Override
    public boolean isEmpty() {
        return this.configuration.isEmpty();
    }

    @Override
    public boolean containsKey(String key) {
        return this.configuration.containsKey(key);
    }

    @Override
    public void addProperty(String key, Object value) {
        this.configuration.addProperty(key, value);
    }

    @Override
    public void setProperty(String key, Object value) {
        this.configuration.setProperty(key, value);
    }

    @Override
    public void clearProperty(String key) {
        this.configuration.clearProperty(key);
    }

    @Override
    public void clear() {
        this.configuration.clear();
    }

    @Override
    public Object getProperty(String key) {
        return this.configuration.getProperty(key);
    }

    @Override
    public Iterator<String> getKeys(String prefix) {
        return this.configuration.getKeys(prefix);
    }

    @Override
    public Iterator<String> getKeys() {
        return this.configuration.getKeys();
    }

    @Override
    public Properties getProperties(String key) {
        return this.configuration.getProperties(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return this.configuration.getBoolean(key);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return this.configuration.getBoolean(key, defaultValue);
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        return this.configuration.getBoolean(key, defaultValue);
    }

    @Override
    public byte getByte(String key) {
        return this.configuration.getByte(key);
    }

    @Override
    public byte getByte(String key, byte defaultValue) {
        return this.configuration.getByte(key, defaultValue);
    }

    @Override
    public Byte getByte(String key, Byte defaultValue) {
        return this.configuration.getByte(key, defaultValue);
    }

    @Override
    public double getDouble(String key) {
        return this.configuration.getDouble(key);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return this.configuration.getDouble(key, defaultValue);
    }

    @Override
    public Double getDouble(String key, Double defaultValue) {
        return this.configuration.getDouble(key, defaultValue);
    }

    @Override
    public float getFloat(String key) {
        return this.configuration.getFloat(key);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return this.configuration.getFloat(key, defaultValue);
    }

    @Override
    public Float getFloat(String key, Float defaultValue) {
        return this.configuration.getFloat(key, defaultValue);
    }

    @Override
    public int getInt(String key) {
        return this.configuration.getInt(key);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return this.configuration.getInt(key, defaultValue);
    }

    @Override
    public Integer getInteger(String key, Integer defaultValue) {
        return this.configuration.getInteger(key, defaultValue);
    }

    @Override
    public long getLong(String key) {
        return this.configuration.getLong(key);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return this.configuration.getLong(key, defaultValue);
    }

    @Override
    public Long getLong(String key, Long defaultValue) {
        return this.configuration.getLong(key, defaultValue);
    }

    @Override
    public short getShort(String key) {
        return this.configuration.getShort(key);
    }

    @Override
    public short getShort(String key, short defaultValue) {
        return this.configuration.getShort(key, defaultValue);
    }

    @Override
    public Short getShort(String key, Short defaultValue) {
        return this.configuration.getShort(key, defaultValue);
    }

    @Override
    public BigDecimal getBigDecimal(String key) {
        return this.configuration.getBigDecimal(key);
    }

    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return this.configuration.getBigDecimal(key, defaultValue);
    }

    @Override
    public BigInteger getBigInteger(String key) {
        return this.configuration.getBigInteger(key);
    }

    @Override
    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        return this.configuration.getBigInteger(key, defaultValue);
    }

    @Override
    public String getString(String key) {
        return this.configuration.getString(key);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return this.configuration.getString(key, defaultValue);
    }

    @Override
    public String[] getStringArray(String key) {
        return this.configuration.getStringArray(key);
    }

    @Override
    public List<Object> getList(String key) {
        return this.configuration.getList(key);
    }

    @Override
    public List<Object> getList(String key, List<?> defaultValue) {
        return this.configuration.getList(key, defaultValue);
    }

}
