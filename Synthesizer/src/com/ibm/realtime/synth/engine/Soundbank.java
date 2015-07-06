/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.engine;

import java.util.List;

/**
 * Master interface for soundbank implementations.
 * 
 * @author florian
 * 
 */
public interface Soundbank {
	
	/**
	 * Return the name of this soundbank
	 */
	public String getName();
	
	/**
	 * This method is called when a note is to be played. The implementation
	 * needs to find the correct patch from the soundbank for the channel, given
	 * in the descriptor, matching the given note and velocity.
	 * <p>
	 * The soundbank can also create linked instances of NoteInput in this method.
	 * 
	 * @param note the note to be played (0..127)
	 * @param vel the velocity at which the note is played (1..127)
	 * @return a (possibly linked) instance of NoteInput, or null if no instrument exists for
	 *    this channel's program/bank, this note, or this velocity.
	 */
	public NoteInput createNoteInput(Synthesizer.Params params,
			AudioTime time, MidiChannel channel, int note, int vel);
	
	/**
	 * Returns a list of the existing Banks
	 */
	public List<Bank> getBanks();
	
	/**
	 * A descriptor of a Bank in a soundbank
	 */
	public interface Bank {
		/**
		 * @return the MIDI bank number of this bank [0..16383]
		 */
		public int getMidiNumber();
		/**
		 * @return the list of instruments in this bank. Up to 128 instruments.
		 */
		public List<Instrument> getInstruments();
	}

	/**
	 * A descriptor of an Instrument in a Bank
	 */
	public interface Instrument {
		/**
		 * @return the MIDI program number of this instrument [0..127]
		 */
		public int getMidiNumber();
		/**
		 * @return the name of this instrument
		 */
		public String getName();
	}
}
