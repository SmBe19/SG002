package com.smeanox.games.sg002.world;

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
}
