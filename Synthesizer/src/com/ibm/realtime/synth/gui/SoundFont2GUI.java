/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import static com.ibm.realtime.synth.utils.Debug.debug;

/**
 * A quick&dirty visual interface for the SoundFont 2 synthesizer engine.
 * 
 * @author florian
 * 
 */
public class SoundFont2GUI {
	private static final String NAME = "Harmonicon";

	public SoundFont2GUI() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		Toolkit.getDefaultToolkit().setDynamicLayout(true);
		System.setProperty("sun.awt.noerasebackground", "true");
		final JFrame frame = new JFrame();
		frame.setTitle(NAME);
		final SFGPane pane = new SFGPane(NAME);
		WindowAdapter windowAdapter = new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				if (pane != null) {
					if (frame != null) {
						Dimension d = frame.getSize();
						pane.setProperty("frameWidth", d.width);
						pane.setProperty("frameHeight", d.height);
						frame.setVisible(false);
					}
					pane.close();
				}
				debug(NAME + " exit.");
				System.exit(0);
			}
		};
		frame.addWindowListener(windowAdapter);
		frame.setSize(new Dimension(pane.getIntProperty("frameWidth", 600),
				pane.getIntProperty("frameHeight", 500)));
		
		pane.createGUI();
		frame.getContentPane().add(pane);
		frame.setVisible(true);
		pane.init();
		pane.start();
	}

	public static void main(String[] args) {
		try {
			new SoundFont2GUI();
		} catch (Throwable t) {
			System.err.println(NAME + ": Exception occurred in main():");
			t.printStackTrace();
			System.exit(1);
		}
	}
}
