package com.spec.util;

import java.util.ArrayList;

/**
 * In case we need a key=value container where key needs be duplicated. Should implement the Map interface for long term.
 * @author wli001
 *
 */
public class StringPair{
	String left, right;
	
	public String getLeft() {
		return left;
	}

	public void setLeft(String left) {
		this.left = left;
	}

	public String getRight() {
		return right;
	}

	public void setRight(String right) {
		this.right = right;
	}

	public void put(String l, String r){
		this.setLeft(l);
		this.setRight(r);
	}
	
	public StringPair(String l, String r){
		setLeft(l);
		setRight(r);
	}		
	
	public StringPair(){}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append( "String Pair: [" + this.left + " : " + this.right + "]");
		return sb.toString();
	}
	
	static public String ListToString(ArrayList<StringPair> sp){
		StringBuffer pairList = new StringBuffer();
		
		for (StringPair s: sp){
			pairList.append( s.toString() + "\n");
		}
		
		return pairList.toString();
	}
}
