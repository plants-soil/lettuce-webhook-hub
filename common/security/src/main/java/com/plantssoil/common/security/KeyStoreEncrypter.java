package com.plantssoil.common.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.spec.SecretKeySpec;

/**
 * Utility to write (alias and plainText) into Java KeyStore with password
 * 
 * @author danialdy
 *
 */
public class KeyStoreEncrypter {
	/**
	 * KeyStore Type enumeration
	 * 
	 * @author danialdy
	 * @see KeyStore#getInstance(String)
	 *
	 */
	public static enum KeyStoreType {
		jceks/*, jks, dks, pkcs11*/, pkcs12
	}
	
    private static final String DEFAULT_PASSWORD = "a3c1ff21-29bd2e6";
	private static Map<String, KeyStoreEncrypter> instances;
	private KeyStore jks;
	private char[] password;
	private String file;
	private boolean loaded = false;
	
	private KeyStoreEncrypter(KeyStoreType keystoreType, String file, String password) {
		try {
			this.file = file;
			this.password = password == null ? DEFAULT_PASSWORD.toCharArray() : password.toCharArray();
			this.jks = KeyStore.getInstance(keystoreType.toString());
		} catch (KeyStoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * @param keystoreType KeyStore Type enumeration, should match the type when keystore file created
	 * @param file KeyStrore file (with full path) which stores all alias and values
	 * @param password The password to open KeyStore file
	 * @return Instance of current utility
	 */
	public static KeyStoreEncrypter getInstance(KeyStoreType keystoreType, String file, String password) {
		KeyStoreEncrypter instance = null;
		String instanceKey = keystoreType + "$$$" + file;
		if (instances == null) {
			synchronized (KeyStoreEncrypter.class) {
				instances = new ConcurrentHashMap<>();
				instance = new KeyStoreEncrypter(keystoreType, file, password);
				instances.put(instanceKey, instance);
			}
		}
		else {
			instance = instances.get(instanceKey);
			if (instance == null) {
				synchronized (KeyStoreEncrypter.class) {
					instance = new KeyStoreEncrypter(keystoreType, file, password);
					instances.put(instanceKey, instance);
				}
			}
		}
		return instance;
	}

    /**
     * Returns a {@link SecretKeySpec} given a secret key.
     * @param secretKey
     */
	private SecretKeySpec getKeySpecFromCache(String secret) {
		byte[] ivraw = secret.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(ivraw, "AES");
		return skeySpec;
	}
	
	/**
	 * write an alias with the plainText as value into the KeyStore file
	 * @param alias The alias (name)
	 * @param plainText The value
	 */
	public void writeEntry(String alias, String plainText) {
		loadKeystoreFile();
		
		KeyStore.ProtectionParameter pwdParameter = new KeyStore.PasswordProtection(this.password);
		try (OutputStream stream = new FileOutputStream(file)) {
			synchronized ("KeyStoreEncrypter.writeEntry") {
				KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(getKeySpecFromCache(plainText));
				this.jks.setEntry(alias, secret, pwdParameter);
				this.jks.store(stream, this.password);
				stream.flush();
			}
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * write the map entries (entry key as the alias, entry value as the plain text) into the KeyStore file
	 * @param aliasPlainTexts the map need write into KeyStore
	 */
	public void writeEntries(Map<String, String> aliasPlainTexts) {
		loadKeystoreFile();
		
		KeyStore.ProtectionParameter pwdParameter = new KeyStore.PasswordProtection(this.password);
		try (OutputStream stream = new FileOutputStream(file)) {
			synchronized ("KeyStoreEncrypter.writeEntry") {
				for (Entry<String, String> entry : aliasPlainTexts.entrySet()) {
					KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(getKeySpecFromCache(entry.getValue()));
					this.jks.setEntry(entry.getKey(), secret, pwdParameter);
				}
				this.jks.store(stream, this.password);
				stream.flush();
			}
		} catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	private void loadKeystoreFile() {
		if (this.loaded) {
			return;
		}
		
		synchronized (this) {
			if (new File(this.file).exists()) {
				try (InputStream input = new FileInputStream(file)) {
					this.jks.load(input, password);
					this.loaded = true;
				} catch (NoSuchAlgorithmException | CertificateException | IOException e) {
					throw new RuntimeException(e);
				}
			}
			else {
				try {
					this.jks.load(null, password);
					this.loaded = true;
				} catch (NoSuchAlgorithmException | CertificateException | IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	/**
	 * Read all KeyStore entries into a Map
	 * @return map to keep the KeyStore alias & value
	 */
	public Map<String, String> readEntries() {
		loadKeystoreFile();
		
		Map<String, String> map = new LinkedHashMap<String, String>();
		Enumeration<String> aliases;
		try {
			aliases = this.jks.aliases();
			while (aliases.hasMoreElements()) {
				String alias = aliases.nextElement();
				Key key = this.jks.getKey(alias, this.password);
				if (key != null) {
					map.put(alias, new String(key.getEncoded()));
				}
			}
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
			throw new RuntimeException(e);
		}
		return map;
	}
	
	/**
	 * Read one alias value from the KeyStore
	 * @param alias The name need to read
	 * @return The value of the alias
	 */
	public String readEntry(String alias) {
		loadKeystoreFile();
		try {
			Key key = this.jks.getKey(alias, this.password);
			if (key != null) {
				return new String(key.getEncoded());
			}
			return null;
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
//	
//	public static void main(String[] args) {
//		KeyStoreEncrypter.getInstance(KeyStoreType.pkcs12, "D:\\workspace\\skyve8\\git\\keystore.jks", "password").encryptAndSave("test-alias", "test-value");
//		KeyStoreEncrypter.getInstance(KeyStoreType.pkcs12, "D:\\workspace\\skyve8\\git\\keystore.jks", "password").decrypt();
//	}
}
