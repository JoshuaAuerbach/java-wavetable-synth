/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.soundfont2;

import java.util.*;

import com.ibm.realtime.synth.engine.Soundbank;

/**
 * A representation of a bank. A bank has a bank ID and 128 programs (presets).
 * 
 * @author florian
 * 
 */
public class SoundFontBank implements Comparable<SoundFontBank>, Soundbank.Bank {

	private int bank;
	private SoundFontPreset[] presets;

	public SoundFontBank(int bank) {
		this.bank = bank;
	}

	/**
	 * @return Returns the bank number.
	 */
	public int getMidiNumber() {
		return bank;
	}

	/**
	 * @return Returns an array of 128 presets (aka instruments). Some elements may be null.
	 */
	public SoundFontPreset[] getPresets() {
		if (presets == null) {
			presets = new SoundFontPreset[128];
		}
		return presets;
	}
	
	/**
	 * @return the list of all existing presets in this bank, up to 128.
	 */
	public List<Soundbank.Instrument> getInstruments() {
		List<Soundbank.Instrument> list = new ArrayList<Soundbank.Instrument>(128);
		SoundFontPreset[] ps = getPresets();
		for (SoundFontPreset p : ps) {
			if (p != null) {
				list.add(p);
			}
		}
		return list;
	}

	/**
	 * @return Returns the specified preset, or null if the preset does not exist in this bank.
	 */
	public SoundFontPreset getPreset(int index) {
		return getPresets()[index];
	}

	/**
	 * Set a specified preset.
	 * @param index the index of the preset [0..127] 
	 */
	public void setPreset(int index, SoundFontPreset preset) {
		getPresets()[index] = preset;
	}

	/**
	 * From an ordered list of SoundFontBanks, find the one identified by thisBank.
	 * 
	 * @return the index of the found SoundFontBank, or -1 if not found
	 */
	public static int findBank(List<SoundFontBank> banks, int thisBank) {
		// TODO: if more than 5 banks, use binary search
		return banks.indexOf(new SoundFontBank(thisBank));
	}

	public boolean equals(SoundFontBank sfb) {
		return (bank == sfb.getMidiNumber());
	}

	public boolean equals(Object o) {
		if (o instanceof SoundFontBank) {
			return (((SoundFontBank) o).getMidiNumber() == bank);
		} else if (o instanceof Integer) {
			return (((Integer) o).intValue() == bank);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(SoundFontBank arg0) {
		return bank - arg0.getMidiNumber();
	}

}
