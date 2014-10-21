package com.spec.util;

import java.text.ParseException;


/**
	Provides print methods for this application.
	
	@author		Wanchun Li
	@author		paulur@gmail.com
	@version	%I% %G%
 */
public final class Out_Util {
	
	/**
		Prints the specified message.
	*/
	static public void print( String msg ){
		System.out.print( msg );
		
		
	}    
	
	/**
		Prints the specified message with a new line.
	*/
	static public void println( String msg ){
		print( msg +  "\n" );
	}
	
	/**
		Prints the specified error message.
	*/
	static public void printErr( String err ){		
		System.err.print( "\nERROR! " + err );
	}
	
	/**
		Prints the name of the instance that makes the error when excutes the doStuff job.
		
		@param	doStuff	the job to be done.
		@param	obje	the ojbect to do the job.
	*/
	static public void printErr( String doStuff, Object obj ){
		System.err.print( "\nERROR! Failed to " + doStuff + " in " + obj.getClass().getName() );
	}

	static public void threadPrint(String msg) {
		String name = Thread.currentThread().getName();
		System.out.println(name + ": " + msg);
	}
	
	static public double formatDoubleValue( double d ) throws ParseException{
		
		return formatDoubleValue(d, CONSTANT.DOUBLE_VALUE_PREC);
	}
	
	static public double formatDoubleValue( double d, int prec ) throws ParseException{
		StringBuffer format = new StringBuffer( "#.");
		for ( int i = 0; i < prec; i++ ){
			format.append( "#" );
		}
		java.text.DecimalFormat df = new java.text.DecimalFormat( format.toString() );
		double fd = df.parse(df.format(d)).doubleValue();
		
		return fd;
	}
}///:~ end of Out