package com.smeanox.games.sg002.nogui;

import com.smeanox.games.sg002.log.GameLogger;
import com.smeanox.games.sg002.log.TournamentLogger;
import com.smeanox.games.sg002.player.AIPlayer_BenNo1;
import com.smeanox.games.sg002.player.ExternalAIPlayer;
import com.smeanox.games.sg002.player.Player;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.util.ProgramArguments;
import com.smeanox.games.sg002.world.GameController;
import com.smeanox.games.sg002.world.Scenario;

import java.util.ArrayList;
import java.util.List;

/**
 * An evaluation consists of only one scenario
 *
 * @author Benjamin Schmid
 */
public class Evaluation {

	private TournamentLogger logger;
	private Scenario scenario;
	private List<String> playerNames;
	private List<String> playerCommands;

	private boolean played;

	public Evaluation(Scenario scenario, List<String> playerNames, List<String> playerCommands, TournamentLogger logger) {
		this.scenario = scenario;
		this.playerNames = playerNames;
		this.playerCommands = playerCommands;
		this.logger = logger;
		if(this.logger == null){
			this.logger = TournamentLogger.createDummyLogger();
		}
		played = false;
	}

	/**
	 * Play the evaluation
	 *
	 * @return list of points each player earned
	 */
	public List<Integer> play() throws InvalidConfigurationException {
		if(played){
			throw new IllegalStateException("can only be played once");
		}
		played = true;

		StringBuilder playerLine = new StringBuilder();
		playerLine.append(playerNames.size());
		playerLine.append(" players: ");
		for(String player : playerNames){
			playerLine.append(player);
			playerLine.append(", ");
		}
		if(!playerNames.isEmpty()) {
			playerLine.setLength(playerLine.length() - 2);
		}

		logger.event(playerLine.toString());

		List<Integer> playerPoints = new ArrayList<Integer>();
		for (int i = 0; i < playerNames.size(); i++) {
			playerPoints.add(0);
		}

		List<Integer> aPlayers = new ArrayList<Integer>();
		for (int i = 0; i < ProgramArguments.playerCount; i++) {
			aPlayers.add(i);
		}

		if (ProgramArguments.evaluation) {
			do {
				List<Integer> aGamePlayers = new ArrayList<Integer>(aPlayers);

				do {
					GameController gameController = generateGameController(aGamePlayers);
					playOneGame(gameController);
					addPoints(gameController, playerPoints, aGamePlayers);
				} while (nextPermutation(aGamePlayers));

			} while (nextPlayers(aPlayers));
		} else {
			GameController gameController = generateGameController(aPlayers);
			playOneGame(gameController);
			addPoints(gameController, playerPoints, aPlayers);
		}

		logger.event("Total points evaluation (" + scenario.getId() + ") :");
		for (int i = 0; i < playerNames.size(); i++) {
			logger.event(playerNames.get(i) + ": " + playerPoints.get(i));
		}

		logger.close();

		return playerPoints;
	}

	/**
	 * Add points according to the state of the GameController
	 * @param gameController the gameController
	 * @param playerPoints list of points for each player
	 * @param aGamePlayers list of players that participated (used to map from playerId to array index)
	 */
	private void addPoints(GameController gameController, List<Integer> playerPoints, List<Integer> aGamePlayers){
		for(Player player : gameController.getPlayers()){
			if(gameController.getGameWorld().isPlayerStillAlive(player)){
				playerPoints.set(aGamePlayers.get(player.getId()), aGamePlayers.get(player.getId()) + 1);
			}
		}
	}

	/**
	 * Generate a GameController for the given players
	 *
	 * @param players the players
	 * @return the GameController
	 */
	private GameController generateGameController(List<Integer> players) throws InvalidConfigurationException {
		GameLogger logger = new GameLogger();

		StringBuilder logPrefix = new StringBuilder();
		logPrefix.append(scenario.getId());
		logPrefix.append("_");
		for (Integer i : players) {
			logPrefix.append(i);
			logPrefix.append("_");
		}

		logger.setLogFolder(ProgramArguments.logFolder);
		if(ProgramArguments.gameLog != null) {
			logger.setGameLogPath(logPrefix + ProgramArguments.gameLog);
		}
		if(ProgramArguments.behaviourLog != null) {
			logger.setBehaviourLogPath(logPrefix + ProgramArguments.behaviourLog);
		}
		logger.setPrintStdOut(ProgramArguments.printStdOut);
		logger.setPrintStdErr(ProgramArguments.printStdErr);

		GameController gameController = new GameController(scenario, logger);

		for (int i = 0; i < players.size(); i++) {
			Player player;
			if (Consts.COMMAND_LOCAL.equals(playerCommands.get(players.get(i)))) {
				throw new InvalidConfigurationException("Local player not allowed in nogui mode");
			} else if (Consts.COMMAND_BENNO1.equals(playerCommands.get(players.get(i)))) {
				player = new AIPlayer_BenNo1();
			} else {
				player = new ExternalAIPlayer();
				((ExternalAIPlayer) player).setCommand(playerCommands.get(players.get(i)));
			}
			gameController.addPlayer(player);
			player.setColor(Consts.playerColors[i % Consts.playerColors.length]);
			player.setShowGUI(false);
			player.setName(playerNames.get(players.get(i)));
		}

		return gameController;
	}

	/**
	 * Moves the given list of players to the next possible state. The list has to be ascending and each item has to be < playerCount
	 *
	 * @param players the list to manipulate
	 * @return true if a state was found
	 */
	private boolean nextPlayers(List<Integer> players) {
		for (int i = 0; i < players.size(); i++) {
			players.set(i, players.get(i) + 1);
			if (i == players.size() - 1) {
				if (players.get(i) >= playerNames.size()) {
					return false;
				} else {
					return true;
				}
			} else {
				if (players.get(i) >= players.get(i + 1)) {
					if (i == 0) {
						players.set(i, 0);
					} else {
						players.set(i, players.get(i - 1) + 1);
					}
				} else {
					return true;
				}
			}
		}
		throw new IllegalStateException("should not happen");
	}

	/**
	 * Generate the next permutation of the given list of players
	 *
	 * @param players the list
	 * @return true if there is a next permutation
	 */
	private boolean nextPermutation(List<Integer> players) {
		int k, l;
		for (k = players.size() - 2; k >= 0 && players.get(k) >= players.get(k + 1); k--) ;
		if (k < 0) {
			return false;
		}
		for (l = players.size() - 1; l > k && players.get(k) >= players.get(l); l--) ;

		swap(players, l, k);
		for (int a = k + 1, b = players.size() - 1; a < b; a++, b--) {
			swap(players, a, b);
		}
		return true;
	}

	/**
	 * Swap two values
	 *
	 * @param list the list in which the swap happens
	 * @param a    index of first element
	 * @param b    index of second element
	 * @param <T>  type of the elements
	 */
	private <T> void swap(List<T> list, int a, int b) {
		T tmp = list.get(a);
		list.set(a, list.get(b));
		list.set(b, tmp);
	}

	/**
	 * Simulates one game
	 *
	 * @param gameController the gameController to use to simulate
	 */
	private void playOneGame(GameController gameController) {
		gameController.startGame();
		while (!gameController.isGameEnded()) {
			gameController.update(Consts.headlessFrameDuration);
		}
	}

}
