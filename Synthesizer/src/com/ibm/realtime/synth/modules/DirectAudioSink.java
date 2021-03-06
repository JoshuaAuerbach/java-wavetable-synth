/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.modules;

import static com.ibm.realtime.synth.utils.Debug.debug;

import com.ibm.realtime.synth.engine.*;
import com.ibm.realtime.synth.utils.*;

import javax.sound.sampled.AudioFormat;

import java.util.*;

/**
 * An AudioSink with optimized latency using ALSA drivers directly.
 * 
 * @author florian
 */
public class DirectAudioSink implements AudioSink {

	public static boolean DEBUG_DIRECTSINK = false;

	private static boolean libAvailable = false;

	private static final int UNDERRUN_FLAG = 0x10000000;

	static {
		try {
			System.loadLibrary("directaudiosink");
			libAvailable = true;
		} catch (UnsatisfiedLinkError ule) {
			if (DEBUG_DIRECTSINK) {
				Debug.debug("DirectAudioSink not available (failed to load native library)");
				// Debug.debug(ule);
				Debug.debug("java.library.path="
						+ System.getProperty("java.library.path"));
			}
		}
	}

	public static final int BIT_TYPE_16_BIT = 1 << 0;
	public static final int BIT_TYPE_24_BIT3 = 1 << 1;
	public static final int BIT_TYPE_24_BIT4 = 1 << 2;
	public static final int BIT_TYPE_32_BIT = 1 << 3;
	public static final int BIT_TYPE_BIG_ENDIAN_FLAG = 1 << 30;
	/** the mask includes all format flags, excluding the big endian flag */
	private static final int BIT_TYPE_MASK = 0xFFF;

	/**
	 * The native audio format to be used
	 */
	private AudioFormat format;

	/**
	 * A temporary byte buffer for conversion to the native format
	 */
	private byte[] byteBuffer;

	/**
	 * Native handle to the device
	 */
	long handle = 0;

	/**
	 * The current offset of the audio clock (interface AdjustableAudioClock) in
	 * samples.
	 */
	private long clockOffsetSamples = 0;

	/**
	 * the name of the open device
	 */
	private String devName;

	/**
	 * The buffer size in samples - i.e. ALSA's period size
	 */
	protected int periodSize;

	/**
	 * number of underruns that occured
	 */
	private int underrunCount = 0;

	/**
	 * Constructor.
	 */
	public DirectAudioSink() {

	}

	/**
	 * Open the soundcard with the given format and buffer size in milliseconds.
	 * The audio time (regardless of the time offset) will be reset to 0.
	 */
	public synchronized void open(String devName, int bufferSizeInMillis,
			AudioFormat format) throws Exception {
		open(devName, format, (int) AudioUtils.millis2samples(
				bufferSizeInMillis, format.getSampleRate()));
	}

	/**
	 * Open the soundcard with the given format. The audio time (regardless of
	 * the time offset) will be reset to 0. By default, the period size will be
	 * half the buffer size.
	 */
	public synchronized void open(String devName, AudioFormat format,
			int bufferSizeInSamples) throws Exception {
		// set the period size to the requested buffer size, use 2 periods per buffer
		int period = bufferSizeInSamples;
		// for the ALSA buffer, use double the buffer size
		bufferSizeInSamples *= 2;
		openImpl(devName, format, bufferSizeInSamples, period, true);
		if (DEBUG_DIRECTSINK) {
			debug("open direct soundcard. requested buffer size: "
					+ bufferSizeInSamples
					+ " samples. Actual: "
					+ this.periodSize
					+ " samples = "
					+ Debug.format2(AudioUtils.samples2micros(periodSize,
							format.getSampleRate()) / 1000.0) + " millis");
		}
	}

	/**
	 * Open the soundcard with the given format. The audio time (regardless of
	 * the time offset) will be reset to 0.
	 */
	protected synchronized void openImpl(String devName, AudioFormat format,
			int bufferSizeInSamples, int periodSizeInSamples, boolean blockingIO)
			throws Exception {
		if (!libAvailable) {
			throw new Exception(
					"Direct Audio Sink: native library could not be loaded!");
		}
		if (handle != 0) {
			close();
		}
		// only use PCM signed formats
		if (!format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
			throw new Exception("Direct Audio Sink: only PCM formats allowed");
		}
		if (format.getSampleSizeInBits() < 16) {
			throw new Exception(
					"Direct Audio Sink: at least 16 bit sample width required");
		}

		if (periodSizeInSamples > bufferSizeInSamples
				|| ((bufferSizeInSamples % periodSizeInSamples) != 0)) {
			throw new Exception("Illegal ALSA period size: buffer size ("
					+ bufferSizeInSamples + " samples) "
					+ "must be a multiple of period size ("
					+ periodSizeInSamples + " samples)");
		}
		// eventually try to open the device
		handle = nOpen(devName, (int) format.getSampleRate(),
				format.getChannels(), format.getSampleSizeInBits(),
				bufferSizeInSamples, periodSizeInSamples, blockingIO);
		if (handle != 0) {
			this.devName = devName;
			periodSize = nGetPeriodSize(handle);
			int sampleBitType = nGetSampleBitType(handle);
			this.format = makeFormat(format, sampleBitType);
		} else {
			throw new Exception("Cannot open direct audio device");
		}
	}

