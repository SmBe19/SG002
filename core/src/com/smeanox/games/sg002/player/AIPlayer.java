package com.smeanox.games.sg002.player;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.smeanox.games.sg002.data.Point;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.world.GameObject;
import com.smeanox.games.sg002.world.GameObjectType;

import java.util.LinkedList;

/**
 * Player played by the computer
 *
 * @author Benjamin Schmid
 */
public abstract class AIPlayer extends Player {
	protected boolean finishedPlaying;
	protected float aiSleepLeft;

	protected long seed0, seed1;

	@Override
	public final void update(float delta) {
		updateAI(delta);
		aiSleepLeft -= delta;
		if (finishedPlaying && aiSleepLeft <= 0) {
			finishPlaying();
		}
	}

	/**
	 * finish the round
	 */
	private void finishPlaying(){
		finishedPlaying = false;

		if(MathUtils.random instanceof RandomXS128) {
			seed0 = ((RandomXS128) MathUtils.random).getState(0);
			seed1 = ((RandomXS128) MathUtils.random).getState(1);
		}

		endPlaying();
	}

	@Override
	protected final void play() throws ProtocolViolationException {
		if(MathUtils.random instanceof RandomXS128) {
			if(seed0 == 0 && seed1 == 0){
				seed0 = System.currentTimeMillis();
				seed1 = MathUtils.random(Long.MIN_VALUE, Long.MAX_VALUE);
			}
			((RandomXS128) MathUtils.random).setState(seed0, seed1);
		}

		playAI();
	}

	/**
	 * Perform the moves for this round
	 */
	protected abstract void playAI() throws ProtocolViolationException;

	/**
	 * Update the AI
	 *
	 * @param delta time passed since last update
	 */
	protected void updateAI(float delta){}

	/**
	 * In the next frame the gamecontroller will be informed that the AI finished
	 */
	protected void setFinishedPlaying() {
		finishedPlaying = true;
		aiSleepLeft = Consts.aiSleep;
	}
}
