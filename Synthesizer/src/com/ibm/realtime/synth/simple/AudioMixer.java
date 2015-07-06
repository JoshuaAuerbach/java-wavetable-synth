/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.simple;

import java.util.*;

/**
 * An object that takes an arbitrary number of
 * input audio streams and renders them to the
 * output buffer.
 */
public class AudioMixer implements AudioInput {

	/**
	 * Collection of currently active input streams
	 */
	private List<AudioInput> streams;
	
	/**
	 * Create an instance of a mixer
	 */
	public AudioMixer(int channels, float sampleRate) {
		streams = new ArrayList<AudioInput>();
	}

	/**
	 * The temporary buffer that is used to read the individual input streams.
	 */
	FloatSampleBuffer tempBuffer = new FloatSampleBuffer(2, 0, 44100.0f);
			

	/**
	 * The actual mixing function
	 */
	public void read(FloatSampleBuffer buffer) {
		// get a local copy of the input streams
		AudioInput[] localStreams;
		synchronized(streams) {
			localStreams = streams.toArray(new AudioInput[0]);
		}
		// setup the temporary buffer
		tempBuffer.init(buffer.getChannelCount(), buffer.getSampleCount(), buffer.getSampleRate());
		
		// empty the buffer passed in
		buffer.makeSilence();
		// iterate over all registered input streams
		for (AudioInput stream : localStreams) {
			// read from this source stream
			stream.read(tempBuffer);
			// add/mix to mix buffer
			for (int channel = 0; channel < buffer.getChannelCount(); channel++) {
				float[] data = buffer.getChannel(channel);
				float[] tempData = tempBuffer.getChannel(channel);
				for (int i=0; i<buffer.getSampleCount(); i++) {
					data[i] += tempData[i];
				}
			}
		}
	}
	
	public boolean done() {
		return false;
	}
	
	public void addAudioStream(AudioInput stream) {
		synchronized(streams) {
			streams.add(stream);
		}
		//Debug.debug("Mixer: added audio stream -- now "+streams.size()+" streams.");
	}

	public void removeAudioStream(AudioInput stream) {
		synchronized(streams) {
			streams.remove(stream);
		}
		//Debug.debug("Mixer: removed audio stream -- now "+streams.size()+" streams.");
	}
	
	public List<AudioInput> getAudioStreams() {
		// TODO: should really return a copy
		return streams;
	}

}
