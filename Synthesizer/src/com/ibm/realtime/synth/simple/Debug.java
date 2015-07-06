/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.simple;

public class Debug {
	public static void debug(String s) {
		System.out.println(s);
	}

	public static void debug(Exception e) {
		e.printStackTrace();
		System.out.println(e.toString());
	}


}
