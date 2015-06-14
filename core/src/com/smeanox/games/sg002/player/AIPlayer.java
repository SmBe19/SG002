package com.smeanox.games.sg002.player;

import com.badlogic.gdx.math.MathUtils;
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

	@Override
	public final void update(float delta) {
		aiSleepLeft -= delta;
		if (finishedPlaying && aiSleepLeft <= 0) {
			finishedPlaying = false;
			endPlaying();
		}
	}

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
				GameObject gameObject = gameWorld.getWorldMap(x, y);
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
				GameObject gameObject = gameWorld.getWorldMap(x, y);
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
	protected LinkedList<Integer> getPositions(Player player) {
		LinkedList<Integer> sol = new LinkedList<Integer>();
		for (int y = 0; y < gameWorld.getMapSizeY(); y++) {
			for (int x = 0; x < gameWorld.getMapSizeX(); x++) {
				GameObject gameObject = gameWorld.getWorldMap(x, y);
				if (gameObject == null) {
					continue;
				}
				if (gameObject.getPlayer() == player) {
					sol.add(compress(x, y));
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
	protected LinkedList<Integer> getMyPositions() {
		return getPositions(this);
	}

	/**
	 * Return a list of all positions where the given player has an object from the given type
	 *
	 * @param player         the player to search
	 * @param gameObjectType the type to search
	 * @return a list of compressed coordinates
	 */
	protected LinkedList<Integer> getPositions(Player player, GameObjectType gameObjectType) {
		LinkedList<Integer> sol = new LinkedList<Integer>();
		for (int y = 0; y < gameWorld.getMapSizeY(); y++) {
			for (int x = 0; x < gameWorld.getMapSizeX(); x++) {
				GameObject gameObject = gameWorld.getWorldMap(x, y);
				if (gameObject == null) {
					continue;
				}
				if (gameObject.getPlayer() == player
						&& gameObject.getGameObjectType() == gameObjectType) {
					sol.add(compress(x, y));
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
	protected LinkedList<Integer> getMyPositions(GameObjectType gameObjectType) {
		return getPositions(this, gameObjectType);
	}

	/**
	 * Return all possible fields to produce to
	 *
	 * @param pos                   field from which to produce
	 * @param produceGameObjectType type to produce
	 * @return list of all possible fields
	 */
	protected LinkedList<Integer> getAllFreeProduceFields(int pos, GameObjectType produceGameObjectType) {
		LinkedList<Integer> sol = new LinkedList<Integer>();
		if (gameWorld.getWorldMap(extractX(pos), extractY(pos)) == null) {
			return sol;
		}
		GameObjectType gameObjectType = gameWorld.getWorldMap(extractX(pos), extractY(pos)).getGameObjectType();
		for (int y = extractY(pos) - gameObjectType.getRadiusProduceMax();
			 y <= extractY(pos) + gameObjectType.getRadiusProduceMax(); y++) {
			for (int x = extractX(pos) - gameObjectType.getRadiusProduceMax();
				 x <= extractX(pos) + gameObjectType.getRadiusProduceMax(); x++) {
				if (gameWorld.canProduce(extractX(pos), extractY(pos), x, y, produceGameObjectType)) {
					sol.add(compress(x, y));
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
	protected int getFirstFreeProduceField(int pos, GameObjectType produceGameObjectType) {
		LinkedList<Integer> allFields = getAllFreeProduceFields(pos, produceGameObjectType);
		if (allFields.isEmpty()) {
			return -1;
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
	protected int getRandomFreeProduceField(int pos, GameObjectType produceGameObjectType) {
		LinkedList<Integer> allFields = getAllFreeProduceFields(pos, produceGameObjectType);
		if (allFields.isEmpty()) {
			return -1;
		}
		return allFields.get(MathUtils.random(allFields.size() - 1));
	}

	/**
	 * Return all possible fields to move to
	 *
	 * @param pos start position
	 * @return list of all possible fields
	 */
	protected LinkedList<Integer> getAllFreeMoveFields(int pos) {
		LinkedList<Integer> sol = new LinkedList<Integer>();
		if (gameWorld.getWorldMap(extractX(pos), extractY(pos)) == null) {
			return sol;
		}
		GameObjectType gameObjectType = gameWorld.getWorldMap(extractX(pos), extractY(pos)).getGameObjectType();
		for (int y = extractY(pos) - gameObjectType.getRadiusWalkMax();
			 y <= extractY(pos) + gameObjectType.getRadiusWalkMax(); y++) {
			for (int x = extractX(pos) - gameObjectType.getRadiusWalkMax();
				 x <= extractX(pos) + gameObjectType.getRadiusWalkMax(); x++) {
				if (gameWorld.canMove(extractX(pos), extractY(pos), x, y)) {
					sol.add(compress(x, y));
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
	protected int getRandomFreeMoveField(int pos) {
		LinkedList<Integer> allFields = getAllFreeMoveFields(pos);
		if (allFields.isEmpty()) {
			return -1;
		}
		return allFields.get(MathUtils.random(allFields.size() - 1));
	}

	/**
	 * Return all possible fields to fight against
	 *
	 * @param pos start position
	 * @return list of all possible fields
	 */
	protected LinkedList<Integer> getAllFightFields(int pos) {
		LinkedList<Integer> sol = new LinkedList<Integer>();
		if (gameWorld.getWorldMap(extractX(pos), extractY(pos)) == null) {
			return sol;
		}
		GameObjectType gameObjectType = gameWorld.getWorldMap(extractX(pos), extractY(pos)).getGameObjectType();
		for (int y = extractY(pos) - gameObjectType.getRadiusFightMax();
			 y <= extractY(pos) + gameObjectType.getRadiusFightMax(); y++) {
			for (int x = extractX(pos) - gameObjectType.getRadiusFightMax();
				 x <= extractX(pos) + gameObjectType.getRadiusFightMax(); x++) {
				if (gameWorld.canFight(extractX(pos), extractY(pos), x, y)) {
					sol.add(compress(x, y));
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
	protected int getRandomFightField(int pos) {
		LinkedList<Integer> allFields = getAllFightFields(pos);
		if (allFields.isEmpty()) {
			return -1;
		}
		return allFields.get(MathUtils.random(allFields.size() - 1));
	}

	/**
	 * Calculate the x coordinate of the given compressed position
	 *
	 * @param pos the compressed position
	 * @return the x coordinate
	 */
	protected int extractX(int pos) {
		return pos % gameWorld.getMapSizeX();
	}

	/**
	 * Calculate the y coordinate of the given compressed position
	 *
	 * @param pos the compressed position
	 * @return the y coordinate
	 */
	protected int extractY(int pos) {
		return pos / gameWorld.getMapSizeX();
	}

	/**
	 * Compress two coordinates into one number
	 *
	 * @param x coordinate
	 * @param y coordinate
	 * @return compressed number
	 */
	protected int compress(int x, int y) {
		return y * gameWorld.getMapSizeX() + x;
	}
}
