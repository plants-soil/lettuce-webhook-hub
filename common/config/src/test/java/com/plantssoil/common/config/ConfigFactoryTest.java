package com.plantssoil.common.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.plantssoil.common.security.AesEncrypter;
import com.plantssoil.common.security.KeyStoreEncrypter;
import com.plantssoil.common.security.KeyStoreEncrypter.KeyStoreType;
import com.plantssoil.common.test.TempDirectoryUtility;

public class ConfigFactoryTest {
    private TempDirectoryUtility util = new TempDirectoryUtility();

    public static void main(String[] args) throws Exception {
        ConfigFactoryTest test = new ConfigFactoryTest();
        test.setUp();
        test.testGetConfiguration();
        test.tearDown();
    }

    @Before
    public void setUp() throws Exception {
        System.setProperty("system.property.integer", "0");
        System.setProperty("system.property.string", AesEncrypter.getInstance().encrypt("system.property.encrypted.value"));

        Properties p = new Properties();
        p.put("property.file.boolean", "true");
        try (FileOutputStream fos = new FileOutputStream(util.getTempDir() + "/lettuce.properties")) {
            p.store(fos, "##This is the test lettuce properties");
        }

        KeyStoreEncrypter jks = KeyStoreEncrypter.getInstance(KeyStoreType.pkcs12, util.getTempDir() + "/lettuce.jks", "goodmorning");
        jks.writeEntry("jks.bigdecimal", "1000");

        System.setProperty("system.property.replace", "Replaced value - ${system.property.string}");
    }

    @After
    public void tearDown() throws Exception {
        util.removeTempDirectory();
    }

    @Test
    public void testGetConfiguration() {
        System.setProperty("lettuce.config.dir", util.getTempDir());
        System.setProperty("lettuce.keystore.type", "pkcs12");
        System.setProperty("lettuce.keystore.file", "lettuce.jks");
        System.setProperty("lettuce.keystore.password", "goodmorning");

        IConfiguration config = ConfigFactory.getInstance().getConfiguration();
        int systemproperty_integer = config.getInt("system.property.integer");
        String systemproperty_string = config.getString("system.property.string");
        boolean propertyfile_boolean = config.getBoolean("property.file.boolean");
        BigDecimal jks_bigdecimal = config.getBigDecimal("jks.bigdecimal");
        String systemproperty_replace = config.getString("system.property.replace");
        System.out.println("System Property Integer: " + systemproperty_integer);
        System.out.println("System Property Encrypted String: " + systemproperty_string);
        System.out.println("Property File Boolean: " + propertyfile_boolean);
        System.out.println("KeyStore File BigDecimal: " + jks_bigdecimal);
        System.out.println("KeyStore File BigDecimal: " + systemproperty_replace);
        assertTrue(systemproperty_integer == 0);
        assertEquals("system.property.encrypted.value", systemproperty_string);
        assertTrue(propertyfile_boolean);
        assertEquals(new BigDecimal(1000), jks_bigdecimal);
        assertEquals("Replaced value - system.property.encrypted.value", systemproperty_replace);
    }

}
