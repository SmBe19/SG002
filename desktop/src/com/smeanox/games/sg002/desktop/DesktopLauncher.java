package com.smeanox.games.sg002.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.smeanox.games.sg002.SG002;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.util.TournamentContext;

public class DesktopLauncher {
	public static void main(String[] arg) {
		if (!readArgs(arg)) {
			return;
		}

		Consts.textureFilter = Texture.TextureFilter.MipMapLinearLinear;

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Consts.devWidth * 2;
		config.height = Consts.devHeight * 2;
		// uncomment this line for production (commented so window can be resized to test different screenSizes faster)
		//config.samples = 32;

		new LwjglApplication(new SG002(), config);
	}

	/**
	 * Read program arguments
	 *
	 * @param arg arguments
	 */
	private static boolean readArgs(String[] arg) {
		for (int i = 0; i < arg.length; i++) {
			try {
				if ("/?".equals(arg[i]) || "-h".equals(arg[i]) || "--help".equals(arg[i])) {
					printHelp();
				} else if ("-logfolder".equals(arg[i])) {
					TournamentContext.logFolder = arg[i + 1];
					i += 1;
				} else if ("-gamelog".equals(arg[i])) {
					TournamentContext.gameLog = arg[i + 1];
					i += 1;
				} else if ("-behaviourlog".equals(arg[i])) {
					TournamentContext.behaviourLog = arg[i + 1];
					i += 1;
				} else if ("-mutestdout".equals(arg[i])) {
					TournamentContext.printStdOut = false;
				} else if ("-mutestderr".equals(arg[i])) {
					TournamentContext.printStdErr = false;
				} else {
					System.out.println("Unknown argument: " + arg[i]);
					printHelp();
					return false;
				}
			} catch (IndexOutOfBoundsException e) {
				System.out.println("Missing argument after " + arg[i]);
				printHelp();
				return false;
			}
		}
		return true;
	}

	/**
	 * Print the usage of the program to stdout
	 */
	private static void printHelp() {
		System.out.println("Usage: SG002 " +
				"[-logfolder folder] " +
				"[-gamelog file] " +
				"[-behaviourlog file] " +
				"[-mutestdout] " +
				"[-mutestderr]");
	}
}
