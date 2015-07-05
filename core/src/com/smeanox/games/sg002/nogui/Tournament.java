package com.smeanox.games.sg002.nogui;

import com.smeanox.games.sg002.log.TournamentLogger;
import com.smeanox.games.sg002.util.ConfigFileUtil;
import com.smeanox.games.sg002.util.ProgramArguments;
import com.smeanox.games.sg002.world.Scenario;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A tournament consists of multiple games with different scenarios
 *
 * @author Benjamin Schmid
 */
public class Tournament {

	private TournamentLogger logger;
	private String tournamentFile;
	private LinkedList<String> playerNames;
	private LinkedList<String> playerCommands;

	private boolean played;

	public Tournament(String tournamentFile, LinkedList<String> playerNames, LinkedList<String> playerCommands, TournamentLogger logger) {
		this.tournamentFile = tournamentFile;
		this.playerNames = playerNames;
		this.playerCommands = playerCommands;
		this.logger = logger;
		if (this.logger == null) {
			this.logger = TournamentLogger.createDummyLogger();
		}

		played = false;
	}

	public void play() throws FileNotFoundException, InvalidConfigurationException {
		if (played) {
			throw new IllegalStateException("can only be played once");
		}
		played = true;

		StringBuilder playerLine = new StringBuilder();
		playerLine.append(playerNames.size());
		playerLine.append(" players: ");
		for (String player : playerNames) {
			playerLine.append(player);
			playerLine.append(", ");
		}
		if (!playerNames.isEmpty()) {
			playerLine.setLength(playerLine.length() - 2);
		}

		logger.event(playerLine.toString());

		List<Integer> playerPoints = new ArrayList<Integer>();
		for (int i = 0; i < playerNames.size(); i++) {
			playerPoints.add(0);
		}

		List<String> scenarios = ConfigFileUtil.readAllLines(new File(tournamentFile));
		for (String scenario : scenarios) {
			TournamentLogger evaluationLogger = new TournamentLogger();

			evaluationLogger.setLogFolder(ProgramArguments.logFolder);
			if (ProgramArguments.evaluationLog != null) {
				evaluationLogger.setEventLogPath(scenario + "_" + ProgramArguments.evaluationLog);
			}
			evaluationLogger.setPrintStdOut(ProgramArguments.evaluationStdOut);
			evaluationLogger.setPrintStdErr(ProgramArguments.evaluationStdErr);

			Evaluation game = new Evaluation(Scenario.getScanarioById(scenario), playerNames, playerCommands, evaluationLogger);
			List<Integer> result = game.play();
			addPoints(result, playerPoints);

			logger.event("Scenario " + scenario + ":");
			for (int i = 0; i < playerNames.size(); i++) {
				logger.event(playerNames.get(i) + ": " + result.get(i));
			}
		}

		logger.event("Total points tournament:");
		for (int i = 0; i < playerNames.size(); i++) {
			logger.event(playerNames.get(i) + ": " + playerPoints.get(i));
		}

		logger.close();
	}

	/**
	 * Add points according to the result of a game evaluation
	 *
	 * @param result       the list with the result
	 * @param playerPoints list of points for each player
	 */
	private void addPoints(List<Integer> result, List<Integer> playerPoints) {
		for (int i = 0; i < playerPoints.size(); i++) {
			playerPoints.set(i, playerPoints.get(i) + result.get(i));
		}
	}
}
