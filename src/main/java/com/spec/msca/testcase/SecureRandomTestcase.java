package com.spec.msca.testcase;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
/**
 * ciphers supported by java security API 
	AES/CBC/NoPadding (128)
	AES/CBC/PKCS5Padding (128)
	AES/ECB/NoPadding (128)
	AES/ECB/PKCS5Padding (128)
	DES/CBC/NoPadding (56)
	DES/CBC/PKCS5Padding (56)
	DES/ECB/NoPadding (56)
	DES/ECB/PKCS5Padding (56)
	DESede/CBC/NoPadding (168)
	DESede/CBC/PKCS5Padding (168)
	DESede/ECB/NoPadding (168)
	DESede/ECB/PKCS5Padding (168)
	RSA/ECB/PKCS1Padding (1024, 2048)
	RSA/ECB/OAEPWithSHA-1AndMGF1Padding (1024, 2048)
	RSA/ECB/OAEPWithSHA-256AndMGF1Padding (1024, 2048)
*/
public class SecureRandomTestcase {
	String key 	= 
//					Util.key;
					"1234567890123457";
	String iv	= 
//					Util.iv;
					"0000111100001111";
	
	public static void main(String args[]) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
		SecureRandomTestcase cvt = new SecureRandomTestcase();	
		String _aString = "abc";	
		String _bString	= "abdedddddddddddddddddddddddddddddddddddddd";

		cvt.doWrongSecureRandom(_aString);
	}
	
	public void doWrongSecureRandom(String input) 
		throws NoSuchAlgorithmException, NoSuchPaddingException, 
				InvalidKeyException, InvalidAlgorithmParameterException, 
				IllegalBlockSizeException, BadPaddingException
	{
		byte[] inputBytes 	= input.getBytes();
		byte[] keyBytes		= key.getBytes();
		byte[] ivBytes		= iv.getBytes();
		
		//Initialize cipher
		// wrap key data in Key/IV specs to pass to cipher
		SecretKeySpec keySpec 	= new SecretKeySpec(keyBytes, "AES");
		
		SecureRandom sr = SecureRandom.getInstance("SHA1");
		sr.generateSeed(100);
		
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");		
		cipher.init(Cipher.ENCRYPT_MODE, keySpec);
				
		byte[] chiperTextByte 	= cipher.doFinal(inputBytes);
		String cihperString		= new String(Base64.encodeBase64(chiperTextByte));
		
		cipher.init(Cipher.DECRYPT_MODE, keySpec);
		byte[] decryptedTextByte	= cipher.doFinal(Base64.decodeBase64(cihperString));
		String decyptedString		= new String(decryptedTextByte);
		
		System.out.println("cleartext:[" + input + "]\tcihperString: [" + cihperString + "]\tdecryptedString: [" + decyptedString + "]");		
	}
}
