/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.engine;

/**
 * Listener that sends its event right before rendering a new chunk of audio
 * data - typically sent by the AudioPullThread.
 * 
 * @author florian
 * 
 */
public interface AudioRendererListener {

	public void newAudioSlice(AudioTime time, AudioTime duration);

}
