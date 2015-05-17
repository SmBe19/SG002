package com.smeanox.games.sg002.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.smeanox.games.sg002.player.Player;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.world.actionHandler.NextPlayerHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Controlls the game flow
 * @author Benjamin Schmid
 */
public class GameController {
	private Scenario scenario;
	private GameWorld gameWorld;
	private LinkedList<Player> players;
	private Iterator<Player> playerIterator;
	private Player activePlayer;

	private LinkedList<NextPlayerHandler> nextPlayerHandlers;

	public GameController(String fileName){
		players = new LinkedList<Player>();
		loadGame(fileName);
	}

	public GameController(Scenario scenario){
		this.scenario = scenario;
		players = new LinkedList<Player>();

		initScenario(scenario);

		gameWorld = new GameWorld(scenario);
	}

	/**
	 * Initializes the scenario
	 * @param scenario the scenario
	 */
	private void initScenario(Scenario scenario){
		Consts.walkDiagonal = scenario.isWalkDiagonal();
		Consts.startGameObjectMinDistance = scenario.getStartGameObjectMinDistance();

		if(gameWorld == null) {
			gameWorld = new GameWorld(scenario);
		}
	}

	/**
	 * Adds a new Player
	 * @param player the player to add
	 */
	public void addPlayer(Player player){
		players.add(player);
		player.setMoney(scenario.getStartMoney());
		player.setGameController(this);
		player.setId(players.size() - 1);
		gameWorld.addStartGameObjects(player, GameObjectType.getStartGameObjectType());
	}

	/**
	 * Starts the game
	 */
	public void startGame(){
		playerIterator = players.iterator();
		finishedRound();
	}

	/**
	 * Called by the active player when he finished his round
	 */
	public void finishedRound(){
		if(!playerIterator.hasNext()){
			playerIterator = players.iterator();
		}
		activePlayer = playerIterator.next();
		if(gameWorld.isPlayerStillAlive(activePlayer)) {
			startRound(activePlayer);
		} else {
			finishedRound();
		}
	}

	private void forwardToPlayer(Player player){
		playerIterator = players.iterator();
		while (playerIterator.hasNext()){
			activePlayer = playerIterator.next();
			if(activePlayer == player){
				break;
			}
		}
		startRound(activePlayer);
	}

	/**
	 * Starts a new round
	 * @param player the active player
	 */
	private void startRound(Player player){
		gameWorld.startRound(player);
		fireOnNextPlayer(player);
		player.startPlaying();
	}

	/**
	 * Updates the active Player
	 * @param delta The time in seconds since the last render.
	 */
	public void update(float delta) {
		if(activePlayer != null) {
			activePlayer.update(delta);
		}
	}

	public Player getActivePlayer(){
		return activePlayer;
	}

	public GameWorld getGameWorld() {
		return gameWorld;
	}

	/**
	 * saves the game state to the given file
	 * @param fileName the file to save to
	 */
	public void saveGame(String fileName){
		try {
			XmlWriter writer = new XmlWriter(new FileWriter(Gdx.files.local(fileName).file()));
			writer.element("Game");
			writer.attribute("scenario", scenario.getId());
			writer.element("Players");
			for(Player player : players){
				writer.element("Player");
				if(player == activePlayer){
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
	 * loads the game state from the given file
	 * @param fileName the file to load from
	 */
	public void loadGame(String fileName){
		try {
			XmlReader reader = new XmlReader();
			XmlReader.Element root = reader.parse(Gdx.files.local(fileName));
			Scenario scenarioLoad = Scenario.getScanarioById(root.getAttribute("scenario"));
			initScenario(scenarioLoad);
			gameWorld.initScenario(scenarioLoad);
			XmlReader.Element playersXML = root.getChildByName("Players");
			players.clear();
			for(XmlReader.Element aPlayer : playersXML.getChildrenByName("Player")){
				Player player = Player.loadStatic(aPlayer);
				addPlayer(player);
				player.load(aPlayer);

				if(aPlayer.getBooleanAttribute("active", false)){
					activePlayer = player;
				}
			}
			XmlReader.Element gameWorldXML = root.getChildByName("GameWorld");
			gameWorld.load(gameWorldXML);

			forwardToPlayer(activePlayer);

			System.out.println("finished loading");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void fireOnNextPlayer(Player nextPlayer){
		if(nextPlayerHandlers != null) {
			for (NextPlayerHandler c : nextPlayerHandlers) {
				c.onNextPlayer(nextPlayer);
			}
		}
	}

	/**
	 * adds a {@link NextPlayerHandler}
	 *
	 * @param handler the NextPlayerHandler
	 */
	public void addNextPlayerHandler(NextPlayerHandler handler) {
		if(nextPlayerHandlers == null){
			nextPlayerHandlers = new LinkedList<NextPlayerHandler>();
		}
		nextPlayerHandlers.add(handler);
	}

	/**
	 * removes a {@link NextPlayerHandler}
	 *
	 * @param handler the NextPlayerHandler
	 */
	public void removeNextPlayerHandler(NextPlayerHandler handler) {
		if(nextPlayerHandlers == null){
			return;
		}
		nextPlayerHandlers.remove(handler);
		if(nextPlayerHandlers.isEmpty()){
			nextPlayerHandlers = null;
		}
	}
}
