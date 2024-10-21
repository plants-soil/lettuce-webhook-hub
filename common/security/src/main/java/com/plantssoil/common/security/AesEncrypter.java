package com.plantssoil.common.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * AES encrypt / decrypt utility
 * 
 * @author danialdy
 *
 */
public class AesEncrypter {
    private static final String secretKey = "d2d0aa10-18ce1f5";
    private Map<String, SecretKeySpec> keySpecs = new ConcurrentHashMap<>();
    private static AesEncrypter instance;

    private AesEncrypter() {
    }
    
    /**
     * Get the instance of this utility
     * @return singleton instance
     */
    public static AesEncrypter getInstance() {
    	if (instance == null) {
    		synchronized(secretKey) {
    			instance = new AesEncrypter();
    		}
    	}
    	return instance;
    }

    /**
     * Encrypt a plain text with the default secret key
     * 
     * @param plainText the plain text to be encrypted
     * 
     * @return encrypted text
     */
    public String encrypt(String plainText) {
        return encrypt(secretKey, plainText);
    }
    
    /**
     * Encrypt a plain text with the specified secret key
     * @param secretKey the secret key used to encrypt
     * @param plainText the plain text to be encrypted
     * @return encrypted text
     */
    public String encrypt(String secretKey, String plainText) {
        if (plainText == null) {
            return null;
        } else if (plainText.startsWith("${crypt:")) {
             // avoid encrypt again on an encrypted text
             return plainText;
        }
        
        byte[] encrypted;
        Cipher cipher;
        try {
            SecretKeySpec skeySpec = getKeySpecFromCache(secretKey);
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        try {
            encrypted = cipher.doFinal(plainText.getBytes());
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
        return "${crypt:" + new String(Base64.encodeBase64(encrypted)) + "}"; //$NON-NLS-1$
    }

    /**
     * Returns a {@link SecretKeySpec} given a secret key.
     * @param secretKey
     */
    private SecretKeySpec getKeySpecFromCache(String secretKey) {
        if (!keySpecs.containsKey(secretKey)) {
            byte[] ivraw = secretKey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(ivraw, "AES");
            keySpecs.put(secretKey, skeySpec);
        }
        return keySpecs.get(secretKey);
    }

    /**
     * Decrypt an encrypted text with the default secret key
     * @param encryptedText the encrypted text
     * @return the plain text decrypted
     */
    public String decrypt(String encryptedText) {
        return decrypt(secretKey, encryptedText);
    }
    
    
    /**
     * Decrypt an encrypted text with the specified secret key
     * @param encryptedText the encrypted text
     * @param secretKey the secret key
     * @return the plain text decrypted
     */
    public String decrypt(String secretKey, String encryptedText) {
        if (encryptedText == null) {
            return null;
        }
        if (encryptedText.startsWith("${crypt:")) {
            byte[] decoded = Base64.decodeBase64(encryptedText.substring(8, encryptedText.length() - 1));
            Cipher cipher;
            try {
                SecretKeySpec skeySpec = getKeySpecFromCache(secretKey);
                cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }
            try {
                return new String(cipher.doFinal(decoded));
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return encryptedText;
        }
    }
    

    /**
     * Support encrypt / decrypt from the command line, this is the main entry point
     * @param args
     */
    public static void main(String [] args) {
        if (args.length != 2) {
            printUsage();
            return;
        }
        String cmd = args[0];
        String input = args[1];
        if ("encrypt".equals(cmd)) {
        	String encrypted = AesEncrypter.getInstance().encrypt(input);
            System.out.println(encrypted.substring(8, encrypted.length() - 1));
        } else if ("decrypt".equals(cmd)) {
            System.out.println(AesEncrypter.getInstance().decrypt("${crypt:" + input + "}"));
        } else {
            printUsage();
        }

    }

    @SuppressWarnings("nls")
    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("\tAesEncrypter encrypt|decrypt \"input\"\n------");
        System.out.println("Argument 1: the command, either 'encrypt' or 'decrypt'");
        System.out.println("Argument 2: the text to encrypt or decrypt (use quotes if the input contains spaces)");
        System.out.println("");
    }
}
