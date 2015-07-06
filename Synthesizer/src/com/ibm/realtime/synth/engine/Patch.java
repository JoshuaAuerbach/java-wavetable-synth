/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.engine;

/**
 * Description data for a patch in the soundbank. This data is typically static data,
 * i.e. will not change during the lifetime of this patch object.
 * <p>
 * Specific soundbank implementations should use descendants of this class for specific
 * Patch instances.
 * 
 * @author florian
 * 
 */
public abstract class Patch {
	/**
	 * This is the bank in which the patch plays.
	 */
	protected int bank;

	/**
	 * The program aka preset number [0..127]
	 */
	protected int program;

	//protected double volumeFactor = 1.0;

	protected boolean selfExclusive = true;

	/**
	 * All playing notes in the same program and bank 
	 * with this exclusive level cannot be played at the same time.
	 */
	protected int exclusiveLevel = 0;

	/**
	 * the data's root key note, i.e. which note is recorded
	 */
	protected int rootKey;

	/**
	 * @return Returns the bank number.
	 */
	public int getBank() {
		return bank;
	}

	/**
	 * @return Returns the isSelfExclusive.
	 */
	public boolean isSelfExclusive() {
		return selfExclusive;
	}

	/**
	 * @return Returns the dataNote.
	 */
	public int getRootKey() {
		return rootKey;
	}

	/**
	 * @return Returns the exclusiveLevel.
	 */
	public int getExclusiveLevel() {
		return exclusiveLevel;
	}

	/**
	 * @return Returns the program.
	 */
	public int getProgram() {
		return program;
	}

}
