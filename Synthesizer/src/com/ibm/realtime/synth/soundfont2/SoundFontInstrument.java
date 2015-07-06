/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.soundfont2;

import java.util.List;
import java.util.ArrayList;

/**
 * Container for a SoundFont instrument with its zones.
 * 
 * @author florian
 * 
 */
public class SoundFontInstrument {
	private String name;

	private SoundFontInstrumentZone[] zones;

	public SoundFontInstrument(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the zones.
	 */
	public SoundFontInstrumentZone[] getZones() {
		return zones;
	}

	/**
	 * @param zones The zones to set.
	 */
	protected void setZones(SoundFontInstrumentZone[] zones) {
		this.zones = zones;
	}

	/**
	 * @return a matching zone, or null if none found. This method does not return zones without a sample.
	 */
	public List<SoundFontInstrumentZone> getZones(int note, int vel) {
		List<SoundFontInstrumentZone> result = null;
		for (SoundFontInstrumentZone zone : zones) {
			if (zone.getSample()!=null && zone.matches(note, vel)) {
				if (result == null) {
					result = new ArrayList<SoundFontInstrumentZone>(2);
				}
				result.add(zone);
			}
		}
		return result;
	}
	
	/**
	 * @return the global zone of this instrument, or null if none exists
	 */
	public SoundFontInstrumentZone getGlobalZone() {
		if (zones.length > 0 && zones[0].isGlobalZone()) {
			return zones[0];
		}
		return null;
	}

	public String toString() {
		return "Instrument: name=" + name + " with " + zones.length + " zones.";
	}
}
