package com.spec.msca.util;

import java.util.StringTokenizer;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.NOP;


public class ByteCode_Util {
	
	static public String[] parseCipherInstanceValue(String cipherInstanceValue){
		String[] cihperInstance = new String[3];
		
		StringTokenizer st = new StringTokenizer(cipherInstanceValue, "/");
		cihperInstance[0]	= st.nextToken();
		cihperInstance[1]	= st.nextToken();
		cihperInstance[2]	= st.nextToken();
		
		return cihperInstance;
	}

	/*
     * 
     *  @return the value of the previous instruction if the previous instruction is LDC; else return null
     *  
     */
	static public String getPreviousInstructionParameter(ConstantPoolGen cpg, InstructionHandle handle, boolean skipNops){
 	   InstructionHandle preHandle	= getPreviousInstruction(handle, skipNops);	   
 	   Instruction preIns			= preHandle.getInstruction();
 	   
 	   if (preIns instanceof LDC) return (String)((LDC)preIns).getValue(cpg);
 	   else return null;
    }
    
     
    static public InstructionHandle getPreviousInstruction(InstructionHandle handle, boolean skipNops) {
         while (handle.getPrev() != null) {
             handle = handle.getPrev();
             Instruction prevIns = handle.getInstruction();
             if (!(prevIns instanceof NOP && skipNops)) {
                 return handle;
             }
         }
         return null;
     }
    
    
    /**
	* detect Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	* @param ins
	* @param cpg
	* @return
	*/
    static public boolean isSingautredMethod(InvokeInstruction ins, ConstantPoolGen cpg, 
    							String className, String methodName, String methodSignature) {
        if (!(ins instanceof INVOKESTATIC)) {
            return false;
        }

        INVOKESTATIC invoke = (INVOKESTATIC) ins;
        String cName	= invoke.getReferenceType(cpg).toString().trim();
        String mName = invoke.getMethodName(cpg).trim();
        String mSignature = invoke.getSignature(cpg).trim();

        if (
        		cName.equals(className)
        		&& mName.equals(methodName)
        		&& mSignature.equals(methodSignature)
         )
        {
            return true;
        }
        return false;
    }
}
