package com.smeanox.games.sg002.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Log the outcome of a tournament
 *
 * @author Benjamin Schmid
 */
public class TournamentLogger {

	private PrintWriter eventLogWriter;
	private String logFolder = "";
	private String eventLogPath;
	private boolean printStdOut;
	private boolean printStdErr;

	public TournamentLogger(){
		printStdOut = true;
		printStdErr = true;
	}

	/**
	 * Create a logger that does not output anything
	 * @return the logger
	 */
	public static TournamentLogger createDummyLogger(){
		TournamentLogger logger = new TournamentLogger();
		logger.setEventLogPath(null);
		logger.setPrintStdOut(false);
		logger.setPrintStdErr(false);
		return logger;
	}

	public String getLogFolder() {
		return logFolder;
	}

	public void setLogFolder(String logFolder) {
		this.logFolder = logFolder;
		if(this.logFolder.length() > 0 && !this.logFolder.endsWith(File.separator)){
			this.logFolder += File.separator;
		}
		File folder = new File(this.logFolder);
		if(!folder.exists()){
			folder.mkdirs();
		}
	}

	public boolean isPrintStdOut() {
		return printStdOut;
	}

	public void setPrintStdOut(boolean printStdOut) {
		this.printStdOut = printStdOut;
	}

	public boolean isPrintStdErr() {
		return printStdErr;
	}

	public void setPrintStdErr(boolean printStdErr) {
		this.printStdErr = printStdErr;
	}

	public String getEventLogPath() {
		return eventLogPath;
	}

	public void setEventLogPath(String eventLogPath) {
		this.eventLogPath = eventLogPath;
		if(eventLogWriter != null){
			eventLogWriter.close();
		}
		if(eventLogPath == null){
			eventLogWriter = null;
		} else {
			try {
				eventLogWriter = new PrintWriter(logFolder + eventLogPath);
			} catch (FileNotFoundException e) {
				eventLogWriter = null;
			}
		}
	}

	/**
	 * Close all files
	 */
	public void close(){
		setEventLogPath(null);
	}

	/**
	 * Log outcome of the tournament
	 * @param s the string to log
	 */
	public synchronized void event(String s){
		if(printStdOut){
			System.out.println(s);
		}
		if(eventLogPath != null){
			eventLogWriter.println(s);
		}
	}

	/**
	 * Log the progress of simulating the game
	 * @param s the string to log
	 */
	public synchronized void progress(String s){
		if(printStdErr){
			System.err.println("[PROGRESS] " + s);
		}
	}
}
