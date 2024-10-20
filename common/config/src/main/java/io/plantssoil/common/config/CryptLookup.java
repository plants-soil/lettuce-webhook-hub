package io.plantssoil.common.config;

import org.apache.commons.lang.text.StrLookup;

import io.plantssoil.common.security.AesEncrypter;

/**
 * Allow users to encrypted their configuration value
 * 
 * @author danialdy
 *
 */
public class CryptLookup extends StrLookup {

	/**
	 * @see org.apache.commons.lang.text.StrLookup#lookup(java.lang.String)
	 */
	@Override
	public String lookup(String key) {
		String cval = "${crypt:" + key + "}";
		System.out.println(key);
		return AesEncrypter.getInstance().decrypt(cval);
	}

}
