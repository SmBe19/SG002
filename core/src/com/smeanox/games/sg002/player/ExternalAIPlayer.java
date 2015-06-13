package com.smeanox.games.sg002.player;

/**
 * Player played by an external program
 *
 * @author Benjamin Schmid
 */
public class ExternalAIPlayer extends Player {

	protected boolean finishedPlaying;

	@Override
	public final void update(float delta) {
		if (finishedPlaying) {
			finishedPlaying = false;
			endPlaying();
		}
	}

	/**
	 * In the next frame the gamecontroller will be informed that the AI finished
	 */
	protected void setFinishedPlaying() {
		finishedPlaying = true;
	}

	/**
	 * Perform the moves for this round
	 */
	@Override
	protected void play() {

	}
}
