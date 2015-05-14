package com.smeanox.games.sg002.player;

import com.badlogic.gdx.graphics.Color;
import com.smeanox.games.sg002.world.Action;
import com.smeanox.games.sg002.world.GameController;

/**
 * a player that can play the game
 * @author Benjamin Schmid
 */
public abstract class Player {
	protected GameController gameController;

	protected int money;
	protected boolean isPlaying;
	protected Color color;
	protected boolean showGUI;

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

	public boolean proposeAction(Action action){
		return false;
	}

	public boolean proposeEndPlaying(){
		return false;
	}

	public void setGameController(GameController gameController) {
		this.gameController = gameController;
	}

	public final int getMoney() {
		return money;
	}

	public final void setMoney(int money) {
		this.money = money;
	}

	public final void addMoney(int amount) {
		money += amount;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isShowGUI() {
		return showGUI;
	}

	public void setShowGUI(boolean showGUI) {
		this.showGUI = showGUI;
	}
}
