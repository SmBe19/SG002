package com.smeanox.games.sg002.world;

import com.smeanox.games.sg002.player.Player;

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

	public GameController(Scenario scenario){
		this.scenario = scenario;
		players = new LinkedList<Player>();
		gameWorld = new GameWorld(scenario);
	}

	/**
	 * Adds a new Player
	 * @param player the player to add
	 */
	public void addPlayer(Player player){
		players.add(player);
		player.setMoney(scenario.getStartMoney());
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
		gameWorld.startRound(activePlayer);
		activePlayer.startPlaying();
	}

	/**
	 * Updates the active Player
	 * @param delta The time in seconds since the last render.
	 */
	public void update(float delta) {
		activePlayer.update(delta);
	}

	public Player getActivePlayer(){
		return activePlayer;
	}

	public GameWorld getGameWorld() {
		return gameWorld;
	}
}
