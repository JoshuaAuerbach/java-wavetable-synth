/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.soundfont2;

import com.ibm.realtime.synth.engine.*;

public class SoundFontPatch extends Patch {
	
	private int note;
	private int velocity;

	public SoundFontPatch(int note, int velocity, int bank, int program, SoundFontSample sample) {
		this.note = note;
		this.velocity = velocity;
		this.bank = bank;
		this.program = program;
		this.rootKey = sample.getOriginalPitch();
	}
	
	/**
	 * Set the note number. This is used to move a keynum generator override
	 * to the NoteInput instance. In that case, triggerNote and note are different.
	 * @param note
	 */
	void setNote(int note) {
		this.note = note;
	}
	
	int getNote() {
		return note;
	}

	/**
	 * Set the velocity. See setNote for explanation.
	 * @param vel
	 */
	void setVelocity(int vel) {
		this.velocity = vel;
	}
	
	int getVelocity() {
		return velocity;
	}
	
	void setRootKey(int rootKey) {
		this.rootKey = rootKey;
	}

	/**
	 * @param exclusiveLevel The exclusiveLevel to set.
	 */
	void setExclusiveLevel(int exclusiveLevel) {
		this.exclusiveLevel = exclusiveLevel;
	}
}