	/**
	 * Converts a format to match the format opened by the device
	 * 
	 * @param format the original format
	 * @param sampleBitType the currently open sample bit type, one of the
	 *            BIT_TYPE_* flags
	 * @return the adapted format
	 */
	private AudioFormat makeFormat(AudioFormat format, int sampleBitType) {
		int sampleSizeInBits = -1; // in bits
		int sampleSizeInBytes = -1; // in bytes

		switch (sampleBitType & BIT_TYPE_MASK) {
		case BIT_TYPE_16_BIT:
			sampleSizeInBits = 16;
			break;
		case BIT_TYPE_24_BIT3:
			sampleSizeInBits = 24;
			break;
		case BIT_TYPE_24_BIT4:
			sampleSizeInBits = 24;
			sampleSizeInBytes = 4;
			break;
		case BIT_TYPE_32_BIT:
			sampleSizeInBits = 32;
			break;
		}
		if (sampleSizeInBits == -1) {
			throw new IllegalArgumentException("Unknown sample bit type");
		}
		if (sampleSizeInBytes < 0) {
			sampleSizeInBytes = (sampleSizeInBits + 7) / 8;
		}
		return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
				format.getSampleRate(), sampleSizeInBits, format.getChannels(),
				sampleSizeInBytes * format.getChannels(),
				format.getSampleRate(), // frameRate = sampleRate for PCM
										// formats
				(sampleBitType & BIT_TYPE_BIG_ENDIAN_FLAG) != 0);
	}

	public synchronized void close() {
		if (handle != 0) {
			nClose(handle);
			handle = 0;
			if (DEBUG_DIRECTSINK) {
				debug("closed direct soundcard: " + devName);
			}
			devName = "";
		}
	}

	/**
	 * @return the format used with this sink
	 */
	public AudioFormat getFormat() {
		return format;
	}

	/**
	 * Convert the buffer to the native audio format, then natively write it to
	 * the soundcard.
	 * 
	 * @see com.ibm.realtime.synth.engine.AudioSink#write(com.ibm.realtime.synth.engine.AudioBuffer)
	 */
	public synchronized void write(AudioBuffer buffer) {
		if (!isOpen()) {
			return;
		}
		AudioFormat format = getFormat();
		// set up the temporary buffer that receives the converted
		// samples in bytes
		int requiredSize = buffer.getByteArrayBufferSize(format);
		if (byteBuffer == null || byteBuffer.length < requiredSize) {
			byteBuffer = new byte[requiredSize];
		}
		int len = buffer.convertToByteArray(byteBuffer, 0, format);
		// debug("Trying to write "+len+" bytes. Sample buffer holds
		// "+buffer.getSampleCount()+" samples");
		int offset = 0;
		int written;
		do {
			written = nWrite(handle, byteBuffer, offset, len);
			if ((written & UNDERRUN_FLAG) != 0) {
				// clear the underrun flag
				written = written ^ UNDERRUN_FLAG;
				underrun(written);
			}
			len -= written;
			offset += written;
		} while (written >= 0 && len > 0);
		if (DEBUG_DIRECTSINK) {
			if (written < 0) {
				Debug.error("DirectAudioSink: nWrite() returned " + written);
			}
		}
	}

	/**
	 * This method is called when an underrun occured in the native layer.
	 */
	private void underrun(int writtenSamples) {
		underrunCount++;
		if (DEBUG_DIRECTSINK) {
			debug("DirectAudioSink: underrun #" + underrunCount
					+ ": wrote only " + writtenSamples + " samples");
		}

	}
	
	/** @return the number of underruns since the last call to this function. */
	public int getUnderrunCount() {
		int ret = underrunCount;
		underrunCount = 0;
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.realtime.synth.engine.AudioSink#isOpen()
	 */
	public boolean isOpen() {
		return handle != 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.realtime.synth.engine.AudioSink#getChannels()
	 */
	public int getChannels() {
		return format.getChannels();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.realtime.synth.engine.AudioSink#getBufferSize()
	 */
	public int getBufferSize() {
		return periodSize;
	}

	public long getPeriodTimeNanos() {
		return AudioUtils.samples2nanos(getPeriodSizeSamples(),
				format.getSampleRate());
	}

	public int getPeriodSizeSamples() {
		return nGetPeriodSize(handle);
	}

	/** @return the ALSA buffer size, i.e. twice the period */
	public long getALSABufferTimeNanos() {
		return AudioUtils.samples2nanos(getALSABufferSizeSamples(),
				format.getSampleRate());
	}

	/** @return the ALSA buffer size, i.e. twice the period */
	public int getALSABufferSizeSamples() {
		return nGetBufferSize(handle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.realtime.synth.engine.AudioSink#getSampleRate()
	 */
	public double getSampleRate() {
		return (double) format.getSampleRate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.realtime.synth.engine.AdjustableAudioClock#setTimeOffset(com.ibm.realtime.synth.engine.AudioTime)
	 */
	public void setTimeOffset(AudioTime offset) {
		this.clockOffsetSamples = offset.getSamplesTime(getSampleRate());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.realtime.synth.engine.AdjustableAudioClock#getTimeOffset()
	 */
	public AudioTime getTimeOffset() {
		return new AudioTime(clockOffsetSamples, getSampleRate());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.realtime.synth.engine.AudioClock#getAudioTime()
	 */
	public AudioTime getAudioTime() {
		if (handle != 0) {
			return new AudioTime(nGetPosition(handle) + clockOffsetSamples,
					getSampleRate());
		} else {
			return new AudioTime(clockOffsetSamples, getSampleRate());
		}
	}

	/**
	 * @return true if the direct audio sink can be used
	 */
	public static boolean isAvailable() {
		return libAvailable;
	}

	/**
	 * Returns a list of available devices. It starts with the device identifier
	 * followed by an optional description. The description is separated by the
	 * bar | character.
	 * 
	 * @return a list of available devices which should not be modified.
	 */
	public static List<String> getDeviceList() {
		if (!isAvailable()) {
			return new ArrayList<String>(0);
		}
		List<DirectAudioSinkDeviceEntry> list = getDeviceEntryList();
		List<String> deviceCache = new ArrayList<String>(list.size());
		for (int i = 0; i < list.size(); i++) {
			deviceCache.add(list.get(i).toString());
		}
		return deviceCache;
	}

	/**
	 * Remember the device listing for a certain time to optimize repeated calls
	 */
	private static List<DirectAudioSinkDeviceEntry> deviceListCache = null;

	/**
	 * the time in millis when the deviceCache was updated
	 */
	private static long deviceListCacheTime = 0;

	public static List<DirectAudioSinkDeviceEntry> getDeviceEntryList() {
		if (!isAvailable()) {
			return new ArrayList<DirectAudioSinkDeviceEntry>(0);
		}
		if (deviceListCache == null
				|| deviceListCacheTime + 2000 < System.currentTimeMillis()) {
			deviceListCache = new ArrayList<DirectAudioSinkDeviceEntry>(20);
			nFillDeviceNames(deviceListCache);
		}
		return deviceListCache;
	}

	// NATIVE METHODS
	/**
	 * Returns Handle to device
	 * 
	 * @param bufferSize the buffer size in samples
	 * @param blocking if the device is opened in synchronous way
	 */
	private native static long nOpen(String devName, int sampleRate,
			int channels, int sampleWidth, int bufferSize, int periodSize,
			boolean blocking);

	/**
	 * @return the buffer size in samples
	 */
	native static int nGetBufferSize(long handle);

	/**
	 * @return the period size in samples
	 */
	native static int nGetPeriodSize(long handle);

	/**
	 * @return the currently open sample bit type, one of the BIT_TYPE_* flags
	 */
	private native static int nGetSampleBitType(long handle);

	private native static boolean nClose(long handle);

	/**
	 * Returns how many bytes were written to the device. If the return code's
	 * UNDERRUN_FLAG is set, an underrun occured.
	 * 
	 * @param len the number of bytes to write
	 * @return number of written bytes, or a negative error code
	 */
	native static int nWrite(long handle, Object byteArray, int offset, int len);

	/**
	 * Returns the number of samples (not bytes) played by this device
	 */
	private native static long nGetPosition(long handle);

	/**
	 * Fill a list with device description objects.
	 */
	private native static void nFillDeviceNames(
			List<DirectAudioSinkDeviceEntry> list);

	/** @return a textual representation of this audio device */
	public String toString() {
		if (isOpen()) {
			return devName;
		} else {
			return "DirectAudioDevice";
		}
	}


}
