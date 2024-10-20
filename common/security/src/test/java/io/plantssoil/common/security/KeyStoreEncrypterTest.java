package io.plantssoil.common.security;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.plantssoil.common.security.KeyStoreEncrypter.KeyStoreType;
import io.plantssoil.common.test.TempDirectoryUtility;

public class KeyStoreEncrypterTest {
	private TempDirectoryUtility tempDir = new TempDirectoryUtility();
	private Map<String,String> tempEntries = new HashMap<String, String>();
	
	@Before
	public void setUp() throws Exception {
		System.out.println(tempDir.getTempDir());
		for (int i = 0; i < 20; i++) {
			tempEntries.put("alias-from-map-" + i, "alias-from-map-value" + ThreadLocalRandom.current().nextInt(100000));
		}
	}
	
	@After
	public void tearDown() throws Exception {
		tempDir.removeTempDirectory();
	}

	@Test
	public void testWriteEntry() {
//		KeyStoreEncrypter dks = KeyStoreEncrypter.getInstance(KeyStoreType.dks, "dks.jks", "123456");
//		dks.writeEntry("alias-dks-01", "alias-dks-01-value");
//		KeyStoreEncrypter dksAgain = KeyStoreEncrypter.getInstance(KeyStoreType.dks, "dks.jks", "123456");
//		assertEquals("alias-dks-01-value", dksAgain.readEntry("alias-dks-01"));
		
		KeyStoreEncrypter jceks = KeyStoreEncrypter.getInstance(KeyStoreType.jceks, tempDir.getTempDir() + "/jceks.jks", "123456");
		jceks.writeEntry("alias-jceks-01", "alias-jceks-01-value");
		KeyStoreEncrypter jceksAgain = KeyStoreEncrypter.getInstance(KeyStoreType.jceks, tempDir.getTempDir() + "/jceks.jks", "123456");
		assertEquals("alias-jceks-01-value", jceksAgain.readEntry("alias-jceks-01"));
		
//		KeyStoreEncrypter jks = KeyStoreEncrypter.getInstance(KeyStoreType.jks, "jks.jks", "123456");
//		jks.writeEntry("alias-jks-01", "alias-jks-01-value");
//		KeyStoreEncrypter jksAgain = KeyStoreEncrypter.getInstance(KeyStoreType.jks, "jks.jks", "123456");
//		assertEquals("alias-jks-01-value", jksAgain.readEntry("alias-jks-01"));
		
//		KeyStoreEncrypter pkcs11 = KeyStoreEncrypter.getInstance(KeyStoreType.pkcs11, "pkcs11.jks", "123456");
//		pkcs11.writeEntry("alias-pkcs11-01", "alias-pkcs11-01-value");
//		KeyStoreEncrypter pkcs11Again = KeyStoreEncrypter.getInstance(KeyStoreType.pkcs11, "pkcs11.jks", "123456");
//		assertEquals("alias-pkcs11-01-value", pkcs11Again.readEntry("alias-pkcs11-01"));
		
		KeyStoreEncrypter pkcs12 = KeyStoreEncrypter.getInstance(KeyStoreType.pkcs12, tempDir.getTempDir() + "/pkcs12.jks", "123456");
		pkcs12.writeEntry("alias-pkcs12-01", "alias-pkcs12-01-value");
		KeyStoreEncrypter pkcs12Again = KeyStoreEncrypter.getInstance(KeyStoreType.pkcs12, tempDir.getTempDir() + "/pkcs12.jks", "123456");
		assertEquals("alias-pkcs12-01-value", pkcs12Again.readEntry("alias-pkcs12-01"));
	}

	@Test
	public void testWriteEntries() {
//		KeyStoreEncrypter dks = KeyStoreEncrypter.getInstance(KeyStoreType.dks, "dks.jks", "123456");
//		dks.writeEntries(tempEntries);
//		KeyStoreEncrypter dksAgain = KeyStoreEncrypter.getInstance(KeyStoreType.dks, "dks.jks", "123456");
//		assertEquals(tempEntries.get("alias-from-map-3"), dksAgain.readEntry("alias-from-map-3"));
		
		KeyStoreEncrypter jceks = KeyStoreEncrypter.getInstance(KeyStoreType.jceks, tempDir.getTempDir() + "/jceks.jks", "123456");
		jceks.writeEntries(tempEntries);
		KeyStoreEncrypter jceksAgain = KeyStoreEncrypter.getInstance(KeyStoreType.jceks, tempDir.getTempDir() + "/jceks.jks", "123456");
		assertEquals(tempEntries.get("alias-from-map-13"), jceksAgain.readEntry("alias-from-map-13"));
		
//		KeyStoreEncrypter jks = KeyStoreEncrypter.getInstance(KeyStoreType.jks, "jks.jks", "123456");
//		jks.writeEntries(tempEntries);
//		KeyStoreEncrypter jksAgain = KeyStoreEncrypter.getInstance(KeyStoreType.jks, "jks.jks", "123456");
//		assertEquals(tempEntries.get("alias-from-map-23"), jksAgain.readEntry("alias-from-map-23"));
		
//		KeyStoreEncrypter pkcs11 = KeyStoreEncrypter.getInstance(KeyStoreType.pkcs11, "pkcs11.jks", "123456");
//		pkcs11.writeEntries(tempEntries);
//		KeyStoreEncrypter pkcs11Again = KeyStoreEncrypter.getInstance(KeyStoreType.pkcs11, "pkcs11.jks", "123456");
//		assertEquals(tempEntries.get("alias-from-map-33"), pkcs11Again.readEntry("alias-from-map-33"));
		
		KeyStoreEncrypter pkcs12 = KeyStoreEncrypter.getInstance(KeyStoreType.pkcs12, tempDir.getTempDir() + "/pkcs12.jks", "123456");
		pkcs12.writeEntries(tempEntries);
		KeyStoreEncrypter pkcs12Again = KeyStoreEncrypter.getInstance(KeyStoreType.pkcs12, tempDir.getTempDir() + "/pkcs12.jks", "123456");
		assertEquals(tempEntries.get("alias-from-map-43"), pkcs12Again.readEntry("alias-from-map-43"));
	}

}
