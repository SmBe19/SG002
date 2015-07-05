package com.smeanox.games.sg002.player;

import com.smeanox.games.sg002.world.Action;

import java.util.LinkedList;

/**
 * A player that replays a already played game using a log
 *
 * @author Benjamin Schmid
 */
public class ReplayPlayer extends AIPlayer {

	private LinkedList<LinkedList<Action>> actions;
	private int activeRound = 0;

	@Override
	protected void playAI() throws ProtocolViolationException {
		if(actions == null || activeRound >= actions.size()){
			return;
		}
		for(Action action : actions.get(activeRound)){
			if(!gameWorld.doAction(action)){
				gameController.getLogger().progBehaviour(id + "/" + name, "invalid replay: " + action);
			}
		}

		activeRound++;

		setFinishedPlaying();
	}

	public LinkedList<LinkedList<Action>> getActions() {
		return actions;
	}

	public void setActions(LinkedList<LinkedList<Action>> actions) {
		this.actions = actions;
	}

	public int getActiveRound() {
		return activeRound;
	}

	public void setActiveRound(int activeRound) {
		if(activeRound < 0 || actions != null && activeRound >= actions.size()){
			return;
		}
		this.activeRound = activeRound;
	}
}
