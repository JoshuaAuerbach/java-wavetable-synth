/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.modules;

import static com.ibm.realtime.synth.utils.Debug.*;
import java.io.*;
import javax.sound.midi.*;
import com.ibm.realtime.synth.engine.*;

/**
 * A class to provide MIDI Input from a Standard MIDI File (SMF)
 * 
 * @author florian
 */
public class SMFMidiIn implements MidiIn, MetaEventListener {

	public static boolean DEBUG_SMF_MIDI_IN = false;

	/**
	 * The Sequencer to use for dispatching
	 */
	private Sequencer sequencer;

	/**
	 * The Sequence containing the MIDI file
	 */
	private Sequence sequence;

	/**
	 * The currently open file
	 */
	private File file;

	/**
	 * The Transmitter retrieved from the sequencer.
	 */
	private Transmitter seqTransmitter;

	/**
	 * The Receiver to use to dispatch the messages received from the
	 * Transmitter
	 */
	private JavaSoundReceiver receiver;

	/**
	 * the offset of the clock in nanoseconds (interface AdjustableAudioClock)
	 */
	private long clockOffset = 0;

	/**
	 * A listener to receive an event when playback stops
	 */
	private SMFMidiInListener stopListener;
	
	/**
	 * the device index
	 */
	private int devIndex = 0;

	/**
	 * Create an SMF MidiIn instance.
	 */
	public SMFMidiIn() {
		receiver = new JavaSoundReceiver(this);
		// do not use the overhead of using this clock.
		receiver.setRemoveTimestamps(true);
	}

	public void addListener(Listener L) {
		receiver.addListener(L);
	}

	public void removeListener(Listener L) {
		receiver.removeListener(L);
	}

	/**
	 * @return Returns the stopListener.
	 */
	public SMFMidiInListener getStopListener() {
		return stopListener;
	}

	/**
	 * @param stopListener The stopListener to set.
	 */
	public void setStopListener(SMFMidiInListener stopListener) {
		this.stopListener = stopListener;
	}

	/** if false, all MIDI events will have time stamp 0 */
	public void setTimestamping(boolean value) {
		receiver.setRemoveTimestamps(!value);
	}

	/** @return the current status of time stamping MIDI events */
	public boolean isTimestamping() {
		return !receiver.isRemovingTimestamps();
	}

	/**
	 * @return the currently loaded file, or <code>null</code>
	 */
	public File getFile() {
		return file;
	}

	public synchronized void open(File file) throws Exception {
		close();
		if (sequencer == null) {
			sequencer = MidiSystem.getSequencer(false);
			if (sequencer.getMaxTransmitters() == 0) {
				throw new Exception(
						"Cannot use system sequencer: does not provide Transmitters!");
			}
		}
		if (DEBUG_SMF_MIDI_IN) {
			debug("Using sequencer: " + sequencer.getDeviceInfo().getName());
		}
		if (DEBUG_SMF_MIDI_IN) {
			debug("Opening MIDI file: " + file);
		}
		sequence = MidiSystem.getSequence(file);
		if (DEBUG_SMF_MIDI_IN) {
			debug("Got MIDI sequence with " + sequence.getTracks().length
					+ " tracks. Duration: "
					+ format3(sequence.getMicrosecondLength() / 1000000.0)
					+ " seconds.");
		}
		seqTransmitter = sequencer.getTransmitter();
		seqTransmitter.setReceiver(receiver);
		sequencer.setSequence(sequence);
		// register a Meta Event listener that reacts on META event 47: End Of
		// File.
		sequencer.addMetaEventListener(this);
		sequencer.open();
		this.file = file;
		if (DEBUG_SMF_MIDI_IN) {
			debug("Sequencer opened and connected.");
		}
	}

	public synchronized void close() {
		if (seqTransmitter != null) {
			seqTransmitter.setReceiver(null);
		}
		if (sequence != null) {
			sequence = null;
		}
		if (sequencer != null) {
			sequencer.close();
			if (DEBUG_SMF_MIDI_IN) {
				debug("Closed Sequencer.");
			}
		}
		file = null;
	}

	public synchronized boolean isOpen() {
		return (sequencer != null) && (sequencer.isOpen());
	}

	public synchronized boolean isStarted() {
		return isOpen() && sequencer.isRunning();
	}

