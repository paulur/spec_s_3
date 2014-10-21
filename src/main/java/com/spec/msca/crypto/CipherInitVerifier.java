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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.spec.msca.crypto;

import java.util.Iterator;

import edu.umd.cs.findbugs.BugAccumulator;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.SourceLineAnnotation;
import edu.umd.cs.findbugs.ba.CFG;
import edu.umd.cs.findbugs.ba.CFGBuilderException;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.DataflowAnalysisException;
import edu.umd.cs.findbugs.ba.Location;
import edu.umd.cs.findbugs.ba.constant.ConstantDataflow;
import edu.umd.cs.findbugs.ba.constant.ConstantFrame;

import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

import com.spec.msca.util.ByteCode_Util;
import com.spec.util._debug;

/**
 * This class is POC frame.
 *
 * @author Paul Li
 */
public class CipherInitVerifier implements Detector {
    BugReporter bugReporter;
    BugAccumulator bugAccumulator;
    
    Method method;
    ClassContext classContext;
  
    public CipherInitVerifier(BugReporter bugReporter) {
//    	_debug.println(this.getClass().getName() + "\tinitialized.");
        this.bugReporter 	= bugReporter;
        this.bugAccumulator = new BugAccumulator(bugReporter);
    }

    public void visitClassContext(ClassContext classContext) {
        JavaClass javaClass = classContext.getJavaClass();
        
        ConstantPool	cPool	= javaClass.getConstantPool();
//        int numConstant	= cPool.getLength();
//        _debug.println(numConstant + "constants");
//        for (int i = 0; i < numConstant - 2; i++){
////        	_debug.println(i);
//        	Constant c = cPool.getConstant(i);
//        	if (c != null)        	
//        		_debug.println( i + "\tConstant: " + c.toString() );
//        	else
//        		_debug.println( i + "\tConstant: NULL" );
//        }
        /*
        Method[] methodList = javaClass.getMethods();

        for (Method method : methodList) {
            MethodGen methodGen = classContext.getMethodGen(method);
            if (methodGen == null)
                continue;
			
            try {
                analyzeMethod(classContext, method);
            } catch (DataflowAnalysisException e) {
                bugReporter.logError(
                        "EncryptVerifier caught exception while analyzing " + 
                        classContext.getFullyQualifiedMethodName(method),
                        e);
            } catch (CFGBuilderException e) {
                bugReporter.logError(
                        "EncryptVerifier caught exception while analyzing " + 
                        classContext.getFullyQualifiedMethodName(method),
                        e);
            } catch (RuntimeException e) {
                bugReporter.logError(
                        "EncryptVerifier caught exception while analyzing " + 
                        classContext.getFullyQualifiedMethodName(method),
                        e);
            }
        }
        */
    }

   
    protected void analyzeMethod(ClassContext classContext, Method method) 
    				throws DataflowAnalysisException, CFGBuilderException  
    {
//    	_debug.println("analyze method init for [" + method.getName() + "]");
        JavaClass javaClass = classContext.getJavaClass();
        this.method 		= method;
        this.classContext 	= classContext;
        MethodGen methodGen = classContext.getMethodGen(method);
        
        if (methodGen == null) return;
        
        ConstantPoolGen cpg 	= methodGen.getConstantPool();
        
        
        
        CFG cfg = classContext.getCFG(method);
        
        
        
        ConstantDataflow dataflow 	= classContext.getConstantDataflow(method);
//        _debug.println("===dataflow: \n" + dataflow.toString());
        
        for (Iterator<Location> iLoc = cfg.locationIterator(); iLoc.hasNext();)
        {
        	Location location	= iLoc.next();
        	InstructionHandle	
        				handle	= location.getHandle();
        	Instruction ins 	= handle.getInstruction();
        	
            if (!isCipherInit(ins, cpg)) continue;
            
            _debug.println("cipher.init discovered.");
            String methodSig	= ((INVOKEVIRTUAL)ins).getSignature(cpg);
            Type[] types 		= Type.getArgumentTypes(methodSig);
            int argSize			= types.length;
            _debug.println("method signature: " + methodSig + "\n num arg: " + argSize + "\n types:");
//            for (int i=0; i<types.length; i++) _debug.println(types[i].toString());
//            InvokeInstruction invoke = (InvokeInstruction) ins;
            verifyCipherInit((InvokeInstruction)ins, handle, location, methodGen, javaClass, dataflow, cpg);
            //need have at least 3 arguments
    		/**
    		 * 
    		 * For a symmetric cipher, the number of parameters needs be either three or four
    		 * -Four parameters
    		 * void	init(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random)
    		 * void	init(int opmode, Key key, AlgorithmParameters params, SecureRandom random)
    		 * 
    		 * -Three parameters
    		 * void	init(int opmode, Key key, AlgorithmParameters params)
    		 * void	init(int opmode, Key key, AlgorithmParameterSpec params)
    		 * void	init(int opmode, Key key, SecureRandom random)
    		 * 
    		 * If having only two parameters, the wrong cipher (i.e., ECB) is used.
    		 */
            if (argSize < 3){
               	BugInstance bug = new BugInstance(this, CONST.BUG_CIPHER_INSTANCE, HIGH_PRIORITY);
            	bug.addClassAndMethod(methodGen, javaClass.getSourceFileName());
            	SourceLineAnnotation sla = SourceLineAnnotation.fromVisitedInstruction
            														(	
            															classContext, 
        									        					methodGen, 
        									        					javaClass.getSourceFileName(), 
        									        					location.getHandle()
            														);
            	bugAccumulator.accumulateBug(bug, sla); 
            }
            
            if (argSize == 3){
            	String arg3Type = types[2].toString();
            	_debug.println("argSize: " + argSize + "\targ3Type: " + arg3Type);
            	 
            	if (
            		! arg3Type.endsWith("java.security.spec.AlgorithmParameterSpec") 
            			|| 
            		! arg3Type.endsWith("java.security.spec.AlgorithmParameters")
            			||
            		! arg3Type.endsWith("java.security.SecureRandom")
            	)      
            	{
            		_debug.println("arg3Type NOT endsWith required type.");
            		BugInstance bug = new BugInstance(this, CONST.BUG_CIPHER_INSTANCE, HIGH_PRIORITY);
                	bug.addClassAndMethod(methodGen, javaClass.getSourceFileName());
                	SourceLineAnnotation sla = SourceLineAnnotation.fromVisitedInstruction
                														(	
                															classContext, 
            									        					methodGen, 
            									        					javaClass.getSourceFileName(), 
            									        					location.getHandle()
                														);
                	bugAccumulator.accumulateBug(bug, sla); 
            	}else{
            		_debug.println("arg-3 type: " + arg3Type);
            	}
            }
            
//            if (argSize == 4){
//            	if (!types[2].toString().endsWith("java/security/spec/AlgorithmParameterSpec") 
//            			&& 
//            		!types[2].toString().endsWith("java/security/spec/AlgorithmParameters")
//            	)
//            	{
//            		BugInstance bug = new BugInstance(this, CONST.BUG_PATTERN_CIPHER_INSTANCE, HIGH_PRIORITY);
//                	bug.addClassAndMethod(methodGen, javaClass.getSourceFileName());
//                	SourceLineAnnotation sla = SourceLineAnnotation.fromVisitedInstruction
//                														(	
//                															classContext, 
//            									        					methodGen, 
//            									        					javaClass.getSourceFileName(), 
//            									        					location.getHandle()
//                														);
//                	bugAccumulator.accumulateBug(bug, sla); 
//            	}else{
//            		_debug.println("arg-3 type: " + types[2]);
//            	}
//            	
//            	if (!types[3].toString().endsWith("Ljava/security/SecureRandom;")){
//            		BugInstance bug = new BugInstance(this, CONST.BUG_PATTERN_CIPHER_INSTANCE, HIGH_PRIORITY);
//                	bug.addClassAndMethod(methodGen, javaClass.getSourceFileName());
//                	SourceLineAnnotation sla = SourceLineAnnotation.fromVisitedInstruction
//                														(	
//                															classContext, 
//            									        					methodGen, 
//            									        					javaClass.getSourceFileName(), 
//            									        					location.getHandle()
//                														);
//                	bugAccumulator.accumulateBug(bug, sla); 
//            	}else{
//            		_debug.println("arg-3 type: " + "\narg-4 type: " + types[3]);
//            	}
//            }
            
        }
        
        bugAccumulator.reportAccumulatedBugs();
	}
    
 

