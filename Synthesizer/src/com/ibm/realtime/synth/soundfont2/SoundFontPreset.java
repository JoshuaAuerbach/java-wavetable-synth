/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.soundfont2;

import com.ibm.realtime.synth.engine.Soundbank;
import java.util.List;
import java.util.ArrayList;

/**
 * Class to hold all data for a Preset in a soundfont file.
 * In MIDI terms, a preset is an instrument, patch, or program.
 * 
 * @author florian
 * 
 */
public class SoundFontPreset implements Soundbank.Instrument {
	private String name;
	private int program;
	private int bank;

	private SoundFontPresetZone[] zones;

	public SoundFontPreset(String name, int program, int bank) {
		this.name = name;
		this.program = program;
		this.bank = bank;
	}

	/**
	 * @return Returns the bank.
	 */
	public int getBank() {
		return bank;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the program number.
	 */
	public int getMidiNumber() {
		return program;
	}

	/**
	 * @return Returns the preset zones.
	 */
	public SoundFontPresetZone[] getZones() {
		return zones;
	}

	/**
	 * @param zones The preset zones to set.
	 */
	protected void setZones(SoundFontPresetZone[] zones) {
		this.zones = zones;
	}

	/**
	 * @return the matching zones, or null if none found. This method does not
	 *         return zones without attached instruments.
	 */
	public List<SoundFontPresetZone> getZones(int note, int vel) {
		List<SoundFontPresetZone> result = null;
		for (SoundFontPresetZone zone : zones) {
			if (zone.getInstrument()!=null && zone.matches(note, vel)) {
				if (result == null) {
					result = new ArrayList<SoundFontPresetZone>(2);
				}
				result.add(zone);
			}
		}
		return result;
	}

	/**
	 * @return the global zone of this preset, or null if none exists
	 */
	public SoundFontPresetZone getGlobalZone() {
		if (zones.length > 0 && zones[0].isGlobalZone()) {
			return zones[0];
		}
		return null;
	}

	public String toString() {
		String ret = "Preset: name=" + name + " program=" + program + " bank="
				+ bank;
		if (zones != null) {
			ret += " with " + zones.length + " zones";
		}
		return ret;
	}
}
