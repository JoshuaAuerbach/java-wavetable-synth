/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * A Starter app for Harmonicon: manage all components
 * used by Harmonicon.
 * 
 * @author florian
 *
 */
public class Starter extends JPanel {
	// implements ItemListener, ActionListener, ChangeListener

	private static final String NAME = "Harmonicon Starter";
	
	private JFrame frame;
	
	public Starter(String[] args) {
		createFrame();
		createGUI();
		this.setOpaque(true);
		frame.setContentPane(this);
		frame.setVisible(true);
		init();
	}
	
	private void createGUI() {
		
	}
	
	private void init() {
		
	}
	
	public void close() {
		
	}

	private void createFrame() {
		JFrame.setDefaultLookAndFeelDecorated(true);
	Toolkit.getDefaultToolkit().setDynamicLayout(true);
	System.setProperty("sun.awt.noerasebackground", "true");
	frame = new JFrame();
	frame.setTitle(NAME);
	WindowAdapter windowAdapter = new WindowAdapter() {
		public void windowClosing(WindowEvent we) {
				if (frame != null) {
					//Dimension d = frame.getSize();
					//pane.setProperty("frameWidth", d.width);
					//pane.setProperty("frameHeight", d.height);
					frame.setVisible(false);
				}
				Starter.this.close();
			
			System.exit(0);
		}
	};
	frame.addWindowListener(windowAdapter);
	//frame.setSize(new Dimension(getIntProperty("frameWidth", 400),
	//		getIntProperty("frameHeight", 500)));
	
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Starter(args);
	}

	private static final long serialVersionUID = 0;
}
