/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
// This source file is a stub that needs to be replaced with functioning code.

package com.ibm.realtime.synth.simple;

import javax.sound.sampled.AudioFormat;

public class FloatSampleBuffer {
  public FloatSampleBuffer(int channelCount, int sampleCount, float sampleRate) {
  }
  protected void init(int aChannelCount, int aSampleCount, float aSampleRate) {
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
  public void removeChannel(int channel) {
  }
  public void expandChannel(int targetChannelCount) {
  }
  public int getChannelCount() {
	  return 0;
  }
  public int getSampleCount() {
	  return 0;
  }
  public float getSampleRate() {
	  return 0.0f;
  }
  public void setSampleRate(float sampleRate) {
  }
  public float[] getChannel(int channel) {
	  return null; 
  }
}
