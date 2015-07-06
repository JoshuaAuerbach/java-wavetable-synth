/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.simple;

/**
 * Master interface for all classes providing
 * audio data.
 * 
 * @author florian
 */
public interface AudioInput {
	/**
	 * Fill the entire length of the buffer.
	 * By definition, the read method will overwrite
	 * the data in buffer, i.e. even if nothing needs
	 * to be written, buffer needs to be silenced
	 * nonetheless.
	 * @param buffer the buffer to be filled
	 */
	public void read(FloatSampleBuffer buffer);
	
	/**
	 * Returns true if this input stream has finished
	 * rendering data and further calls to read would
	 * just return silence.
	 */
	public boolean done();
}
