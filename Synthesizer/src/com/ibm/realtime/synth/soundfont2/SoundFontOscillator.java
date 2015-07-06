/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.soundfont2;

import com.ibm.realtime.synth.engine.*;

public class SoundFontOscillator extends Oscillator {

	public SoundFontOscillator(SoundFontSample sample,
			SoundFontSampleData sampleData) {
		setNativeAudioFormat(sample.getSampleRate(), 16, 2, 1, true, false);
		this.nativeSamples = sampleData.getData();
		this.nativeSamplesStartPos = sample.getStart();
		this.nativeSamplesEndPos = sample.getEnd();

		this.loopStart = sample.getStartLoop();
		this.loopEnd = sample.getEndLoop();
	}

	protected void convertOneBlock(AudioBuffer buffer, int offset, int count) {
		assert(buffer.getChannelCount()==1);
		ConversionTool.byte2doubleGenericLSRC(nativeSamples,
					0, nativeSampleSize, nativePos,
					nativePosDelta, buffer.getChannel(0), offset,
					count, nativeFormatCode);
	}

	/**
	 * @param loopEnd The loopEnd to set.
	 */
	void addLoopEnd(double loopEnd) {
		this.loopEnd += loopEnd;
	}

	/**
	 * @param loopMode The loopMode to set, one of the Oscillator.LOOPMODE_*
	 *            constants.
	 */
	void setLoopMode(int loopMode) {
		this.loopMode = loopMode;
	}

	/**
	 * @param loopStart The loopStart to set.
	 */
	void addLoopStart(double loopStart) {
		this.loopStart += loopStart;
	}

	/**
	 * @param nativeSamplesEndPos The nativeSamplesEndPos to set.
	 */
	void addNativeSamplesEndPos(int nativeSamplesEndPos) {
		this.nativeSamplesEndPos += nativeSamplesEndPos;
	}

	/**
	 * @param nativeSamplesStartPos The nativeSamplesStartPos to set.
	 */
	void addNativeSamplesStartPos(int nativeSamplesStartPos) {
		this.nativeSamplesStartPos += nativeSamplesStartPos;
	}

}
