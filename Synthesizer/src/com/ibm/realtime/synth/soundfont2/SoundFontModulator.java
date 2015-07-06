/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.soundfont2;

/**
 * Definitions for SoundFont Modulators.
 * 
 * @author florian
 * 
 */
public class SoundFontModulator {
	private int sourceOp;
	private int destOp;
	private short amount;
	private int sourceOpAmount;
	private int transform;

	public SoundFontModulator(int sourceOp, int destOp, short amount,
			int sourceOpAmount, int transform) {
		this.sourceOp = sourceOp;
		this.destOp = destOp;
		this.amount = amount;
		this.sourceOpAmount = sourceOpAmount;
		this.transform = transform;
	}

	/**
	 * @return Returns the amount.
	 */
	public short getAmount() {
		return amount;
	}

	/**
	 * @return Returns the destOp.
	 */
	public int getDestOp() {
		return destOp;
	}

	/**
	 * @return Returns the sourceOp.
	 */
	public int getSourceOp() {
		return sourceOp;
	}

	/**
	 * @return Returns the sourceOpAmount.
	 */
	public int getSourceOpAmount() {
		return sourceOpAmount;
	}

	/**
	 * @return Returns the transform.
	 */
	public int getTransform() {
		return transform;
	}

	/**
	 * @return true if the source modulator is bipolar (i.e. range -1...0...+1).
	 *         Otherwise, the modulator is unipolar (range 0..1)
	 */
	public static String modulator2string(int modulator) {
		int index = modulator & 0x7F; // bit 0..6
		boolean isMIDICtrl = (modulator & 0x80) != 0; // bit 7
		boolean isReversed = (modulator & 0x100) != 0; // bit 8
		boolean isBipolar = (modulator & 0x200) != 0; // bit 9
		int type = (modulator & 0xFC00) >> 10; // bits 10..15
		String ret = "";
		if (isMIDICtrl) {
			ret += "MIDI Controller " + index;
		} else {
			switch (index) {
			case 0:
				ret += "No Controller";
				break;
			case 2:
				ret += "NoteOn Velocity";
				break;
			case 3:
				ret += "NoteOn Key Number";
				break;
			case 10:
				ret += "PolyPressure";
				break;
			case 13:
				ret += "ChannelPressure";
				break;
			case 14:
				ret += "PitchWheel";
				break;
			case 16:
				ret += "PitchWheelSensitivity";
				break;
			default:
				ret += "Unknown General Controller";
				break;
			}
		}
		if (isReversed) {
			if (isBipolar) {
				ret += "[1..0..-1]";
			} else {
				ret += "[1..0]";
			}
		} else {
			if (isBipolar) {
				ret += "[-1..0..1]";
			} else {
				ret += "[0..1]";
			}
		}
		ret += ",";
		switch (type) {
		case 0:
			ret += "linear";
			break;
		case 1:
			ret += "concave";
			break;
		case 2:
			ret += "convex";
			break;
		case 3:
			ret += "switch";
			break;
		default:
			ret += "(unknown continuity)";
			break;
		}
		return ret;
	}

	public static String transform2string(int transform) {
		switch (transform) {
		case 0:
			return "linear transform";
		}
		return "unknown transform " + transform;
	}

	public String toString() {
		return "Modulator: source: " + modulator2string(sourceOp) + " dest: "
				+ SoundFontGenerator.generator2String(destOp) + " amount="
				+ amount + " sourceAmount=" + modulator2string(sourceOpAmount)
				+ " transform=" + transform2string(transform);
	}
}
