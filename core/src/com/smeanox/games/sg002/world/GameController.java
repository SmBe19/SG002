package com.smeanox.games.sg002.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.smeanox.games.sg002.data.Point;
import com.smeanox.games.sg002.log.GameLogger;
import com.smeanox.games.sg002.player.ExternalAIPlayer;
import com.smeanox.games.sg002.player.Player;
import com.smeanox.games.sg002.player.ProtocolViolationException;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.world.actionHandler.GameEndHandler;
import com.smeanox.games.sg002.world.actionHandler.NextPlayerHandler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Control the game flow
 *
 * @author Benjamin Schmid
 */
public class GameController {
	private GameLogger logger;
	private Scenario scenario;
	private GameWorld gameWorld;
	private LinkedList<Player> players;
	private Iterator<Player> playerIterator;
	private Player activePlayer;
	private boolean gameEnded;

	private LinkedList<NextPlayerHandler> nextPlayerHandlers;
	private LinkedList<GameEndHandler> gameEndHandlers;

	/**
	 * Create a new instance and load the given save file
	 *
	 * @param fileName the file to load
	 */
	public GameController(String fileName) {
		players = new LinkedList<Player>();
		loadGame(fileName);
		gameEnded = false;
	}

	/**
	 * Create a new instance
	 *
	 * @param scenario the scenario to use
	 */
	public GameController(Scenario scenario) {
		this(scenario, GameLogger.createDummyLogger());
	}

	/**
	 * Create a new instance
	 *
	 * @param scenario the scenario to use
	 * @param logger   the logger to use to log the game
	 */
	public GameController(Scenario scenario, GameLogger logger) {
		this.scenario = scenario;
		this.logger = logger;
		if (this.logger == null) {
			this.logger = GameLogger.createDummyLogger();
		}
		players = new LinkedList<Player>();

		initScenario(scenario);

		gameWorld = new GameWorld(scenario);

		gameEnded = false;
	}

	/**
	 * Initialize the scenario
	 *
	 * @param scenario the scenario
	 */
	private void initScenario(Scenario scenario) {
		Consts.seed = scenario.getSeed();
		Consts.walkDiagonal = scenario.isWalkDiagonal();
		Consts.multipleActionsPerObject = scenario.isMultipleActionsPerObject();
		Consts.startGameObjectMinDistance = scenario.getStartGameObjectMinDistance();
	}

