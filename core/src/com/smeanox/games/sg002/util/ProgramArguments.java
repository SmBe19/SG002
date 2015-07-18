package com.smeanox.games.sg002.util;

import java.io.File;

/**
 * Contains Constants specified by program arguments
 * <br>
 * Mostly set with program arguments
 *
 * @author Benjamin Schmid
 */
public class ProgramArguments {

	public static boolean noGUI = false;

	public static boolean fullScreen = false;

	public static String logFolder = "";

	public static String gameLog = null;

	public static String behaviourLog = null;

	public static boolean printStdOut = false;

	public static boolean printStdErr = false;

	public static String tournamentLog = null;

	public static String evaluationLog = null;

	public static boolean tournamentStdOut = true;

	public static boolean tournamentStdErr = true;

	public static boolean evaluationStdOut = true;

	public static boolean evaluationStdErr = true;

	public static String playersFile = null;

	public static String namesFile = null;

	public static boolean allowPlayersOverride = false;

	public static boolean allowNamesOverride = false;

	public static String scenario = null;

	public static int playerCount = -1;

	public static boolean evaluation = true;

	public static String tournamentFile = null;

	public static boolean autoStart = false;

	public static String replay = null;

	public static boolean printFPS = false;

	/**
	 * Read program arguments
	 *
	 * @param args arguments
	 */
	public static boolean readArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			try {
				if ("/?".equals(args[i]) || "-h".equals(args[i]) || "--help".equals(args[i])) {
					printHelp();
				} else if ("-nogui".equals(args[i])) {
					noGUI = "1".equals(args[i+1]);
					i += 1;
				} else if ("-fullscreen".equals(args[i])) {
					fullScreen = "1".equals(args[i+1]);
					i += 1;
				} else if ("-logfolder".equals(args[i])) {
					logFolder = args[i + 1];
					i += 1;
				} else if ("-gamelog".equals(args[i])) {
					gameLog = args[i + 1];
					i += 1;
				} else if ("-behaviourlog".equals(args[i])) {
					behaviourLog = args[i + 1];
					i += 1;
				} else if ("-stdout".equals(args[i])) {
					printStdOut = "1".equals(args[i+1]);
					i += 1;
				} else if ("-stderr".equals(args[i])) {
					printStdErr = "1".equals(args[i+1]);
					i += 1;
				} else if ("-tournamentlog".equals(args[i])) {
					tournamentLog = args[i + 1];
					i += 1;
				} else if ("-evaluationlog".equals(args[i])) {
					evaluationLog = args[i + 1];
					i += 1;
				} else if ("-tournamentstdout".equals(args[i])) {
					tournamentStdOut = "1".equals(args[i+1]);
					i += 1;
				} else if ("-tournamentstderr".equals(args[i])) {
					tournamentStdErr = "1".equals(args[i+1]);
					i += 1;
				} else if ("-evaluationstdout".equals(args[i])) {
					evaluationStdOut = "1".equals(args[i+1]);
					i += 1;
				} else if ("-evaluationstderr".equals(args[i])) {
					evaluationStdErr = "1".equals(args[i+1]);
					i += 1;
				} else if ("-players".equals(args[i])) {
					playersFile = args[i + 1];
					i += 1;
				} else if ("-names".equals(args[i])) {
					namesFile = args[i + 1];
					i += 1;
				} else if ("-playersoverride".equals(args[i])) {
					allowPlayersOverride = "1".equals(args[i+1]);
					i += 1;
				} else if ("-namesoverride".equals(args[i])) {
					allowNamesOverride = "1".equals(args[i+1]);
					i += 1;
				} else if ("-scenario".equals(args[i])) {
					scenario = args[i + 1];
					i += 1;
				} else if ("-playercount".equals(args[i])) {
					playerCount = Integer.parseInt(args[i + 1]);
					i += 1;
				} else if ("-autostart".equals(args[i])) {
					autoStart = "1".equals(args[i+1]);
					i += 1;
				} else if ("-replay".equals(args[i])) {
					replay = args[i + 1];
					i += 1;
				} else if ("-evaluation".equals(args[i])) {
					evaluation = "1".equals(args[i+1]);
					i += 1;
				} else if ("-tournament".equals(args[i])) {
					tournamentFile = args[i + 1];
					i += 1;
				} else if ("-printfps".equals(args[i])) {
					printFPS = "1".equals(args[i+1]);
					i += 1;
				} else {
					System.out.println("Unknown argument: " + args[i]);
					printHelp();
					return false;
				}
			} catch (IndexOutOfBoundsException e) {
				System.out.println("Missing argument after " + args[i]);
				printHelp();
				return false;
			} catch(NumberFormatException e){
				System.out.println("Argument is not a number after " + args[i]);
				printHelp();
				return false;
			}
		}
		return checkArguments();
	}

	/**
	 * Print the usage of the program to stdout
	 */
	public static void printHelp() {
		System.out.println("Usage: SG002 " +
				"[-nogui 0/1] " +
				"[-fullscreen 0/1] " +
				"[-logfolder folder] " +
				"[-gamelog file] " +
				"[-behaviourlog file] " +
				"[-stdout 0/1] " +
				"[-stderr 0/1] " +
				"[-tournamentlog file] " +
				"[-evaluationlog file] " +
				"[-tournamentstdout 0/1] " +
				"[-tournamentstderr 0/1] " +
				"[-evaluationstdout 0/1] " +
				"[-evaluationstderr 0/1] " +
				"[-players file] " +
				"[-names file] " +
				"[-playersoverride 0/1] " +
				"[-namesoverride 0/1] " +
				"[-scenario id] " +
				"[-playercount count] " +
				"[-autostart 0/1] " +
				"[-replay file] " +
				"[-evaluation 0/1] " +
				"[-tournament file] " +
				"[-printfps 0/1");
	}

	/**
	 * Check whether all arguments are valid
	 *
	 * @return true if they are valid
	 */
	private static boolean checkArguments() {
		if(playersFile != null){
			if (!checkFile("players", playersFile)){
				return false;
			}
		}
		if(namesFile != null){
			if(!checkFile("names", namesFile)){
				return false;
			}
		}
		if(replay != null){
			if(!checkFile("replay", replay)){
				return false;
			}
		}
		if(autoStart){
			if(playerCount < 2 || scenario == null || playersFile == null || namesFile == null) {
				System.out.println("not enough info for autostart");
				return false;
			}
		}

		if(noGUI){
			if(playerCount < 2 || playersFile == null || namesFile == null){
				System.out.println("not enough info for nogui");
				return false;
			}
			if(tournamentFile != null && scenario != null){
				System.out.println("only tournament or scenario may be specified");
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks whether the given file exists
	 * @param name the name to use for the error message
	 * @param fileName the name of the file to check
	 * @return true if it exists
	 */
	private static boolean checkFile(String name, String fileName){
		File file = new File(fileName);
		if(!file.exists()){
			System.out.println(name + " file doesn't exist: " + fileName);
			return false;
		}
		return true;
	}
}
