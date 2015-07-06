/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.test;

import static com.ibm.realtime.synth.utils.Debug.*;

import org.eclipse.swt.widgets.Composite;

/**
 * Class to record wave files, and emit trace data.
 * 
 * @author florian
 */
public class AudioRecorder extends AudioCaptureGUIBase {

	/**
	 * Create an instance and load the persistence properties.
	 * 
	 * @param name the title of this pane
	 */
	public AudioRecorder(String name, String[] args) {
		super(name);
		parseArguments(args);
	}

	protected void initImpl() {
		// activate recording by default
		recorderActive.setSelection(true);
	}

	@Override
	protected void createPartControlImpl(Composite parent) {
	}

	/** called directly after starting the audio device */
	protected void onStart() {
	}

	/** called directly after staopping the audio device */
	protected void onStop() {
	}

	private void parseArguments(String[] args) {
		if (args != null) {
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi];
				if (arg.equals("-h")) {
					printUsageAndExit();
				} else if (arg.equals("-v")) {
					DEBUG = true;
				} else if (arg.equals("-start")) {
					setAutoStart(true);
				} else if (arg.equals("-duration")) {
					argi++;
					if (argi >= args.length) {
						printUsageAndExit(arg);
					}
					setDuration(Integer.parseInt(args[argi]));
				} else if (arg.equals("-a")) {
					argi++;
					if (argi >= args.length) {
						printUsageAndExit(arg);
					}
					setAudioOutputDevice(args[argi]);
				} else {
					printUsageAndExit(arg);
				}
				argi++;
			}
		}
	}

	/* main method */
	public static void main(String[] args) {
		(new AudioRecorder("Audio Recorder", args)).mainImpl(400, 400);
	}

	private static void printUsageAndExit(String failedArg) {
		out("ERROR: argument " + failedArg);
		printUsageAndExit();
	}

	private static void printUsageAndExit() {
		out("Usage:");
		out("java AudioRecorder [options]");
		out("Options:");
		out("-h                  : display this help message");
		out("-v                  : verbose");
		out("-start              : start recording immediately");
		out("-a <audio dev>      : override audio output device");
		out("-duration <sec>     : quit this program after <sec> seconds");
		System.exit(1);
	}


}
