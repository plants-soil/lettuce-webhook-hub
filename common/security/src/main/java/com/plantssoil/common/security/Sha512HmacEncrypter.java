package com.plantssoil.common.security;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Sha512HmacEncrypter {
    private Map<String, SecretKeySpec> keySpecs = new ConcurrentHashMap<>();
    private static volatile Sha512HmacEncrypter instance;
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String SECRET_PREFIX = "whsec_";

    private Sha512HmacEncrypter() {
    }

    /**
     * Get the instance of this utility
     * 
     * @return singleton instance
     */
    public static Sha512HmacEncrypter getInstance() {
        if (instance == null) {
            synchronized (Sha512HmacEncrypter.class) {
                if (instance == null) {
                    instance = new Sha512HmacEncrypter();
                }
            }
        }
        return instance;
    }

    public String encrypt(String secretKey, String plainText) {
        if (plainText == null) {
            return null;
        }
        if (secretKey == null || secretKey.strip().length() == 0) {
            throw new RuntimeException("Secrete Key can't be null!");
        }
        String sec = secretKey;
        if (sec.startsWith(SECRET_PREFIX)) {
            sec = sec.substring(SECRET_PREFIX.length());
        }
        byte[] key = Base64.getDecoder().decode(sec);
        try {
            Mac sha512Hmac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec keySpec = getKeySpecFromCache(secretKey, key);
            sha512Hmac.init(keySpec);
            byte[] macData = sha512Hmac.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            String encrypted = Base64.getEncoder().encodeToString(macData);
            return encrypted;
        } catch (NoSuchAlgorithmException e) {
            throw new com.plantssoil.common.security.exception.SecurityException(
                    com.plantssoil.common.security.exception.SecurityException.BUSINESS_EXCEPTION_CODE_10001, e);
        } catch (InvalidKeyException e) {
            throw new com.plantssoil.common.security.exception.SecurityException(
                    com.plantssoil.common.security.exception.SecurityException.BUSINESS_EXCEPTION_CODE_10002, e);
        }
    }

    private SecretKeySpec getKeySpecFromCache(String secretKey, byte[] key) {
        if (!keySpecs.containsKey(secretKey)) {
            SecretKeySpec skeySpec = new SecretKeySpec(key, HMAC_SHA256);
            keySpecs.put(secretKey, skeySpec);
        }
        return keySpecs.get(secretKey);
    }

}
