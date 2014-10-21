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
public class HashTestcase {
	String key 	= 
//					Util.key;
					"1234567890123457";
	String iv	= 
//					Util.iv;
					"0000111100001111";
	
	public static void main(String args[]) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
		HashTestcase cvt = new HashTestcase();	
		String _aString = "abc";	
		String _bString	= "abdedddddddddddddddddddddddddddddddddddddd";
		cvt.doRightHash(_aString);
		cvt.doOutdatedHash(_aString);
	}
		
	private void doRightHash(String input) throws NoSuchAlgorithmException{
	    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");        
	    byte[] stringBytes	= input.getBytes();
	    byte[] stringDigest = sha256.digest(stringBytes);
	    String hashString 	= new String(Base64.encodeBase64(stringDigest));
	    
	    System.out.println("cleartext:[" + input + "]\thash: [" + hashString + "]");
	}	
	
	private void doOutdatedHash(String input) throws NoSuchAlgorithmException{
	    MessageDigest hash = MessageDigest.getInstance("MD5");        
	    byte[] stringBytes	= input.getBytes();
	    byte[] stringDigest = hash.digest(stringBytes);
	    String hashString 	= new String(Base64.encodeBase64(stringDigest));
	    
	    hash = MessageDigest.getInstance("s-MD5");        
	    System.out.println("cleartext:[" + input + "]\thash: [" + hashString + "]");
	}
}
