/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.soundfont2;

/**
 * A class to store the actual audio sample data of a SoundFont file.
 * 
 * @author florian
 * 
 */
public class SoundFontSampleData {
	private byte[] data;

	/**
	 * @return Returns the data.
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @param data The sample data to set.
	 */
	protected void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * @return the number of sample data points
	 */
	public int getSampleCount() {
		if (data == null) {
			return 0;
		}
		// 16-bit samples
		return data.length / 2;
	}

}
