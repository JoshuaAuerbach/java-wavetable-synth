/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.engine;

import static com.ibm.realtime.synth.utils.AudioUtils.*;

/**
 * An abstraction of a time value.
 * 
 * @author florian
 */
public class AudioTime implements Comparable<AudioTime> {
	private long nanosecond;

	public AudioTime(long nanosecond) {
		this.nanosecond = nanosecond;
	}

	public AudioTime(long samples, double sampleRate) {
		this.nanosecond = samples2nanos(samples, sampleRate);
	}

	public long getNanoTime() {
		return nanosecond;
	}

	public long getMicroTime() {
		return nanosecond / 1000L;
	}

	public long getMillisTime() {
		return nanosecond / 1000000L;
	}

	public double getSecondsTime() {
		return nanosecond / 1000000000.0;
	}

	public long getSamplesTime(double sampleRate) {
		return nanos2samples(nanosecond, sampleRate);
	}

	public AudioTime add(long nanos) {
		return new AudioTime(getNanoTime() + nanos);
	}

	public AudioTime add(AudioTime at) {
		return new AudioTime(getNanoTime() + at.getNanoTime());
	}

	/**
	 * @return this - nanos
	 */
	public AudioTime subtract(long nanos) {
		return new AudioTime(getNanoTime() - nanos);
	}

	/**
	 * @return this time - at
	 */
	public AudioTime subtract(AudioTime at) {
		return new AudioTime(getNanoTime() - at.getNanoTime());
	}

	public boolean earlierThan(AudioTime at) {
		return compareTo(at) < 0;
	}

	public boolean earlierOrEqualThan(AudioTime at) {
		return compareTo(at) <= 0;
	}

	public boolean laterThan(AudioTime at) {
		return compareTo(at) > 0;
	}

	public boolean laterOrEqualThan(AudioTime at) {
		return compareTo(at) >= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(AudioTime at) {
		long comp = getNanoTime() - at.getNanoTime();
		return (comp < 0) ? -1 : ((comp == 0) ? 0 : 1);
	}

	/** display this audio time in microseconds */
	public String toString() {
		StringBuffer sb = new StringBuffer(Long.toString(getNanoTime() / 1000));
		int length = sb.length();
		int cmp = 0;
		if (sb.charAt(0) == '-') {
			cmp = 1;
		}
		char sep = '.';
		if (length > 3 + cmp) {
			sb.insert(length - 3, sep);
			if (length > 6 + cmp) {
				sb.insert(length - 6, sep);
				// if (length > 9 + cmp) {
				// sb.insert(length-9, sep);
				// }
			}
		}
		sb.append("us");
		return sb.toString();
	}

}
