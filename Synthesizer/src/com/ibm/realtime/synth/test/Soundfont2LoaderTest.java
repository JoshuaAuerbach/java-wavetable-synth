/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.test;

import java.io.*;
import com.ibm.realtime.synth.soundfont2.*;

/**
 * Test program to load a SoundFont 2 file and to dump information while parsing
 * the file. See Parser.java for trace options.
 * 
 * @author florian
 * 
 */
public class Soundfont2LoaderTest {

	public static final String sfDir = "E:\\TestSounds\\sf2\\";

	//public static final String filename = sfDir + "Chorium.SF2";
	// public static final String filename = sfDir+"classictechno.sf2";
	// public static final String filename = sfDir+"bh_cello.sf2";
	public static final String filename = sfDir + "RealFont_2_1.sf2";

	public static boolean MEM_TEST = false;

	public static long initialMem = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Parser.TRACE = true;
		Parser.TRACE_INFO = true;
		Parser.TRACE_RIFF = true;
		Parser.TRACE_RIFF_MORE = true;
		Parser.TRACE_PRESET = true;
		Parser.TRACE_PROCESSOR = true;
		Parser.TRACE_GENERATORS = true;
		Parser.TRACE_MODULATORS = true;
		Parser.TRACE_SAMPLELINKS = true;


		if (!MEM_TEST) {
			new Parser().load(new FileInputStream(new File(filename)));
		} else {
			// Memory test
			initialMem = printMemory("before", false);
			Parser p = new Parser();
			p.load(new FileInputStream(new File(filename)));
			printMemory("after 1st run", true);
			p = new Parser();
			p.load(new FileInputStream(new File(filename)));
			printMemory("after 2nd run", true);
			p = new Parser();
			p.load(new FileInputStream(new File(filename)));
			printMemory("after 3rd run", true);

		}
	}

	private static long printMemory(String text, boolean printOverhead) {
		Runtime rt = Runtime.getRuntime();
		System.gc();
		System.runFinalization();
		System.gc();
		long mem = (rt.totalMemory() - rt.freeMemory()) / 1024;
		long fileSize = (new File(filename)).length() / 1024;
		System.out.println("--------------------------------------");
		System.out.print("Used memory " + text + ": " + mem + " KB");
		if (printOverhead) {
			System.out.println(", class overhead: "
					+ (mem - fileSize - initialMem) + " KB.");
		} else {
			System.out.println(".");
		}
		System.out.println("--------------------------------------");
		return mem;
	}

}