	public synchronized void start() {
		if (isOpen()) {
			// if at the end, rewind
			if (sequencer.getTickPosition() >= sequencer.getTickLength()) {
				rewind();
			}
			sequencer.start();
			if (DEBUG_SMF_MIDI_IN) {
				debug("Started sequencer. Current position: "
						+ format3(sequencer.getMicrosecondPosition() / 1000000.0));
			}
		}
	}

	public synchronized void stop() {
		if (isOpen()) {
			sequencer.stop();
			if (DEBUG_SMF_MIDI_IN) {
				debug("Stopped sequencer. Current position: "
						+ format3(sequencer.getMicrosecondPosition() / 1000000.0));
			}
		}
	}

	public synchronized void rewind() {
		if (isOpen()) {
			sequencer.setTickPosition(0);
		}
	}

	public synchronized void windSeconds(double seconds) {
		if (isOpen()) {
			long newMicroPos = sequencer.getMicrosecondPosition() + ((long) (seconds * 1000000.0));
			if (newMicroPos < 0) {
				newMicroPos = 0;
			} else if (newMicroPos > sequencer.getMicrosecondLength()) {
				newMicroPos = sequencer.getMicrosecondLength();
			}
			sequencer.setMicrosecondPosition(newMicroPos);
		}
	}

	/**
	 * Set the playback position to the percentage
	 * @param percent 0..100
	 */
	public synchronized void setPositionPercent(double percent) {
		if (isOpen()) {
			long tickLen = sequencer.getTickLength();
			long newTickPos = (long) (tickLen * percent / 100.0);
			if (newTickPos < 0) {
				newTickPos = 0;
			} else if (newTickPos > tickLen) {
				newTickPos = tickLen-1;
			}
			sequencer.setTickPosition(newTickPos);
		}
	}

	/**
	 * Get the playback position expressed as a percentage
	 * @return percent 0..100
	 */
	public synchronized double getPositionPercent() {
		if (isOpen()) {
			double tickLen = (double) sequencer.getTickLength();
			double tickPos = (double) sequencer.getTickPosition();
			double percent = (long) (tickPos * 100.0 / tickLen);
			return percent;
		}
		return 0.0;
	}

	public synchronized long getPlaybackPosMillis() {
		if (isOpen()) {
			return sequencer.getMicrosecondPosition() / 1000;
		}
		return 0;
	}

	public synchronized String getPlaybackPosBars() {
		if (isOpen()) {
			long tickPos = sequencer.getTickPosition(); 
			// last number is "frames"
			int frames = (int) tickPos % sequence.getResolution();
			// align frames to a 12 scale
			frames = ((frames * 12) / sequence.getResolution())+1;
			String sFrames;
			if (frames < 10) {
				sFrames = "0"+frames;
			} else {
				sFrames = Integer.toString(frames);
			}
			tickPos /= sequence.getResolution();
			// second number is beats
			int beat = (int) ((tickPos % 4)+1);
			// first number is bars, assume a 4/4 signature
			long bars = (tickPos / 4)+1;
			return Long.toString(bars)+":"+beat+"."+sFrames;
		}
		return "";
	}

	// interface MetaEventListener
	public void meta(MetaMessage event) {
		if (event.getType() == 47) {
			if (stopListener != null) {
				stopListener.onMidiPlaybackStop();
			}
		}
	}

	// interface AudioClock
	public AudioTime getAudioTime() {
		if (sequencer != null) {
			return new AudioTime((sequencer.getMicrosecondPosition() * 1000L)
					+ clockOffset);
		}
		return new AudioTime(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.realtime.synth.engine.AdjustableAudioClock#getTimeOffset()
	 */
	public AudioTime getTimeOffset() {
		return new AudioTime(clockOffset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.realtime.synth.engine.AdjustableAudioClock#setTimeOffset(com.ibm.realtime.synth.engine.AudioTime)
	 */
	public void setTimeOffset(AudioTime offset) {
		this.clockOffset = offset.getNanoTime();
	}

	public int getInstanceIndex() {
		return devIndex;
	}
	
	public void setDeviceIndex(int index) {
		this.devIndex = index;
	}

	public String toString() {
		if (isOpen()) {
			return "SMFMidiIn " + file;
		}
		return "SMFMidiIn";
	}


	
}
