/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.engine;

/**
 * An interface for objects that can render audio data. The objects
 * must maintain themselves the buffer, sample rate and number of samples
 * to be rendered in each render() call.
 * @author florian
 *
 */
public interface Renderable {

	/**
	 * Render the next buffer for the passed time. The method
	 * should keep track on its own of how many samples to render. 
	 * @param time the time of the buffer to be rendered.
	 * @return true if the method actually rendered. False if the Renderable
	 * has already rendered the buffer for the time, or if the Renderable is 
	 * already done
	 */
	public boolean render(AudioTime time);

	/**
	 * Checks if this Renderable has already rendered a buffer for the 
	 * specified time. This method should allow for 125microseconds grace time
	 * to compensate rounding errors (125us is 1 sample at 8000Hz).
	 * 
	 * @param currTime the time to be tested
	 * @return true if this Renderable has already rendered a block of audio 
	 * data for the specified time.
	 */
	public boolean alreadyRendered(AudioTime currTime);

}
