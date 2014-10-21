package com.spec.msca.crypto;

import com.spec.msca.util.ByteCode_Util;
import com.spec.util._debug;

public class PackageTester {
	
	public static void main(String args[]){
		testParseCihperInstance();
	}
	
	static void testParseCihperInstance(){
		String cipherInstanceValue = "c/m/p";
		String[] cipherInstance	= ByteCode_Util.parseCipherInstanceValue(cipherInstanceValue);
		
		_debug.printlnStringArray(cipherInstance);
	}
	
}
