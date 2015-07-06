/*
 * (C) Copyright IBM Corp. 2005, 2008. All Rights Reserved
 */
package com.ibm.realtime.synth.utils;

import static com.ibm.realtime.synth.utils.Debug.debug;
import static com.ibm.realtime.synth.utils.Debug.error;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * A thread to execute the listener asynchronously.
 */
public class AsynchExec<T> implements Runnable {

	public static boolean DEBUG_ASYNCH_EXEC = false;
	
	private Listener<T> listener;
	private String name;
	
	/**
	 * The list of asynchronous events
	 */
	private Queue<T> execs = new PriorityBlockingQueue<T>();

	/**
	 * The event dispatcher thread
	 */
	private Thread thread;

	private volatile boolean stopped = false;

	public AsynchExec(Listener<T> listener, String name) {
		this.name = name;
		this.listener = listener;
	}

	public synchronized void start() {
		stop();
		stopped = false;
		thread = new Thread(this, name);
		thread.setDaemon(true);
		thread.start();
	}

	public synchronized void stop() {
		if (!stopped && thread != null && thread.isAlive()) {
			stopped = true;
			synchronized (execs) {
				execs.notifyAll();
			}
			try {
				thread.join();
			} catch (InterruptedException ie) {
				error(ie);
			}
		}
		thread = null;
	}
	
	public synchronized boolean isStarted() {
		return !stopped;
	}

	/**
	 * Call the registered listener asynchronously with the specified parameter.
	 * @param o the object passed on to the listener as parameter
	 */
	public void invokeLater(T o) {
		execs.offer(o);
		synchronized (execs) {
			execs.notifyAll();
		}
	}

	public void run() {
		if (DEBUG_ASYNCH_EXEC) {
			debug(name+": start.");
		}
		try {
			while (!stopped) {
				if (execs.isEmpty()) {
					synchronized (execs) {
						execs.wait();
					}
				}
				while (!stopped && !execs.isEmpty()) {
					T event = execs.poll();
					if (listener != null) {
						listener.onAsynchronousExecution(event);
					}
				}
			}
		} catch (InterruptedException ie) {
			error(ie);
		}
		if (DEBUG_ASYNCH_EXEC) {
			debug(name+": exit.");
		}
	}
	
	public interface Listener<T> {
		public void onAsynchronousExecution(T event);
	}
}