    /**
     * Verify cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
     * 	iv 
     * 	-	needs be random value, cannot be in the constant pool
     *  -	needs be secure random value
     *  -	needs be long as the first block
     *  key 
     *  -	needs be loaded in at runtime; cannot be in the constant pool
     *  -	key size needs be long, depending on the cipher AES 128; triple DES 168
     *  
     * @param invoke
     * @param location
     * @param handle
     * @param cpg
     * @param methodGen
     * @param javaClass
     * @throws DataflowAnalysisException 
     */
	private void verifyCipherInit(InvokeInstruction invoke,  InstructionHandle	handle,
									Location location, MethodGen methodGen, JavaClass javaClass,
									ConstantDataflow dataflow, ConstantPoolGen cpg) 
				throws DataflowAnalysisException 
	{
		_debug.println("verifyCipherInit...");
		/*
		ConstantFrame frame = dataflow.getFactAtLocation(location);
		_debug.println("frame: " + frame.toString());
        int numArguments 	= frame.getNumArguments(invoke, cpg);
        
        if (numArguments < 3) {
        	BugInstance bug = new BugInstance(this, CONST.BUG_PATTERN_CIPHER_INSTANCE, HIGH_PRIORITY);
        	bug.addClassAndMethod(methodGen, javaClass.getSourceFileName());
        	SourceLineAnnotation sla = SourceLineAnnotation.fromVisitedInstruction
        														(	
        															classContext, 
    									        					methodGen, 
    									        					javaClass.getSourceFileName(), 
    									        					location.getHandle()
        														);
        }
        
        
        
        
        _debug.println("numArguments: " + numArguments);
        for (int i=0; i<numArguments; i++){
        	_debug.println("arg:" + frame.getArgument(invoke, cpg, i, numArguments));
        }
        
//        Constant iv 	= frame.getStackValue(numArguments - 1);
//        Constant key	= frame.getStackValue(numArguments - 2);
//        
//        _debug.println("iv: " + iv.toString() );
//        _debug.println("key: " + key.toString() );
        
        
        if (iv.isConstant()){
        	_debug.println("iv: [is constant]");
        }
        
        if (key.isConstant()){
        	_debug.println("key: [is constant]");
        }
        */
		
        //TODO verify SecretKeySpec; IvParameterSpec.
        //can use the following way for two-step LDC instruction too
		//get pre-instruction: ivSpec
		/**
		 * 
		 * For a symmetric cipher, the number of parameters needs be either three or four
		 * -Four parameters
		 * void	init(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random)
		 * void	init(int opmode, Key key, AlgorithmParameters params, SecureRandom random)
		 * 
		 * -Three parameters
		 * void	init(int opmode, Key key, AlgorithmParameters params)
		 * void	init(int opmode, Key key, AlgorithmParameterSpec params)
		 * void	init(int opmode, Key key, SecureRandom random)
		 * 
		 * If having only two parameters, the wrong cipher (i.e., ECB) is used.
		 */
		ConstantFrame frame = dataflow.getFactAtLocation(location);
//		_debug.println("frame: " + frame.toString());
        int numArguments 	= frame.getNumArguments(invoke, cpg);
        
        if (numArguments < 3) {
        	BugInstance bug = new BugInstance(this, CONST.BUG_CIPHER_INSTANCE, HIGH_PRIORITY);
        	bug.addClassAndMethod(methodGen, javaClass.getSourceFileName());
        	SourceLineAnnotation sla = SourceLineAnnotation.fromVisitedInstruction
        														(	
        															classContext, 
    									        					methodGen, 
    									        					javaClass.getSourceFileName(), 
    									        					location.getHandle()
        														);
        	bugAccumulator.accumulateBug(bug, sla); 
        }
        
		
		InstructionHandle preHandle		= ByteCode_Util.getPreviousInstruction(handle, true);
		Instruction preIns				= preHandle.getInstruction();
		String preName = preIns.getName(), preClass = preIns.getClass().toString();
		String preParemter				= ByteCode_Util.getPreviousInstructionParameter(cpg, handle, true);
		//TRY get datatype flow... to make sure the last parameter is type of SecureRandom
		
//		_debug.println("preIns: " + preIns.toString() + "\tparameter: " + preParemter + "\tname: " + preName + "\tclass" + preClass);
		

		//get pre-pre-instruction: keySpec
		InstructionHandle pre2Handle	= ByteCode_Util.getPreviousInstruction(preHandle, true);
		Instruction pre2Ins				= pre2Handle.getInstruction();
		String pre2Paremter				= ByteCode_Util.getPreviousInstructionParameter(cpg, preHandle, true);
		_debug.println("pre2Ins: " + pre2Ins.toString() + "\tparameter: " + pre2Paremter);		
	}
     
    /**
     * detect cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
     * @param invoke
     * @param cpg
     * @return
     */
    private boolean isCipherInit(Instruction ins, ConstantPoolGen cpg) {
 		if (!(ins instanceof INVOKEVIRTUAL)){
 			return false;
 		}
 		
 		INVOKEVIRTUAL invoke 	= (INVOKEVIRTUAL) ins;      
 		String className		= invoke.getReferenceType(cpg).toString().trim();       
        String methodName 		= invoke.getMethodName(cpg).trim();
        String methodSignature 	= invoke.getSignature(cpg).trim();
 		
        if ( 	
        		className.equals("javax.crypto.Cipher") 
        		&& methodName.equals("init") 
        		&& methodSignature.equals("(ILjava/security/Key;Ljava/security/spec/AlgorithmParameterSpec;)V")
        	) 
        {        	
            return true;
        }
        return false;
 	}

       
	public void report() {
    }
}

// vim:ts=4
