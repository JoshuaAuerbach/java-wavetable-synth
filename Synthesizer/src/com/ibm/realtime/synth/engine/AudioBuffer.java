/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
// This source file is a stub that needs to be replaced with functioning code.

package com.ibm.realtime.synth.engine;

import javax.sound.sampled.AudioFormat;

public class AudioBuffer {
  public AudioBuffer(int channelCount, int sampleCount, double sampleRate) {
  }
  public void initFromByteArray(byte[] buffer, int offset, int byteCount,
          AudioFormat format) {
  }
  public int getByteArrayBufferSize(AudioFormat format) {
	  return 0;
  }
  public int convertToByteArray(byte[] buffer, int offset, AudioFormat format) {
	  return 0;
  }
  public void changeSampleCount(int newSampleCount, boolean keepOldSamples) {
  }
  public void makeSilence() {
  }
  public void mix(AudioBuffer source) {
  }
  public void copyTo(AudioBuffer dest, int offset, int count) {
  }
  public int getChannelCount() {
	  return 0;
  }
  public int getSampleCount() {
	  return 0;
  }
  public double getSampleRate() {
	  return 0.0;
  }
  public void setSampleRate(double sampleRate) {
  }
  public double[] getChannel(int channel) {
	  return null;
  }
}
