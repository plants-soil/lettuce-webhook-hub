package com.plantssoil.common.config;

import static org.junit.Assert.*;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.plantssoil.common.security.AesEncrypter;
import com.plantssoil.common.security.KeyStoreEncrypter;
import com.plantssoil.common.security.KeyStoreEncrypter.KeyStoreType;
import com.plantssoil.common.test.TempDirectoryUtility;

public class ConfigFactoryTest {
	private TempDirectoryUtility util = new TempDirectoryUtility();

	@Before
	public void setUp() throws Exception {
		System.setProperty("systemproperty_integer", "0");
		System.setProperty("systemproperty_string", AesEncrypter.getInstance().encrypt("systemproperty_encrypted_value"));
		
		Properties p = new Properties();
		p.put("propertyfile_boolean", "true");
		try (FileOutputStream fos = new FileOutputStream(util.getTempDir() + "/lettuce.properties")) {
			p.store(fos, "##This is the test lettuce properties");
		}
		
		KeyStoreEncrypter jks = KeyStoreEncrypter.getInstance(KeyStoreType.pkcs12, util.getTempDir() + "/lettuce.jks", "goodmorning");
		jks.writeEntry("jks_bigdecimal", "1000");
		
		System.setProperty("systemproperty_replace", "Replaced value - ${systemproperty_string}");
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
    	
		Configuration config = ConfigFactory.getInstance().getConfiguration();
		int systemproperty_integer = config.getInt("systemproperty_integer");
		String systemproperty_string = config.getString("systemproperty_string");
		boolean propertyfile_boolean = config.getBoolean("propertyfile_boolean");
		BigDecimal jks_bigdecimal = config.getBigDecimal("jks_bigdecimal");
		String systemproperty_replace = config.getString("systemproperty_replace");
		System.out.println("System Property Integer: " + systemproperty_integer);
		System.out.println("System Property Encrypted String: " + systemproperty_string);
		System.out.println("Property File Boolean: " + propertyfile_boolean);
		System.out.println("KeyStore File BigDecimal: " + jks_bigdecimal);
		System.out.println("KeyStore File BigDecimal: " + systemproperty_replace);
		assertTrue(systemproperty_integer == 0);
		assertEquals("systemproperty_encrypted_value", systemproperty_string);
		assertTrue(propertyfile_boolean);
		assertEquals(new BigDecimal(1000), jks_bigdecimal);
		assertEquals("Replaced value - systemproperty_encrypted_value", systemproperty_replace);
	}

}
