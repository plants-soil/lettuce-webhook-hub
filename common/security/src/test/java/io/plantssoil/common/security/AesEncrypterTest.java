package io.plantssoil.common.security;

import static org.junit.Assert.*;

import org.junit.Test;

public class AesEncrypterTest {
	private final static String PLAIN_TEXT = "THIS IS THE TEST STRING TO ENCRYPT";
	private final static String TEST_SECRET_KEY = "TEST_SECRET_KEY+15_BYTES";
	private final static String ENCRYPTED_DEFAULT = "${crypt:z6s4EY6ZrkIfwt0nnKZRM4efUVNbANZSjME6b4qak03Xm7n77X9MROo61zKJHEx2}";
	private final static String ENCRYPTED_WITH_KEY = "${crypt:9LMbjtj9jO1B0bkhQoi1ivvt+alO+l4VNzhlIxrOU3ftFaOfYhYP+xHZzl10DGWR}";

	@Test
	public void testEncryptString() {
		String encrypted = AesEncrypter.getInstance().encrypt(PLAIN_TEXT);
		assertEquals(ENCRYPTED_DEFAULT, encrypted);
	}

	@Test
	public void testEncryptStringString() {
		String encrypted = AesEncrypter.getInstance().encrypt(TEST_SECRET_KEY, PLAIN_TEXT);
		assertEquals(ENCRYPTED_WITH_KEY, encrypted);
	}

	@Test
	public void testDecryptString() {
		String decrypted = AesEncrypter.getInstance().decrypt(ENCRYPTED_DEFAULT);
		assertEquals(PLAIN_TEXT, decrypted);
	}

	@Test
	public void testDecryptStringString() {
		String decrypted = AesEncrypter.getInstance().decrypt(TEST_SECRET_KEY, ENCRYPTED_WITH_KEY);
		assertEquals(PLAIN_TEXT, decrypted);
	}

}
