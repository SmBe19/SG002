package com.smeanox.games.sg002.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Helper methods to read config files
 *
 * @author Benjamin Schmid
 */
public class ConfigFileUtil {

	public static LinkedList<String> readAllLines(File file) throws FileNotFoundException {
		LinkedList<String> lines = new LinkedList<String>();

		Scanner sc = new Scanner(file);
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			if (!(line.trim().isEmpty() || line.trim().startsWith("#"))) {
				lines.add(line);
			}
		}

		sc.close();

		return lines;
	}
}
