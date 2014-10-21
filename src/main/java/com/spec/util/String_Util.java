package com.spec.util;

/**
 * 
 * @author paul
 *
 */
public class String_Util {
	/**
	 * truncate specified last N char
	 * @param 	aString the source string
	 * @param 	N	the number of char to truncate
	 * @return	the truncated string
	 */
	public static String truncateLastNChar( String aString, int N ){
		int length = aString.length();
		
		int indexOfTrancate	= length - N;
		
		return aString.substring(0, indexOfTrancate);
	}
	
	
	public static String truncateLastChar(String aString){
		return truncateLastNChar(aString, 1);
	}
	
	public static void main( String[] args ){
		String sourceString	= "RDC-Fwd-2-Lo-L-2(o)";
								//"A429-LRU-001(i)";
		String trancatedString	= truncateLastNChar( sourceString, 1 );
		
		_debug.println("trancete " + sourceString + "= " + trancatedString);		
	}
}
