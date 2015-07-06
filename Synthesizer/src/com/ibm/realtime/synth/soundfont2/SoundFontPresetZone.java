/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.soundfont2;

/**
 * A zone on preset level. The preset zones typically define the instrument to
 * be used in this preset for this zone.
 * 
 * @author florian
 * 
 */
public class SoundFontPresetZone extends SoundFontZone {

	/**
	 * The associated instrument for this zone. Will be null for global zones or
	 * instrument zones.
	 */
	private SoundFontInstrument instrument;

	/**
	 * Constructor for a preset zone
	 * 
	 * @param generators
	 * @param modulators
	 * @param instrument
	 */
	public SoundFontPresetZone(SoundFontGenerator[] generators,
			SoundFontModulator[] modulators, SoundFontInstrument instrument) {
		super(generators, modulators);
		this.instrument = instrument;
	}

	/**
	 * @return Returns the instrument.
	 */
	public SoundFontInstrument getInstrument() {
		return instrument;
	}

	public boolean isGlobalZone() {
		return (keyMin >= 0) && (instrument == null);
	}

	public String toString() {
		String ret = super.toString();
		if (instrument != null) {
			ret += " using inst: " + instrument.getName();
		}
		return ret;
	}
}
