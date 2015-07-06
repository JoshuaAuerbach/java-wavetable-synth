/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.test;

import java.io.IOException;
import java.util.*;

import com.ibm.realtime.synth.modules.*;
import com.ibm.realtime.synth.engine.*;

/**
 * Simple test program for the DirectMidiIn class
 * @author florian
 */
public class DirectMidiInTest {

	
	public static void main(String[] args) {
		
		List<DirectMidiInDeviceEntry> list = DirectMidiIn.getDeviceEntryList();
		List<DirectMidiIn> openList = new ArrayList<DirectMidiIn>();
		
		if (list.size() == 0) {
		    System.out.println("No MIDI devices available.");
		} else {
		    System.out.println("Available MIDI devices:");
			for (int i = 0; i<list.size(); i++) {
				

			    System.out.print(""+(i+1)+". "+list.get(i));
				DirectMidiIn dmi = new DirectMidiIn();
				try {
					dmi.addListener(new MidiIn.Listener() {
						public void midiInReceived(MidiEvent me) {
						    System.out.println("IN "+me.getSource()+": "+me.getTime()+": "+me.getStatus()+" "+me.getData1()+" "+me.getData2());
						}
					});
					// for now, only open the last 2 devices
					if (i >= list.size()-2) {
					    dmi.open(list.get(i).getDevName());
					    openList.add(dmi);
						System.out.print(" [opened]");
					}
					System.out.println();
				} catch (Exception e) {
				    System.out.println();
				    e.printStackTrace();
				}
			}
			System.out.println("Press ENTER to quit");
			try {
				System.in.read();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			for (DirectMidiIn dmi:openList) {
				System.out.println("Closing "+dmi);
				dmi.close();
			}
		}
	}
	
}
