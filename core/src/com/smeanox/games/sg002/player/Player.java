package com.smeanox.games.sg002.player;

import com.smeanox.games.sg002.world.GameController;

/**
 * a player that can play the game
 * @author Benjamin Schmid
 */
public abstract class Player {
	protected GameController gameController;

	protected int money;
	protected boolean isPlaying;

	public Player(GameController gameController){
		this.gameController = gameController;
		money = 0;
	}

	public final void startPlaying(){
		isPlaying = true;
		play();
	}

	protected final void endPlaying(){
		isPlaying = false;
		gameController.finishedRound();
	}

	protected abstract void play();

	public abstract void update(float delta);

	public final int getMoney() {
		return money;
	}

	public final void setMoney(int money) {
		this.money = money;
	}

	public final void addMoney(int amount) {
		money += amount;
	}
}
