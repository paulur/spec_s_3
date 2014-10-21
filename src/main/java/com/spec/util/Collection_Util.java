package com.spec.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class Collection_Util {
	static public ArrayList<String> convertStringSetToSortedArrayList(Set<String> aSet){
		ArrayList<String> list = new ArrayList<String>(aSet);
		Collections.sort(list);
		
		return list; 
	}
	
	
}
