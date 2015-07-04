package com.smeanox.games.sg002.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Log the game for replay and log the behaviour of AIs
 *
 * @author Benjamin Schmid
 */
public class GameLogger {

	private PrintWriter gameLogWriter, behaviourLogWriter;
	private String logFolder = "";
	private String gameLogPath;
	private String behaviourLogPath;
	private boolean printStdOut;
	private boolean printStdErr;

	public GameLogger(){
		printStdOut = true;
		printStdErr = true;
	}

	/**
	 * Create a logger that does not output anything
	 * @return the logger
	 */
	public static GameLogger createDummyLogger(){
		GameLogger logger = new GameLogger();
		logger.setGameLogPath(null);
		logger.setBehaviourLogPath(null);
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

	public String getGameLogPath() {
		return gameLogPath;
	}

	public void setGameLogPath(String gameLogPath) {
		this.gameLogPath = gameLogPath;
		if(gameLogWriter != null){
			gameLogWriter.close();
		}
		if(gameLogPath == null){
			gameLogWriter = null;
		} else {
			try {
				gameLogWriter = new PrintWriter(logFolder + gameLogPath);
			} catch (FileNotFoundException e) {
				gameLogWriter = null;
			}
		}
	}

	public String getBehaviourLogPath() {
		return behaviourLogPath;
	}

	public void setBehaviourLogPath(String behaviourLogPath) {
		this.behaviourLogPath = behaviourLogPath;
		if(behaviourLogWriter != null){
			behaviourLogWriter.close();
		}
		if(behaviourLogPath == null){
			behaviourLogWriter = null;
		} else {
			try {
				behaviourLogWriter = new PrintWriter(logFolder + behaviourLogPath);
			} catch (FileNotFoundException e) {
				behaviourLogWriter = null;
			}
		}
	}

	/**
	 * Close all files
	 */
	public void close(){
		setGameLogPath(null);
		setBehaviourLogPath(null);
	}

	/**
	 * Log something for game replay
	 * @param s the string to log
	 */
	public void game(String s){
		if(printStdOut){
			System.out.println(s);
		}
		if(gameLogWriter != null){
			gameLogWriter.println(s);
		}
	}

	/**
	 * Log something related to the technical side of simulating the game
	 * @param s the string to log
	 */
	public void tech(String s){
		if(printStdErr){
			System.err.println("[TECH] " + s);
		}
	}

	/**
	 * Log the output on stderr of an external AI
	 * @param id the id of the AI
	 * @param s the string to log
	 */
	public void progStdErr(String id, String s){
		if(printStdErr){
			System.err.println("[STDERR] [" + id + "] " + s);
		}
	}

	/**
	 * Log behaviour of an external AI
	 * @param id the id of the AI
	 * @param s the string to log
	 */
	public void progBehaviour(String id, String s){
		if(printStdErr){
			System.err.println("[BEHAVIOUR] [" + id + "] " + s);
		}
	}
}
