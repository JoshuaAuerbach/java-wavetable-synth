/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.engine;

/**
 * Master interface for all classes providing a final place for audio samples.
 * It provides a clock so that the audio time can serve as clock provider.
 * 
 * @author florian
 */
public interface AudioSink extends AdjustableAudioClock {

	/**
	 * write audio data to this audio sink. This method is blocking, i.e. it
	 * only returns when all data is written or this sink is closed.
	 * 
	 * @param buffer
	 */
	public void write(AudioBuffer buffer);

	/**
	 * @return true if the sink is open and ready to accept audio data
	 */
	public boolean isOpen();

	/**
	 * Close the device
	 */
	public void close();
	
	/**
	 * @return number of audio channels of this sink
	 */
	public int getChannels();

	/**
	 * @return the number of samples that this devices preferably writes at once
	 */
	public int getBufferSize();

	/**
	 * @return the sample rate at which this sink expects audio data be written
	 */
	public double getSampleRate();

}
