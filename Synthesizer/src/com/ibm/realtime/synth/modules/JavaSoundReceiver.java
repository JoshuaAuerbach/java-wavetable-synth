/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.modules;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import com.ibm.realtime.synth.engine.MidiEvent;
import com.ibm.realtime.synth.engine.MidiIn;
import static com.ibm.realtime.synth.utils.Debug.*;

public class JavaSoundReceiver implements Receiver {

	public static boolean DEBUG_JSMIDIIN_IO = false;
	
	/**
	 * The listeners that will receive incoming MIDI messages.
	 */
	private List<MidiIn.Listener> listeners = new ArrayList<MidiIn.Listener>();

	// private int ID;

	private MidiIn owner;

	/**
	 * If false, use the time of the incoming MIDI events, otherwise push MIDI
	 * events to the listener with time 0, i.e. "now"
	 */
	private boolean removeTimestamps = false;

	public JavaSoundReceiver(MidiIn owner) {
		this.owner = owner;
	}

	public void addListener(MidiIn.Listener L) {
		synchronized (listeners) {
			this.listeners.add(L);
		}
	}

	public void removeListener(MidiIn.Listener L) {
		synchronized (listeners) {
			this.listeners.remove(L);
		}
	}

	public void setID(int ID) {
		// this.ID = ID;
	}

	/**
	 * @return Returns the value of removeTimestamps.
	 */
	public boolean isRemovingTimestamps() {
		return removeTimestamps;
	}

	/**
	 * @param removeTimestamps
	 */
	public void setRemoveTimestamps(boolean removeTimestamps) {
		this.removeTimestamps = removeTimestamps;
	}

	/**
	 * Send a short message to the registered listeners.
	 * 
	 * @param microTime the original device time of the event in microseconds
	 * @param devID the internal ID of the MIDI device
	 * @param status MIDI status byte
	 * @param data1 1st MIDI data byte
	 * @param data2 2nd MIDI data byte
	 */
	private void dispatchMessage(long microTime, int status, int data1,
			int data2) {
		if (DEBUG_JSMIDIIN_IO) {
			debug("JavaSoundReceiver: time="+(microTime/1000)+"ms "
					+hexString(status, 2)+" "
					+hexString(data1, 2)+" "
					+hexString(data2, 2));
		}
		
		int channel;
		if (status < 0xF0) {
			// normal channel messages
			channel = status & 0x0F;
			status &= 0xF0;
		} else {
			// real time/system messages
			channel = 0;
		}
		long nanoTime;
		if (microTime == -1 || removeTimestamps) {
			if (removeTimestamps) {
				// let the receiver schedule!
				nanoTime = 0;
			} else {
				nanoTime = owner.getAudioTime().getNanoTime();
			}
		} else {
			nanoTime =
					(microTime * 1000L) + owner.getTimeOffset().getNanoTime();
		}
		synchronized (listeners) {
			for (MidiIn.Listener listener : listeners) {
				listener.midiInReceived(new MidiEvent(owner, nanoTime, channel,
						status, data1, data2));
			}
		}
	}

	/**
	 * Send a long message to the registered listeners.
	 * 
	 * @param microTime the original device time of the event in microseconds
	 * @param devID the internal ID of the MIDI device
	 * @param msg the actual message
	 */
	private void dispatchMessage(long microTime, byte[] msg) {
		if (DEBUG_JSMIDIIN_IO) {
			debug("JavaSoundReceiver: time="+(microTime/1000)+"ms "
					+" long msg, length="+msg.length);
		}
		long nanoTime;
		if (microTime == -1 || removeTimestamps) {
			if (removeTimestamps) {
				// let the receiver schedule!
				nanoTime = 0;
			} else {
				nanoTime = owner.getAudioTime().getNanoTime();
			}
		} else {
			nanoTime =
					(microTime * 1000L) + owner.getTimeOffset().getNanoTime();
		}
		synchronized (listeners) {
			for (MidiIn.Listener listener : listeners) {
				listener.midiInReceived(new MidiEvent(owner, nanoTime, msg));
			}
		}
	}

	public void send(MidiMessage message, long timeStamp) {
		// timestamp should be in microseconds
		if (message.getLength() <= 3) {
			if (message instanceof ShortMessage) {
				ShortMessage sm = (ShortMessage) message;
				dispatchMessage(timeStamp, sm.getStatus(), sm.getData1(),
						sm.getData2());
			} else {
				int data1 = 0;
				int data2 = 0;
				if (message.getLength() > 1) {
					byte[] msg = message.getMessage();
					data1 = msg[1] & 0xFF;
					if (message.getLength() > 2) {
						data2 = msg[2] & 0xFF;
					}
				}
				dispatchMessage(timeStamp, message.getStatus(), data1, data2);
			}
		} else {
			dispatchMessage(timeStamp, message.getMessage());
		}
	}

	public void close() {
		// nothing to do?
	}

}
