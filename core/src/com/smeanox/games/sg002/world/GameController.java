package com.smeanox.games.sg002.world;

import com.smeanox.games.sg002.player.Player;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.world.actionHandler.NextPlayerHandler;

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

	public GameController(Scenario scenario){
		this.scenario = scenario;
		players = new LinkedList<Player>();

		Consts.walkDiagonal = scenario.isWalkDiagonal();
		Consts.startGameObjectMinDistance = scenario.getStartGameObjectMinDistance();

		gameWorld = new GameWorld(scenario);
	}

	/**
	 * Adds a new Player
	 * @param player the player to add
	 */
	public void addPlayer(Player player){
		players.add(player);
		player.setMoney(scenario.getStartMoney());
		player.setGameController(this);
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
			gameWorld.startRound(activePlayer);
			fireOnNextPlayer(activePlayer);
			activePlayer.startPlaying();
		} else {
			finishedRound();
		}
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