	/**
	 * Finish the game and clean up
	 */
	public void endGame(){
		if(gameEnded){
			return;
		}
		gameEnded = true;
		for(Player player : players){
			if(player instanceof ExternalAIPlayer){
				try {
					((ExternalAIPlayer) player).terminate();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		logger.close();
		fireOnGameEnd();
	}

	/**
	 * Add a new Player
	 *
	 * @param player the player to add
	 */
	public void addPlayer(Player player) {
		players.add(player);
		player.setMoney(scenario.getStartMoney());
		player.setGameController(this);
		player.setId(players.size() - 1);
		gameWorld.addStartGameObjects(player, GameObjectType.getStartGameObjectType());
	}

	/**
	 * Start the game
	 */
	public void startGame() {
		logger.game(players.size() + " " + scenario.getStartMoney() + " " +
				scenario.getMapSizeX() + " " + scenario.getMapSizeY() + " " +
				scenario.getMaxGold());
		StringBuilder playerNames = new StringBuilder();
		for(Player player : players){
			if(player != players.getFirst()){
				playerNames.append(" ");
			}
			playerNames.append(player.getName());
		}
		logger.game(playerNames.toString());
		for(Point point : scenario.getGoldPos()){
			logger.game(point.x + " " + point.y);
		}
		for (int i = 0; i < players.size(); i++) {
			logger.game(scenario.getStartPos(i).x + " " + scenario.getStartPos(i).y);
		}

		playerIterator = players.iterator();
		finishedRound();
	}

	/**
	 * Called by the active player when he finished his round
	 */
	public void finishedRound() {
		if(gameEnded){
			return;
		}

		writeActionsLog();

		if (countLivingPlayers() < 2) {
			endGame();
			return;
		}
		logger.game(Consts.NEXT_ROUND_ID);
		Player oldActivePlayer = activePlayer;
		nextPlayer();
		if (oldActivePlayer == activePlayer) {
			throw new IllegalStateException("Something's broken");
		}
		logger.game("" + activePlayer.getId());
		startRound(activePlayer);
	}

	/**
	 * write the performed actions to the log file
	 */
	private void writeActionsLog() {
		if(gameWorld.getPlayerActions().get(activePlayer) != null) {
			for (Action action : gameWorld.getPlayerActions().get(activePlayer)) {
				String startEnd = action.startX + " " + action.startY + " " + action.endX + " " + action.endY;
				switch (action.actionType) {
					case MOVE:
						logger.game(Consts.MOVE_ID + " " + startEnd);
						break;
					case FIGHT:
						logger.game(Consts.FIGHT_ID + " " + startEnd);
						break;
					case PRODUCE:
						logger.game(Consts.PRODUCE_ID + " " + startEnd + " " + action.produceGameObjectType.getExternalId());
						break;
					case NONE:
						break;
				}
			}
		}
	}

	/**
	 * Sets the activePlayer to the next player that is still alive
	 */
	private void nextPlayer() {
		if (!playerIterator.hasNext()) {
			playerIterator = players.iterator();
		}
		activePlayer = playerIterator.next();
		if (!gameWorld.isPlayerStillAlive(activePlayer)) {
			if(gameWorld.getPlayerActions().containsKey(activePlayer)) {
				gameWorld.getPlayerActions().get(activePlayer).clear();
			}
			nextPlayer();
		}
	}

	/**
	 * Increas the playerIterator until the given player is the active player
	 *
	 * @param player the player that should be the active player afterwards
	 */
	private void forwardToPlayer(Player player) {
		playerIterator = players.iterator();
		while (playerIterator.hasNext()) {
			activePlayer = playerIterator.next();
			if (activePlayer == player) {
				break;
			}
		}
		startRound(activePlayer, false);
	}

	/**
	 * Count the number of players that are still alive
	 *
	 * @return the number of players
	 */
	public int countLivingPlayers() {
		int count = 0;
		for (Player player : players) {
			if (gameWorld.isPlayerStillAlive(player)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Start a new round
	 *
	 * @param player the active player
	 */
	private void startRound(Player player) {
		startRound(player, true);
	}

	/**
	 * Start a new round
	 *
	 * @param player              the active player
	 * @param reenableUsedActions whether usedActions should be cleared
	 */
	private void startRound(Player player, boolean reenableUsedActions) {
		gameWorld.startRound(player, reenableUsedActions);
		fireOnNextPlayer(player);
		try {
			player.startPlaying();
		} catch (ProtocolViolationException e) {
			writeActionsLog();
			endGame();
		}
	}

	/**
	 * Update the active Player
	 *
	 * @param delta The time in seconds since the last render.
	 */
	public void update(float delta) {
		if (activePlayer != null) {
			activePlayer.update(delta);
		}
	}

	public LinkedList<Player> getPlayers() {
		return players;
	}

	public Player getActivePlayer() {
		return activePlayer;
	}

	public GameWorld getGameWorld() {
		return gameWorld;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public GameLogger getLogger() {
		return logger;
	}

	public boolean isGameEnded() {
		return gameEnded;
	}

	/**
	 * save the game state to the given file
	 *
	 * @param fileName the file to save to
	 */
	public void saveGame(String fileName) {
		try {
			File file = new File(fileName);
			XmlWriter writer = new XmlWriter(new FileWriter(file));
			writer.element("Game");
			writer.attribute("scenario", scenario.getId());
			writer.element("Players");
			for (Player player : players) {
				writer.element("Player");
				if (player == activePlayer) {
					writer.attribute("active", true);
				}
				player.save(writer);
				writer.pop();
			}
			writer.pop();
			writer.element("GameWorld");
			gameWorld.save(writer);
			writer.pop();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * load the game state from the given file
	 *
	 * @param fileName the file to load from
	 */
	public void loadGame(String fileName) {
		try {
			XmlReader reader = new XmlReader();
			File file = new File(fileName);
			if (!file.exists()) {
				return;
			}
			XmlReader.Element root = reader.parse(new FileReader(file));
			Scenario scenarioLoad = Scenario.getScanarioById(root.getAttribute("scenario"));
			initScenario(scenarioLoad);
			gameWorld.initScenario(scenarioLoad);
			XmlReader.Element playersXML = root.getChildByName("Players");
			players.clear();
			for (XmlReader.Element aPlayer : playersXML.getChildrenByName("Player")) {
				Player player = Player.loadStatic(aPlayer);
				addPlayer(player);
				player.load(aPlayer);

				if (aPlayer.getBooleanAttribute("active", false)) {
					activePlayer = player;
				}
			}
			// make sure the players are associated to the correct ids
			Player.resetPlayerIds();
			for (Player player : players) {
				player.setId(player.getId());
			}

			XmlReader.Element gameWorldXML = root.getChildByName("GameWorld");
			gameWorld.load(gameWorldXML);

			forwardToPlayer(activePlayer);

			// TODO remove me
			System.out.println("finished loading");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fire an event that the next player started its turn
	 *
	 * @param nextPlayer the player that started its round
	 */
	protected void fireOnNextPlayer(Player nextPlayer) {
		if (nextPlayerHandlers != null) {
			for (NextPlayerHandler c : nextPlayerHandlers) {
				c.onNextPlayer(nextPlayer);
			}
		}
	}

	/**
	 * add a {@link NextPlayerHandler}
	 *
	 * @param handler the NextPlayerHandler
	 */
	public void addNextPlayerHandler(NextPlayerHandler handler) {
		if (nextPlayerHandlers == null) {
			nextPlayerHandlers = new LinkedList<NextPlayerHandler>();
		}
		nextPlayerHandlers.add(handler);
	}

	/**
	 * remove a {@link NextPlayerHandler}
	 *
	 * @param handler the NextPlayerHandler
	 */
	public void removeNextPlayerHandler(NextPlayerHandler handler) {
		if (nextPlayerHandlers == null) {
			return;
		}
		nextPlayerHandlers.remove(handler);
		if (nextPlayerHandlers.isEmpty()) {
			nextPlayerHandlers = null;
		}
	}

	/**
	 * Fire an event that the game ended
	 */
	protected void fireOnGameEnd() {
		if (gameEndHandlers != null) {
			for (GameEndHandler c : gameEndHandlers) {
				c.onGameEnd();
			}
		}
	}

	/**
	 * add a {@link GameEndHandler}
	 *
	 * @param handler the GameEndHandler
	 */
	public void addGameEndHandler(GameEndHandler handler) {
		if (gameEndHandlers == null) {
			gameEndHandlers = new LinkedList<GameEndHandler>();
		}
		gameEndHandlers.add(handler);
	}

	/**
	 * remove a {@link GameEndHandler}
	 *
	 * @param handler the GameEndHandler
	 */
	public void removeGameEndHandler(GameEndHandler handler) {
		if (gameEndHandlers == null) {
			return;
		}
		gameEndHandlers.remove(handler);
		if (gameEndHandlers.isEmpty()) {
			gameEndHandlers = null;
		}
	}
}
