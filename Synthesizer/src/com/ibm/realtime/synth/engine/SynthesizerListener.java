/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.engine;

/**
 * Listener for the synthesizer's incoming MIDI events.
 * @author florian
 *
 */
public interface SynthesizerListener {
	/**
	 * Called for every MIDI event that the synthesizer is processing.
	 * The event is dispatched when it is actual processed/played (not when
	 * it is received by the midiInReceived() method).
	 *  
	 * @param time the time, normalized to the master clock 
	 * @param source the MidiIn instance that received this event (may be null)
	 * @param channel the MidiChannel of this event (may be null)
	 * @param status the status byte
	 * @param data1 the first data byte for 2/3 byte events
	 * @param data2 the second data byte for 3 byte events
	 */
	public void midiEventPlayed(AudioTime time, MidiIn source, MidiChannel channel, int 
			status, int data1, int data2);
}
