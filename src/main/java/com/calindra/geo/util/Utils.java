package com.calindra.geo.util;

public class Utils {
	
	public static Boolean checkIfStringIsNullOrEmpty(String s) {
		if(s == null) {
			return true;
		} else if(s.isBlank()) {
			return true;
		}
		return false;
		
	}

}
