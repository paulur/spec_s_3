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


import edu.umd.cs.findbugs.BugAccumulator;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.Location;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;




import com.spec.msca.BaseDetector;
import com.spec.msca.util.ByteCode_Util;

/**
 * This class verify encryption algorithm using FindBugs library.
 *
 * @author Paul Li
 */
public class SecureRandomInstanceVerifier extends BaseDetector{
    BugReporter bugReporter;
    BugAccumulator bugAccumulator;
    
    Method method;
    ClassContext classContext;
  
    public SecureRandomInstanceVerifier(BugReporter bugReporter) {
        this.bugReporter 	= bugReporter;
        this.bugAccumulator = new BugAccumulator(bugReporter);
    }  
   
	private boolean isSecureRandomGetInstance(InvokeInstruction ins, ConstantPoolGen cpg) {
		return ByteCode_Util.isSingautredMethod(ins, cpg, "java.security.SecureRandom", "getInstance", "(Ljava/lang/String;)Ljava/security/SecureRandom;");		
	}
      
	public void report() {}

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
		this.method	= method;		
	}

	@Override
	public void setClassContext(ClassContext classContext) {
		this.classContext	= classContext;
	}
	
	@Override
	public ClassContext getClassContext() {
		return this.classContext;
	}

	@Override
	public boolean isTargetInstruction(InvokeInstruction invoke, ConstantPoolGen cpg) {
		return this.isSecureRandomGetInstance(invoke, cpg);
	}

	@Override
	public void verifyTarget(InvokeInstruction invoke, Location location,
			InstructionHandle handle, ConstantPoolGen cpg, MethodGen methodGen,
			JavaClass javaClass) 
	{
		BugInstance bug = this.verifySecureRandomInstance(invoke, location, handle, cpg, methodGen, javaClass);
		accumulateBug(bug, methodGen, javaClass, location);
	}
	
	private BugInstance verifySecureRandomInstance(InvokeInstruction invoke, Location location, InstructionHandle handle, 
			ConstantPoolGen cpg, MethodGen methodGen, JavaClass javaClass)
	{
		return new BugInstance(this, CONST.BUG_SECURE_RANDOME_INSTANCE, HIGH_PRIORITY); 
		
//		 bug.addClassAndMethod(methodGen, javaClass.getSourceFileName());
//	     SourceLineAnnotation sla = SourceLineAnnotation.fromVisitedInstruction
//	     (	
//	    		 classContext,
//			methodGen,
//			javaClass.getSourceFileName(),
//			location.getHandle()
//	     );
//	     
//	     this.getBugAccumulator().accumulateBug(bug, sla);
		
	}    
}

// vim:ts=4
