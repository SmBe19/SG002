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

	/**
	 * Count all objects of the given player
	 *
	 * @param player the player to count the objects
	 * @return the number of objects
	 */
	protected int countObjects(Player player) {
		int count = 0;
		for (int y = 0; y < gameWorld.getMapSizeY(); y++) {
			for (int x = 0; x < gameWorld.getMapSizeX(); x++) {
				GameObject gameObject = gameWorld.getWorldGameObject(x, y);
				if (gameObject == null) {
					continue;
				}
				if (gameObject.getPlayer() == player) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Count all objects of this player
	 *
	 * @see #countObjects(Player)
	 */
	protected int countMyObjects() {
		return countObjects(this);
	}

	/**
	 * Count all objects of a given player of the given type
	 *
	 * @param player         the player to count the objects
	 * @param gameObjectType the type to count
	 * @return the number of objects
	 */
	protected int countObjects(Player player, GameObjectType gameObjectType) {
		int count = 0;
		for (int y = 0; y < gameWorld.getMapSizeY(); y++) {
			for (int x = 0; x < gameWorld.getMapSizeX(); x++) {
				GameObject gameObject = gameWorld.getWorldGameObject(x, y);
				if (gameObject == null) {
					continue;
				}
				if (gameObject.getPlayer() == player
						&& gameObject.getGameObjectType() == gameObjectType) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Count all objects of this player of a given type
	 *
	 * @see #countObjects(Player, GameObjectType)
	 */
	protected int countMyObjects(GameObjectType gameObjectType) {
		return countObjects(this, gameObjectType);
	}

	/**
	 * Return a list of all positions where the given player has an object
	 *
	 * @param player the player to search
	 * @return a list of compressed coordinates
	 */
	protected LinkedList<Point> getPositions(Player player) {
		LinkedList<Point> sol = new LinkedList<Point>();
		for (int y = 0; y < gameWorld.getMapSizeY(); y++) {
			for (int x = 0; x < gameWorld.getMapSizeX(); x++) {
				GameObject gameObject = gameWorld.getWorldGameObject(x, y);
				if (gameObject == null) {
					continue;
				}
				if (gameObject.getPlayer() == player) {
					sol.add(new Point(x, y));
				}
			}
		}
		return sol;
	}

	/**
	 * Return a list of all positions where this player has an object
	 *
	 * @see #getPositions(Player)
	 */
	protected LinkedList<Point> getMyPositions() {
		return getPositions(this);
	}

	/**
	 * Return a list of all positions where the given player has an object from the given type
	 *
	 * @param player         the player to search
	 * @param gameObjectType the type to search
	 * @return a list of compressed coordinates
	 */
	protected LinkedList<Point> getPositions(Player player, GameObjectType gameObjectType) {
		LinkedList<Point> sol = new LinkedList<Point>();
		for (int y = 0; y < gameWorld.getMapSizeY(); y++) {
			for (int x = 0; x < gameWorld.getMapSizeX(); x++) {
				GameObject gameObject = gameWorld.getWorldGameObject(x, y);
				if (gameObject == null) {
					continue;
				}
				if (gameObject.getPlayer() == player
						&& gameObject.getGameObjectType() == gameObjectType) {
					sol.add(new Point(x, y));
				}
			}
		}
		return sol;
	}

	/**
	 * Return a list of all positions where this player has an object from the given type
	 *
	 * @see #getPositions(Player, GameObjectType)
	 */
	protected LinkedList<Point> getMyPositions(GameObjectType gameObjectType) {
		return getPositions(this, gameObjectType);
	}

	/**
	 * Return all possible fields to produce to
	 *
	 * @param pos                   field from which to produce
	 * @param produceGameObjectType type to produce
	 * @return list of all possible fields
	 */
	protected LinkedList<Point> getAllFreeProduceFields(Point pos, GameObjectType produceGameObjectType) {
		LinkedList<Point> sol = new LinkedList<Point>();
		if (gameWorld.getWorldGameObject(pos.x, pos.y) == null) {
			return sol;
		}
		GameObjectType gameObjectType = gameWorld.getWorldGameObject(pos.x, pos.y).getGameObjectType();
		for (int y = pos.y - gameObjectType.getRadiusProduceMax();
			 y <= pos.y + gameObjectType.getRadiusProduceMax(); y++) {
			for (int x = pos.x - gameObjectType.getRadiusProduceMax();
				 x <= pos.x + gameObjectType.getRadiusProduceMax(); x++) {
				if (gameWorld.canProduce(pos.x, pos.y, x, y, produceGameObjectType)) {
					sol.add(new Point(x, y));
				}
			}
		}
		return sol;
	}

	/**
	 * Return one arbitrary possible field to produce to
	 *
	 * @param pos                   field from which to produce
	 * @param produceGameObjectType type to produce
	 * @return compressed coordinate of a possible field
	 */
	protected Point getFirstFreeProduceField(Point pos, GameObjectType produceGameObjectType) {
		LinkedList<Point> allFields = getAllFreeProduceFields(pos, produceGameObjectType);
		if (allFields.isEmpty()) {
			return null;
		}
		return allFields.get(0);
	}

	/**
	 * Return one random possible field to produce to
	 *
	 * @param pos                   field from which to produce
	 * @param produceGameObjectType type to produce
	 * @return compressed coordinate of a possible field
	 */
	protected Point getRandomFreeProduceField(Point pos, GameObjectType produceGameObjectType) {
		LinkedList<Point> allFields = getAllFreeProduceFields(pos, produceGameObjectType);
		if (allFields.isEmpty()) {
			return null;
		}
		return allFields.get(MathUtils.random(allFields.size() - 1));
	}

	/**
	 * Return all possible fields to move to
	 *
	 * @param pos start position
	 * @return list of all possible fields
	 */
	protected LinkedList<Point> getAllFreeMoveFields(Point pos) {
		LinkedList<Point> sol = new LinkedList<Point>();
		if (gameWorld.getWorldGameObject(pos.x, pos.y) == null) {
			return sol;
		}
		GameObjectType gameObjectType = gameWorld.getWorldGameObject(pos.x, pos.y).getGameObjectType();
		for (int y = pos.y - gameObjectType.getRadiusWalkMax();
			 y <= pos.y + gameObjectType.getRadiusWalkMax(); y++) {
			for (int x = pos.x - gameObjectType.getRadiusWalkMax();
				 x <= pos.x + gameObjectType.getRadiusWalkMax(); x++) {
				if (gameWorld.canMove(pos.x, pos.y, x, y)) {
					sol.add(new Point(x, y));
				}
			}
		}
		return sol;
	}

	/**
	 * Return a random possible field to move to
	 *
	 * @param pos start position
	 * @return compressed coordinate of a possible field
	 */
	protected Point getRandomFreeMoveField(Point pos) {
		LinkedList<Point> allFields = getAllFreeMoveFields(pos);
		if (allFields.isEmpty()) {
			return null;
		}
		return allFields.get(MathUtils.random(allFields.size() - 1));
	}

	/**
	 * Return all possible fields to fight against
	 *
	 * @param pos start position
	 * @return list of all possible fields
	 */
	protected LinkedList<Point> getAllFightFields(Point pos) {
		LinkedList<Point> sol = new LinkedList<Point>();
		if (gameWorld.getWorldGameObject(pos.x, pos.y) == null) {
			return sol;
		}
		GameObjectType gameObjectType = gameWorld.getWorldGameObject(pos.x, pos.y).getGameObjectType();
		for (int y = pos.y - gameObjectType.getRadiusFightMax();
			 y <= pos.y + gameObjectType.getRadiusFightMax(); y++) {
			for (int x = pos.x - gameObjectType.getRadiusFightMax();
				 x <= pos.x + gameObjectType.getRadiusFightMax(); x++) {
				if (gameWorld.canFight(pos.x, pos.y, x, y)) {
					sol.add(new Point(x, y));
				}
			}
		}
		return sol;
	}

	/**
	 * Return a random possible field to fight against
	 *
	 * @param pos start position
	 * @return compressed coordinate of a possible field
	 */
	protected Point getRandomFightField(Point pos) {
		LinkedList<Point> allFields = getAllFightFields(pos);
		if (allFields.isEmpty()) {
			return null;
		}
		return allFields.get(MathUtils.random(allFields.size() - 1));
	}
}
