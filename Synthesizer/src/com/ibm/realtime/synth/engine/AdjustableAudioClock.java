/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.engine;

/**
 * A time reference that can be adjusted with an offset.
 * 
 * @author florian
 */
public interface AdjustableAudioClock extends AudioClock {

	/**
	 * Set an offset that will be applied to the time returned by this clock.
	 * 
	 * @param offset
	 */
	public void setTimeOffset(AudioTime offset);

	/**
	 * Returns the current offset of this clock. If no offset has been set,
	 * returns 0.
	 * 
	 * @return the time offset of this clock
	 */
	public AudioTime getTimeOffset();

}
