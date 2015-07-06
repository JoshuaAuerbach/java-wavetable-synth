/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.test;

import com.ibm.realtime.synth.engine.*;
import static com.ibm.realtime.synth.utils.Debug.debug;
import static com.ibm.realtime.synth.utils.Debug.format3;

import java.io.File;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

/**
 * Load a MIDI file and push its events directly to a synthesizer's queue.
 * 
 * @author florian
 */
public class SMFPusher {
	private final static boolean DEBUG_SMF_PUSHER = false;

	/**
	 * The Sequence containing the MIDI file
	 */
	private Sequence sequence;
	
	private double durationSeconds = 0.0;

	public void open(File file) throws Exception {
		sequence = MidiSystem.getSequence(file);
		if (DEBUG_SMF_PUSHER) {
			debug("Got MIDI sequence with " + sequence.getTracks().length
					+ " tracks. Duration: "
					+ format3(sequence.getMicrosecondLength() / 1000000.0)
					+ " seconds.");
		}
	}

	/**
	 * Send all events to the synth and let it schedule them.
	 * We do not need to order the tracks' events, since the 
	 * synth should do it on its own. This method plays the file 
	 * at 120bpm and ignores any tempo change events. 
	 * @return the number of events scheduled to the synth
	 */
	public int pushToSynth(Synthesizer synth) {
		if (DEBUG_SMF_PUSHER) {
			debug("Pushing the MIDI file to the synth...");
		}
		durationSeconds = 0.0;
		int events = 0;
		// TODO: keep track of tempo
		double tempo = 120.0;
		double tickFactor = 60.0/(tempo*((double)sequence.getResolution()));
		Track[] tracks = sequence.getTracks();
		for (Track track:tracks) {
			int trackSize = track.size();
			double timeSeconds = 0.0;
			for (int i = 0; i<trackSize; i++) {
				javax.sound.midi.MidiEvent event = track.get(i);
				timeSeconds = event.getTick() * tickFactor;
				long nanoTime = (long) (timeSeconds * 1000000000.0);
				byte[] msg = event.getMessage().getMessage();
				if (msg.length>1 && msg.length<=3) {
					int data2 = 0;
					if (msg.length==3) {
						data2 = (int) msg[2];
					}
					int channel = msg[0] & 0xF;
					int status = msg[0] & 0xF0;
					if (synth!=null) {
						MidiEvent me = new MidiEvent(null, nanoTime, channel, status, msg[1], data2);
						synth.midiInReceived(me);
					}
					if (status == 0x90 && data2 > 0) {
						events++;
					}
				}
			}
			if (timeSeconds > durationSeconds) {
				durationSeconds = timeSeconds;
			}
		}
		if (DEBUG_SMF_PUSHER) {
			debug("...done: scheduled "+events+" Note On events to synth.");
		}
		return events;
	}
	
	public double getDurationInSeconds() {
		return durationSeconds;
	}
	
}
