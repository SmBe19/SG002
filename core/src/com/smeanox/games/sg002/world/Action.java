package com.smeanox.games.sg002.world;

import com.smeanox.games.sg002.util.Consts;

/**
 * Represents an action a player can take
 *
 * @author Benjamin Schmid
 */
public class Action {
	public enum ActionType {
		MOVE, FIGHT, PRODUCE, NONE
	}

	public ActionType actionType;
	public int startX, startY, endX, endY;
	public GameObjectType produceGameObjectType;

	public Action() {
	}

	public Action(ActionType actionType, int startX, int startY, int endX, int endY) {
		this.actionType = actionType;
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
	}

	public Action(ActionType actionType, int startX, int startY, int endX, int endY, GameObjectType produceGameObjectType) {
		this.actionType = actionType;
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.produceGameObjectType = produceGameObjectType;
	}

	@Override
	public String toString() {
		String startEnd = startX + " " + startY + " " + endX + " " + endY;
		switch (actionType) {
			case MOVE:
				return Consts.MOVE_ID + " " + startEnd;
			case FIGHT:
				return Consts.FIGHT_ID + " " + startEnd;
			case PRODUCE:
				return Consts.PRODUCE_ID + " " + startEnd + " " + produceGameObjectType.getExternalId();
		}
		return "None Action";
	}
}
