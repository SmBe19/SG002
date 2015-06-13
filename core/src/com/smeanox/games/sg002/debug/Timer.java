package com.smeanox.games.sg002.debug;

import java.util.LinkedList;

/**
 * Debug utility to measure time used for a given task
 *
 * @author Benjamin Schmid
 */
public class Timer {
	private static Timer singleton;

	private LinkedList<TimerEntry> stack;

	/**
	 * Create a new instance
	 */
	private Timer() {
		stack = new LinkedList<TimerEntry>();
	}

	/**
	 * Returns the singleton instance
	 *
	 * @return the singleton instance
	 */
	public static Timer get() {
		if (singleton == null) {
			singleton = new Timer();
		}
		return singleton;
	}

	/**
	 * Push a new Timer on the stack
	 *
	 * @param name the name of the timer
	 */
	public void start(String name) {
		stack.addFirst(new TimerEntry(name, System.currentTimeMillis()));
		System.out.println("Task " + name + " started");
	}

	/**
	 * Stop the last not yet stopped Timer and print the duration
	 */
	public void stop() {
		TimerEntry entry = stack.poll();
		long time = System.currentTimeMillis() - entry.time;
		System.out.println("Task " + entry.name + " ran for " + time + "ms");
	}

	/**
	 * Class to hold a Timer
	 */
	private class TimerEntry {
		public String name;
		public long time;

		/**
		 * Create a new instance
		 */
		public TimerEntry() {
		}

		/**
		 * Create a new instance
		 *
		 * @param name the name of the timer
		 * @param time the time the timer was started
		 */
		public TimerEntry(String name, long time) {
			this.name = name;
			this.time = time;
		}
	}
}
