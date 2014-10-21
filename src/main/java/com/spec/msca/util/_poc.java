package com.spec.msca.util;

public class _poc {

	private int f;
	
	public int getF(){
		return this.f;		
	}
	
	public void setF(int f){
		if (f > 100) this.f = f;
		else throw new IllegalArgumentException();
	}
}
