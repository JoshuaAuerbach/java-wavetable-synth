/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.gui;

import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import static com.ibm.realtime.synth.utils.Debug.*;

public class HarmoniconGUI {

	private static HarmoniconPane harmoniconPane = null;
	private static Display display = null;
	private static Shell shell = null;
	
	private static void closeWindowImpl(boolean disposeWindow) {
		if (!display.isDisposed()) {
			if (harmoniconPane != null) {
				if (shell != null && !shell.isDisposed()) {
					Point d = shell.getSize();
					harmoniconPane.setProperty("frameWidth", d.x);
					harmoniconPane.setProperty("frameHeight", d.y);
				}
				harmoniconPane.close(); // Kill harmonicon threads
			}
		}
		if (disposeWindow) {
			if (shell != null && !shell.isDisposed()) {
				shell.dispose();
			}
			if (display != null && !display.isDisposed()) {
				display.dispose();
			}
		}
	}

	public static void main(String[] args) {
		try {
			harmoniconPane = new HarmoniconPane("Harmonicon", args);
			display = new Display();
			shell = new Shell(display);
			shell.setSize(800, 800);
			shell.setText("Harmonicon");
			shell.setLayout(new FillLayout());
			harmoniconPane.createPartControl(shell);
			shell.addShellListener(new ShellListener() {
				public void shellActivated(ShellEvent e) {
				}

				public void shellClosed(ShellEvent e) {
					closeWindowImpl(false);
				}

				public void shellDeactivated(ShellEvent e) {
				}

				public void shellDeiconified(ShellEvent e) {
				}

				public void shellIconified(ShellEvent e) {
				}
			});
			harmoniconPane.setCloseListener(new HarmoniconPane.CloseListener() {
				public void closeWindow() {
						closeWindowImpl(true);
				}
			});
			shell.setSize(harmoniconPane.getIntProperty("frameWidth", 400),
					harmoniconPane.getIntProperty("frameHeight", 500));
			shell.open();
			harmoniconPane.init(); // Init properties
			harmoniconPane.start(); // Start rendering threads
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			// make sure we don't call dispose() before the harmoniconPane
			// has finished the close() operation!
			harmoniconPane.close();
			if (!display.isDisposed()) {
				display.dispose();
			}
		} catch (Throwable t) {
			error(t);
		}
	}
}
