/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.engine;

/**
 * Encapsulation for a MIDI message plus timing information.
 * 
 * @author florian
 * 
 */
public class MidiEvent implements Comparable<MidiEvent> {

	// IDEA: data values in normalized floating point format

	private AudioTime time;
	private int channel;
	private int status;
	private int data1;
	private int data2;
	private boolean isLong;
	private byte[] longData;
	private MidiIn source;

	/**
	 * Creates a new short MIDI event object.
	 * 
	 * @param time
	 * @param channel
	 * @param status
	 * @param data1
	 * @param data2
	 */
	public MidiEvent(MidiIn source, AudioTime time, int channel, int status,
			int data1, int data2) {
		this.time = time;
		this.channel = channel;
		this.status = status;
		this.data1 = data1;
		this.data2 = data2;
		this.isLong = false;
		this.source = source;
	}

	/**
	 * Creates a new long MIDI event object.
	 * 
	 * @param time
	 * @param longMessage
	 */
	public MidiEvent(MidiIn source, AudioTime time, byte[] longMessage) {
		this.time = time;
		this.longData = longMessage;
		this.isLong = true;
		this.source = source;
	}

	/**
	 * Creates a new short MIDI event object.
	 * 
	 * @param nanoTime
	 * @param channel
	 * @param status
	 * @param data1
	 * @param data2
	 */
	public MidiEvent(MidiIn source, long nanoTime, int channel, int status,
			int data1, int data2) {
		this(source, new AudioTime(nanoTime), channel, status, data1, data2);
	}

	/**
	 * Creates a new long MIDI event object.
	 * 
	 * @param nanoTime
	 * @param longMessage
	 */
	public MidiEvent(MidiIn source, long nanoTime, byte[] longMessage) {
		this(source, new AudioTime(nanoTime), longMessage);
	}

	/**
	 * Create a clone of this event, but with this new audio time.
	 * @param newTime the new time of the cloned event
	 * @return the cloned and modified event
	 */
	public MidiEvent clone(AudioTime newTime) {
		if (isLong()) {
			return new MidiEvent(source, newTime, this.longData);
		}
		return new MidiEvent(source, newTime, channel, status, data1, data2);
	}
	
	/**
	 * Create a clone of this event, but with this new audio time and patched MIDI channel.
	 * @param newTime the new time of the cloned event
	 * @param newChannel the new MIDI channel of the cloned event
	 * @return the cloned and modified event
	 */
	public MidiEvent cloneNewTimeChannel(AudioTime newTime, int newChannel) {
		if (isLong()) {
			return new MidiEvent(source, newTime, this.longData);
		}
		return new MidiEvent(source, newTime, newChannel, status, data1, data2);
	}
	

	/**
	 * @return Returns true if this is a long MIDI message.
	 */
	public boolean isLong() {
		return isLong;
	}

	/**
	 * This method will return for non long messages.
	 * 
	 * @return Returns the long data.
	 */
	public byte[] getLongData() {
		return longData;
	}

	/**
	 * @return Returns the MIDI channel.
	 */
	public int getChannel() {
		return channel;
	}

	/**
	 * @return Returns the data1 parameter.
	 */
	public int getData1() {
		return data1;
	}

	/**
	 * @return Returns the data2 parameter.
	 */
	public int getData2() {
		return data2;
	}

	/**
	 * @return Returns the status byte.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @return Returns the time.
	 */
	public AudioTime getTime() {
		return time;
	}

	/**
	 * @return returns true if this is a realtime event
	 */
	public boolean isRealtimeEvent() {
		return !isLong() && (status > 0xF0);
	}

	/**
	 * @return Returns the source of this event.
	 */
	public MidiIn getSource() {
		return source;
	}

	/**
	 * Compares the time of this event to the time of another event.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(MidiEvent evt) {
		return getTime().compareTo(evt.getTime());
	}

	private static String hexByte(int n) {
		String res = Integer.toHexString(n);
		while (res.length() < 2) {
			res = "0" + res;
		}
		return res;
	}

	private final static int MAX_LONG_EVENT_DISPLAY = 10;

	public String toString() {
		if (isLong()) {
			String res = "";
			for (int i = 0; i < Math.min(MAX_LONG_EVENT_DISPLAY,
					this.longData.length); i++) {
				res += " " + hexByte(longData[i] & 0xFF);
			}
			if (longData.length > MAX_LONG_EVENT_DISPLAY) {
				res += "...(total length=" + longData.length + " bytes)";
			}
			return time + res;
		} else {
			return time + " channel " + getChannel() + ", 0x"
					+ hexByte(getStatus()) + ", 0x" + hexByte(getData1())
					+ ", 0x" + hexByte(getData2());
		}
	}

}
