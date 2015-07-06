/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.modules;

/**
 * A description class for direct MIDI input devices.
 * 
 * @author florian
 */
public class DirectMidiInDeviceEntry {

	private String devName;
	private String driverName;

	
	/**
	 * 
	 * @param devName
	 * @param driverName
	 */
	public DirectMidiInDeviceEntry(String devName, String driverName) {
		this.devName = devName;
		this.driverName = driverName;
	}



	/**
	 * @return the devName
	 */
	public String getDevName() {
		return devName;
	}



	/**
	 * @return the name
	 */
	public String getDriverName() {
		return driverName;
	}

	private String getPaddedDevName(int length) {
		StringBuffer result = new StringBuffer(devName);
		while (result.length() < length) {
			result.append(" ");
		}
		return result.toString();
	}
	
	public String getFullInfoString() {
		return getPaddedDevName(12)+": "+getDriverName();
	}

	public String toString() {
		return getDevName()+"|"+getDriverName();
	}

}
