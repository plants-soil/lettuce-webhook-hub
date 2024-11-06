package com.plantssoil.common.config.lookup;

import org.apache.commons.lang.text.StrLookup;

import com.plantssoil.common.security.AesEncrypter;

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
		return AesEncrypter.getInstance().decrypt(cval);
	}

}
