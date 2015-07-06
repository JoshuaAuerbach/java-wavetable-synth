/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.engine;

/**
 * Generic interface for classes providing MIDI input. It provides a clock so
 * that the MIDI time can serve as clock provider.
 * 
 * @author florian
 * 
 */
public interface MidiIn extends AdjustableAudioClock {

	/**
	 * Set the listener
	 */
	public void addListener(Listener L);

	/**
	 * remove the listener
	 */
	public void removeListener(Listener L);
	
	/**
	 * @return the Index of this MIDI IN instance
	 */
	public int getInstanceIndex();

	/**
	 * @return true if the device is currently open
	 */
	public boolean isOpen();
	
	/**
	 * Close the device.
	 */
	public void close();

	/** if false, all MIDI events will have time stamp 0 */
	public void setTimestamping(boolean value);

	/** @return the current status of time stamping MIDI events */
	public boolean isTimestamping();

	/**
	 * The listener interface that needs to be implemented by classes that want
	 * to receive MIDI messages.
	 */
	public interface Listener {
		// have channel a single parameter to allow more than 16 channels
		/**
		 * Sent to the listener upon incoming MIDI data.
		 * 
		 * @param event the received event 
		 */
		public void midiInReceived(MidiEvent event);
	}
}
