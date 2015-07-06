/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.engine;

import java.lang.reflect.*;
import static com.ibm.realtime.synth.utils.Debug.*;

/**
 * A centralized class for creation of threads. If available, real time threads
 * will be used.
 * 
 * @author florian
 */
public class ThreadFactory {

	protected static boolean useRTSJ = true;

	public static boolean DEBUG_THREAD_FACTORY = false;

	private static final boolean USE_PRIORITY = true;

	private static boolean bHasRealtimeThread = false;
	private static boolean bCouldSetRealtimeThreadPriority = false;
	
	
	/**
	 * Create a thread that excutes the given runner. If priority is larger than
	 * 7, a realtime thread is tried to be used. The thread immediately starts
	 * execution.
	 * 
	 * @param runner the Runnable to run in the new thread
	 * @param name the name of the thread, usually for debugging purposes only
	 * @param priority the priority from 0 (lowest) to 28 (highest)
	 * @return the thread object
	 */
	public static Thread createThread(Runnable runner, String name, int priority) {
		Thread t = null;

		if (useRTSJ) {
			t = createRealTimeThread(runner, name, priority);
		}

		if (t == null) {
			t = createJavaThread(runner, name, priority);
		}
		t.start();
		return t;
	}

	/**
	 * Given a priority from 0 to 28, return a priority level suitable for
	 * Thread.setPriority.
	 * 
	 * @param priority the priority in the range 0..28
	 * @return the Java Thread priority
	 */
	private static int priority2javaPriority(int priority) {
		int javaPrio;
		if (priority >= 28) {
			javaPrio = Thread.MAX_PRIORITY;
		} else if (priority <= 0) {
			javaPrio = Thread.MIN_PRIORITY;
		} else {
			javaPrio = Thread.MIN_PRIORITY
					+ (((Thread.MAX_PRIORITY - Thread.MIN_PRIORITY) * priority) / 28);
		}
		return javaPrio;
	}

	private static Thread createJavaThread(Runnable runner, String name,
			int priority) {
		Thread res = new Thread(runner);
		res.setName(name);
		int javaPriority = priority2javaPriority(priority);
		res.setPriority(javaPriority);
		if (DEBUG_THREAD_FACTORY) {
			debug("Created NON-RTSJ thread '" + name + "' logical priority "
					+ priority + "; Java priority " + javaPriority);
		}
		return res;
	}

	public static void setUseRTSJ(boolean value) {
		useRTSJ = value;
	}

	public static boolean couldSetRealtimeThreadPriority() {
		return bCouldSetRealtimeThreadPriority;
	}

	public static boolean hasRealtimeThread() {
		return bHasRealtimeThread;
	}

	/**
	 * Create a realtime thread.
	 * 
	 * @param priority 0..28
	 * @return the real time thread, or null on error
	 */
	@SuppressWarnings("unchecked")
	private static Thread createRealTimeThread(Runnable runner, String name,
			int priority) {
		Thread res = null;

		try {
			Class cRealtimeThread = Class.forName("javax.realtime.RealtimeThread");
			Class cSchedulingParameters = Class.forName("javax.realtime.SchedulingParameters");
			Class cReleaseParameters = Class.forName("javax.realtime.ReleaseParameters");
			Class cMemoryParameters = Class.forName("javax.realtime.MemoryParameters");
			Class cMemoryArea = Class.forName("javax.realtime.MemoryArea");
			Class cProcessingGroupParameters = Class.forName("javax.realtime.ProcessingGroupParameters");
			Constructor rtCons = cRealtimeThread.getConstructor(new Class[] {
					cSchedulingParameters, cReleaseParameters,
					cMemoryParameters, cMemoryArea, cProcessingGroupParameters,
					Runnable.class
			});
			// get an instance of PriorityParameters and set the priority
			Class cPriorityParameters = Class.forName("javax.realtime.PriorityParameters");
			// get the constructor with one int parameter
			Constructor ppCons = cPriorityParameters.getConstructor(new Class[] {
				int.class
			});

			while (priority > 0) {
				try {
					Object pp = null;
					if (USE_PRIORITY) {
						if (priority >= 1) {
							pp = ppCons.newInstance(new Object[] {
								new Integer(priority)
							});
						}
					}
					res = (Thread) rtCons.newInstance(new Object[] {
							pp, null, null, null, null, runner
					});
					res.setName(name);
					bHasRealtimeThread = true;
					bCouldSetRealtimeThreadPriority = (pp != null);
					if (DEBUG_THREAD_FACTORY) {
						if (pp == null) {
							debug("Created RTSJ thread '" + name
									+ "' with no priority setting");
						} else {
							debug("Created RTSJ thread '" + name
									+ "' priority=" + priority);
						}
					}
					break;
				} catch (Exception e) {
					priority--;
				}
			}
		} catch (Exception e) {
			if (DEBUG_THREAD_FACTORY) {
				error("Unable to create RTSJ thread: " + e);
			}
		}

		return res;
	}

}
