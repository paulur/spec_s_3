/*
* FindBugs - Find bugs in Java programs
* Copyright (C) 2004,2005 University of Maryland
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package com.spec.msca.crypto;

import java.util.ArrayList;

import edu.umd.cs.findbugs.BugAccumulator;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.ba.CFGBuilderException;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.DataflowAnalysisException;
import edu.umd.cs.findbugs.ba.Location;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;




import com.spec.msca.BaseDetector;
import com.spec.msca.util.ByteCode_Util;
import com.spec.msca.util.Crypt_Util;

/**
* This class verify encryption algorithm using FindBugs library.
* Java supports the following symmetric ciphers
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
	the format is cipher/mode/padding schema (keylength). For example, AES/CBC/NoPadding (128) refers to AES in CBC mode without padding with 128 bit key.
	Only AES and DESede (i.e., triple-des) are NIST certificated cipher.
	Both AES and DESede are block ciphers, which needs padding.
	ECB mode is not recommended.
	RSA is asymmetric cipher, which is not recommended for encrypting application data.
	A asymmetric cipher is mostly used for encrypting a session key, which is used as the key of symmetric cipher.
* @author Paul Li
*/
public class CipherInstanceVerifier extends BaseDetector {
    BugReporter bugReporter;
    BugAccumulator bugAccumulator;
    
    Method method;
    ClassContext classContext;
  
    public CipherInstanceVerifier(BugReporter bugReporter) {
// _debug.println("CipherInstanceVerifier initialized.");
        this.bugReporter 	= bugReporter;
        this.bugAccumulator = new BugAccumulator(bugReporter);
    }
    
	/**
	* Verify Cipher.getInstance("AES/CBC/PKCS5Padding");

	* @param classContext
	* @param method
	* @throws DataflowAnalysisException
	* @throws CFGBuilderException
	*/
	private void verifyCipherInstance(InvokeInstruction invoke, Location location, InstructionHandle handle,
	     ConstantPoolGen cpg, MethodGen methodGen, JavaClass javaClass )
	    {
	     String cipherInstanceValue = ByteCode_Util.getPreviousInstructionParameter(cpg, handle, true);
	     ArrayList<Integer> verificationCodeList = checkCipherModeVerificationCode(cipherInstanceValue);
	    
	     if (verificationCodeList.size() == 0) return;
	    
	     BugInstance bug;
	     if (verificationCodeList.get(0) > 0 ) //symmetric cipher bug
	     {
	    	 bug = new BugInstance(this, CONST.BUG_CIPHER_INSTANCE, HIGH_PRIORITY);
	     }else if (verificationCodeList.get(0) == 0 )	// asymmetric cipher is found
	     {
	    	 bug = new BugInstance(this, CONST.BUG_ASYMMETRIC_CIPHER_IN_APP, HIGH_PRIORITY);
	     }else{
	    	 bug = new BugInstance(this, CONST.WARNING_CIPHER_DISCOVERED, HIGH_PRIORITY);
	     }
	        
	     bug.addClassAndMethod(methodGen, javaClass.getSourceFileName());
	     accumulateBug(bug, methodGen, javaClass, location);
	}
	    
	/*
	* Code Reference
	*
	* 0, if asymmetric cipher
	* 1, non-standard symmetric cipher (i.e., not AES or tri-des)
	* 2, if not correct mode (i.e., not "CBC" or "CTR"
	* 3, if not correct padding
	* -1, correct cipher
	*
	*/
	private ArrayList<Integer> checkCipherModeVerificationCode(String cipherInstanceValue) {
		ArrayList<Integer> codeList	= new ArrayList<Integer>();
		String[] cipherInstance = ByteCode_Util.parseCipherInstanceValue(cipherInstanceValue);
		if (Crypt_Util.isAsymmeticCipher(cipherInstance[0]))
		{
			codeList.add(0);
		}
		else
		{
			int wrongCipherflag = 0;
			if (!cipherInstance[0].equals(CONST.AES_CIPHER) && !cipherInstance[0].equals(CONST.TRIPLE_DES_CIPHER)){
				codeList.add(1);
				wrongCipherflag = 1;
			}
				
			if (!cipherInstance[1].equals(CONST.CBC_MODE) && !cipherInstance[1].equals(CONST.CTR)){
				codeList.add(2);
				wrongCipherflag = 1;
			}
			
			if (cipherInstance.equals(CONST.NO_PADDING_SCHEME)){
				codeList.add(3);
				wrongCipherflag = 1;
			}
			
			if (wrongCipherflag == 0) codeList.add(-1);
		}
		
		return codeList;
	}

	private boolean isCipherGetInstance(InvokeInstruction ins, ConstantPoolGen cpg){
		return ByteCode_Util.isSingautredMethod(ins, cpg, 
										"javax.crypto.Cipher", 
										"getInstance", 
										"(Ljava/lang/String;)Ljavax/crypto/Cipher;");
	}
   
	@Override
	public BugReporter getBugReporter() {
		return this.bugReporter;
	}

	@Override
	public BugAccumulator getBugAccumulator() {
		return this.bugAccumulator;
	}

	@Override
	public void setMethod(Method method) {
		this.method = method;
	}

	@Override
	public void setClassContext(ClassContext classContext) {
		this.classContext = classContext;
	}

	@Override
	public ClassContext getClassContext() {
		return this.classContext;
	}

	@Override
	public boolean isTargetInstruction(InvokeInstruction invoke, ConstantPoolGen cpg) {
		return this.isCipherGetInstance(invoke, cpg);
	}

	@Override
	public void verifyTarget(InvokeInstruction invoke, Location location,
			InstructionHandle handle, ConstantPoolGen cpg, MethodGen methodGen,
			JavaClass javaClass) {
		this.verifyCipherInstance(invoke, location, handle, cpg, methodGen, javaClass);		
	}
}