/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.engine;

/**
 * A time reference that provides continuous time.
 * 
 * @author florian
 * 
 */
public interface AudioClock {
	/**
	 * Returns the current time of this clock in seconds.
	 * 
	 * @return the time in seconds
	 */
	public AudioTime getAudioTime();
}
