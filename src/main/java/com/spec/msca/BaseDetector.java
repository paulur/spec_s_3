package com.spec.msca;

import java.util.Iterator;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;

//import com.spec.util._debug;

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

/**
 * The base class of the template pattern.
 * 
 * @author wli001
 *
 */
public abstract class BaseDetector implements Detector{
    
	abstract public BugReporter 	getBugReporter(); 
    abstract public BugAccumulator	getBugAccumulator();
    abstract public void			setMethod(Method method);
    abstract public void			setClassContext(ClassContext classContext);
    abstract public ClassContext 	getClassContext();
	abstract public boolean 		isTargetInstruction(InvokeInstruction invoke, ConstantPoolGen cpg);
	abstract public void 			verifyTarget(InvokeInstruction invoke, Location location,
										InstructionHandle handle, ConstantPoolGen cpg, MethodGen methodGen,
										JavaClass javaClass);
    
    public void report() {
    }    
    
    public void visitClassContext(ClassContext classContext) {
        JavaClass javaClass = classContext.getJavaClass();
        Method[] methodList = javaClass.getMethods();

        for (Method method : methodList) {
            MethodGen methodGen = classContext.getMethodGen(method);
            if (methodGen == null)
                continue;

            try {
                analyzeMethod(classContext, method);
            } catch (DataflowAnalysisException e) {
            	getBugReporter().logError(
                        "EncryptVerifier caught exception while analyzing " +
                        classContext.getFullyQualifiedMethodName(method),
                        e);
            } catch (CFGBuilderException e) {
            	getBugReporter().logError(
                        "EncryptVerifier caught exception while analyzing " +
                        classContext.getFullyQualifiedMethodName(method),
                        e);
            } catch (RuntimeException e) {
            	getBugReporter().logError(
                        "EncryptVerifier caught exception while analyzing " +
                        classContext.getFullyQualifiedMethodName(method),
                        e);
            }
        }
    }
	
    protected void accumulateBug(BugInstance bug, MethodGen methodGen, JavaClass javaClass, Location location){
    	bug.addClassAndMethod(methodGen, javaClass.getSourceFileName());
    	SourceLineAnnotation sla = SourceLineAnnotation.fromVisitedInstruction(getClassContext(), methodGen, javaClass.getSourceFileName(), location.getHandle());
    	getBugAccumulator().accumulateBug(bug, sla);
    }
     
    private void analyzeMethod(ClassContext classContext, Method method)
    	throws DataflowAnalysisException, CFGBuilderException
    {
//    	 _debug.println("analyze method init...");
        JavaClass javaClass = classContext.getJavaClass();
//        this.method = method;
//        this.classContext = classContext;
        setMethod(method);
        setClassContext(classContext);
        MethodGen methodGen = classContext.getMethodGen(method);
        
        if (methodGen == null) return;
        
        ConstantPoolGen cpg = methodGen.getConstantPool();
        CFG cfg = classContext.getCFG(method);
        
        for (Iterator<Location> i = cfg.locationIterator(); i.hasNext();)
        {
        	Location location	= i.next();
        	InstructionHandle handle	= location.getHandle();
        	Instruction ins = handle.getInstruction();
        
            if (!(ins instanceof InvokeInstruction)) continue;
            
            InvokeInstruction invoke = (InvokeInstruction) ins;
            
            if ( isTargetInstruction(invoke, cpg) ) 
            	verifyTarget(invoke, location, handle, cpg, methodGen, javaClass);
        }
        
        getBugAccumulator().reportAccumulatedBugs();
    }
}
