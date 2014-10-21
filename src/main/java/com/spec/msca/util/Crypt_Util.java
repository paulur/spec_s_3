package com.spec.msca.util;

import com.spec.msca.crypto.CONST;

public class Crypt_Util {

	static public boolean isAsymmeticCipher(String cipherName) {		
		return cipherName.equals(CONST.RSA_CIPHER);
	}

}
