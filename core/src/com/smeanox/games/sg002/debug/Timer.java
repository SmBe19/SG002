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

	private Timer(){
		stack = new LinkedList<TimerEntry>();
	}

	public static Timer get(){
		if(singleton == null){
			singleton = new Timer();
		}
		return singleton;
	}

	public void start(String name){
		stack.addFirst(new TimerEntry(name, System.currentTimeMillis()));
		System.out.println("Task " + name + " started");
	}

	public void stop(){
		TimerEntry entry = stack.poll();
		long time = System.currentTimeMillis() - entry.time;
		System.out.println("Task " + entry.name + " ran for " + time + "ms");
	}

	private class TimerEntry{
		public String name;
		public long time;

		public TimerEntry(){}

		public TimerEntry(String name, long time){
			this.name = name;
			this.time = time;
		}
	}
}
