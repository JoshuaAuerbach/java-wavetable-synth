/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.engine;

/**
 * Implemented by classes that need regular servicing. The MaintenanceThread
 * calls Serviceable objects in regular objects.
 * 
 * @author florian
 */
public interface Serviceable {
	public void service();
}
