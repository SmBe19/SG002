package com.smeanox.games.sg002.world;

import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.smeanox.games.sg002.player.Player;

import java.awt.Point;
import java.io.IOException;
import java.util.HashSet;

/**
 * Contains all information about the active game
 *
 * @author Benjamin Schmid
 */
public class GameWorld {
	private int mapSizeX;
	private int mapSizeY;

	private GameObject[][] worldGameObjects;
	private MapObject[][] worldMapObjects;

	private Player activePlayer;
	private HashSet<GameObject> gameObjects;

	private Scenario scenario;

	/**
	 * Create a new instance
	 *
	 * @param scenario the scenario to use
	 */
	public GameWorld(Scenario scenario) {
		initScenario(scenario);

		gameObjects = new HashSet<GameObject>();
	}

	/**
	 * initializes the values using the given scenario
	 *
	 * @param scenario the scenario to use
	 */
	public void initScenario(Scenario scenario) {
		this.scenario = scenario;

		mapSizeX = scenario.getMapSizeX();
		mapSizeY = scenario.getMapSizeY();

		worldGameObjects = new GameObject[mapSizeY][mapSizeX];
		worldMapObjects = new MapObject[mapSizeY][mapSizeX];
		for (int y = 0; y < mapSizeY; y++) {
			for (int x = 0; x < mapSizeX; x++) {
				worldMapObjects[y][x] = new MapObject(MapObjectType.getDefaultMapObjectType(), x, y);
			}
		}
		for (Point point : scenario.getGoldPos()) {
			worldMapObjects[point.y][point.x] = new MapObject(MapObjectType.getMapObjectTypeById("gold"), point.x, point.y);
		}
	}

	public int getMapSizeX() {
		return mapSizeX;
	}

	public int getMapSizeY() {
		return mapSizeY;
	}

	public Player getActivePlayer() {
		return activePlayer;
	}

	/**
	 * Return the GameObject at the given position or null if there is no GameObject
	 *
	 * @param x position
	 * @param y position
	 * @return the GameObject or null
	 */
	public GameObject getWorldGameObject(int x, int y) {
		if (x < 0 || y < 0 || x >= mapSizeX || y >= mapSizeY) {
			return null;
		}
		return worldGameObjects[y][x];
	}

	public MapObject getWorldMapObject(int x, int y) {
		if (x < 0 || y < 0 || x >= mapSizeX || y >= mapSizeY) {
			return null;
		}
		return worldMapObjects[y][x];
	}

	public GameObject[][] getWorldGameObjects() {
		return worldGameObjects;
	}

	public MapObject[][] getWorldMapObjects() {
		return worldMapObjects;
	}

	/**
	 * Add the starting GameObjects for the given player at a random position
	 *
	 * @param player         the player for which the starting GameObject should be added
	 * @param gameObjectType the type of the object to add
	 */
	public void addStartGameObjects(Player player, GameObjectType gameObjectType) {
		int x, y;
		x = scenario.getStartPos(player.getId()).x;
		y = scenario.getStartPos(player.getId()).y;
		worldGameObjects[y][x] = new GameObject(gameObjectType, player);
		worldGameObjects[y][x].setPositionX(x);
		worldGameObjects[y][x].setPositionY(y);
		gameObjects.add(worldGameObjects[y][x]);
	}

	/**
	 * Called when a player starts his round
	 *
	 * @param activePlayer        the player
	 * @param reenableUsedActions whether usedActions should be cleared
	 */
	public void startRound(Player activePlayer, boolean reenableUsedActions) {
		this.activePlayer = activePlayer;
		if (reenableUsedActions) {
			for (GameObject go : gameObjects) {
				go.resetUsedActions();
			}
		}
		activePlayer.addMoney(calcMoneyPerRound(activePlayer));
	}

	/**
	 * Return the amount the given player receives per round
	 *
	 * @param activePlayer the player for which the amount should be calculated
	 * @return the amount the player receives per round
	 */
	private int calcMoneyPerRound(Player activePlayer) {
		int sol = 0;
		for (GameObject gameObject : gameObjects) {
			if (gameObject.getPlayer() == activePlayer) {
				sol += gameObject.getGameObjectType().getValuePerRound();
			}
		}
		return sol;
	}

	/**
	 * execute the given action
	 *
	 * @param action the action
	 * @return true if the action was successful
	 */
	public boolean doAction(Action action) {
		switch (action.actionType) {
			case MOVE:
				return move(action.startX, action.startY, action.endX, action.endY);
			case FIGHT:
				return fight(action.startX, action.startY, action.endX, action.endY) < 0;
			case PRODUCE:
				return produce(action.startX, action.startY, action.endX, action.endY, action.produceGameObjectType);
		}
		return false;
	}

	/**
	 * Check if a player is still alive
	 *
	 * @param player the player
	 * @return true if the player didn't lose yet
	 */
	public boolean isPlayerStillAlive(Player player) {
		for (GameObject gameObject : gameObjects) {
			if (gameObject.getPlayer() == player) {
				return true;
			}
		}
		return false;
	}


	/**
	 * remove the GameObject from the map
	 *
	 * @param x coordinates
	 * @param y coordinates
	 */
	public void removeGameObject(int x, int y) {
		if (worldGameObjects[y][x] != null) {
			gameObjects.remove(worldGameObjects[y][x]);
		}
		worldGameObjects[y][x] = null;
	}

	/**
	 * Check whether a gameObject has any remaining actions
	 *
	 * @param x coordinates
	 * @param y coordinates
	 * @return true if all actions were used
	 */
	public boolean wasUsed(int x, int y) {
		try {
			/*if (x < 0 || y < 0 || x >= mapSizeX || y >= mapSizeY) {
				return true;//what happened here?
			}
			if (worldGameObjects[x][y] == null) return true; //this shouldn't happen either*/
			return !worldGameObjects[y][x].isCanDoAction(Action.ActionType.MOVE) &&
					!worldGameObjects[y][x].isCanDoAction(Action.ActionType.PRODUCE) &&
					!worldGameObjects[y][x].isCanDoAction(Action.ActionType.FIGHT);
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		} catch (IndexOutOfBoundsException ex) { // java 6 compatibility
			ex.printStackTrace();
		}
		return true;
	}

	/**
	 * check if the GameObject can be moved to the given position
	 *
	 * @param startX start coordinates
	 * @param startY start coordinates
	 * @param endX   end coordinates
	 * @param endY   end coordinates
	 * @return true if it can be moved
	 */
	public boolean canMove(int startX, int startY, int endX, int endY) {
		if (startX < 0 || startY < 0 || startX >= mapSizeX || startY >= mapSizeY) {
			return false;
		}
		if (endX < 0 || endY < 0 || endX >= mapSizeX || endY >= mapSizeY) {
			return false;
		}

		GameObject gameObject = getWorldGameObject(startX, startY);
		// there is no GameObject at the start
		if (gameObject == null) {
			return false;
		}
		// the destination is blocked
		if (getWorldGameObject(endX, endY) != null) {
			return false;
		}
		// the destination is not within radius
		if (!gameObject.canMoveTo(endX, endY)) {
			return false;
		}
		// the gameObject has been used already
		if (gameObject.wasUsed(Action.ActionType.MOVE)) {
			return false;
		}
		return true;
	}

	/**
	 * move the GameObject to the given position
	 *
	 * @param startX start coordinates
	 * @param startY start coordinates
	 * @param endX   end coordinates
	 * @param endY   end coordinates
	 * @return true if the move was successful
	 */
	public boolean move(int startX, int startY, int endX, int endY) {
		if (!canMove(startX, startY, endX, endY)) {
			return false;
		}
		worldGameObjects[endY][endX] = worldGameObjects[startY][startX];
		worldGameObjects[startY][startX] = null;
		getWorldGameObject(endX, endY).setPositionX(endX);
		getWorldGameObject(endX, endY).setPositionY(endY);
		getWorldGameObject(endX, endY).use(Action.ActionType.MOVE);
		return true;
	}

	/**
	 * check if the GameObject can produce to the given position
	 *
	 * @param startX start coordinates
	 * @param startY start coordinates
	 * @param endX   end coordinates
	 * @param endY   end coordinates
	 * @return true if it can produce
	 */
	public boolean canProduce(int startX, int startY, int endX, int endY, GameObjectType gameObjectType) {
		if (startX < 0 || startY < 0 || startX >= mapSizeX || startY >= mapSizeY) {
			return false;
		}
		if (endX < 0 || endY < 0 || endX >= mapSizeX || endY >= mapSizeY) {
			return false;
		}

		GameObject gameObject = getWorldGameObject(startX, startY);
		// there is no GameObject at the start
		if (gameObject == null) {
			return false;
		}
		// the destination is blocked
		if (getWorldGameObject(endX, endY) != null) {
			return false;
		}
		// the destination is not within radius
		if (!gameObject.canProduceTo(endX, endY)) {
			return false;
		}
		// target mapObject does not allow this gameObjectType
		if (!getWorldMapObject(endX, endY).getMapObjectType().isGameObjectTypeAllowed(gameObjectType)) {
			return false;
		}
		// the active GameObjectType can't produce the desired GameObjectType
		if (!gameObject.getGameObjectType().getCanProduceList().contains(gameObjectType)) {
			return false;
		}
		// the new GameObject is too expensive
		if (getActivePlayer().getMoney() < gameObjectType.getValue()) {
			return false;
		}
		// the gameObject has been used already
		if (gameObject.wasUsed(Action.ActionType.PRODUCE)) {
			return false;
		}
		return true;
	}

	/**
	 * Produce a new GameObject at the given position
	 *
	 * @param startX start coordinates
	 * @param startY start coordinates
	 * @param endX   end coordinates
	 * @param endY   end coordinates
	 * @return true if the production was successful
	 */
	public boolean produce(int startX, int startY, int endX, int endY, GameObjectType gameObjectType) {
		if (!canProduce(startX, startY, endX, endY, gameObjectType)) {
			return false;
		}
		GameObject newGameObject = new GameObject(gameObjectType, getActivePlayer());
		newGameObject.setPositionX(endX);
		newGameObject.setPositionY(endY);
		worldGameObjects[endY][endX] = newGameObject;
		gameObjects.add(newGameObject);
		getActivePlayer().addMoney(-gameObjectType.getValue());
		getWorldGameObject(startX, startY).use(Action.ActionType.PRODUCE);
		for (Action.ActionType a : Action.ActionType.values()) {
			newGameObject.use(a);//not able to do anything after being built
		}
		return true;
	}

	/**
	 * check if the GameObject can fight the other GameObject
	 *
	 * @param startX start coordinates
	 * @param startY start coordinates
	 * @param endX   end coordinates
	 * @param endY   end coordinates
	 * @return true if it can fight
	 */
	public boolean canFight(int startX, int startY, int endX, int endY) {
		if (startX < 0 || startY < 0 || startX >= mapSizeX || startY >= mapSizeY) {
			return false;
		}
		if (endX < 0 || endY < 0 || endX >= mapSizeX || endY >= mapSizeY) {
			return false;
		}

		GameObject gameObject = getWorldGameObject(startX, startY);
		// there is no GameObject at the start
		if (gameObject == null) {
			return false;
		}
		// there is no GameObject at the destination
		if (getWorldGameObject(endX, endY) == null) {
			return false;
		}
		// the destination is not within radius
		if (!gameObject.canFight(getWorldGameObject(endX, endY))) {
			return false;
		}
		// the gameObject has been used already
		if (gameObject.wasUsed(Action.ActionType.FIGHT)) {
			return false;
		}
		return true;
	}

	/**
	 * fight the other GameObject
	 *
	 * @param startX start coordinates
	 * @param startY start coordinates
	 * @param endX   end coordinates
	 * @param endY   end coordinates
	 * @return the difference in HP
	 */
	public int fight(int startX, int startY, int endX, int endY) {
		if (!canFight(startX, startY, endX, endY)) {
			return 0;
		}
		int damage = getWorldGameObject(startX, startY).fight(getWorldGameObject(endX, endY));
		if (getWorldGameObject(endX, endY).getHp() <= 0) {
			getActivePlayer().addMoney(getWorldGameObject(endX, endY).getGameObjectType().getValueOnDestruction());
			Player otherPlayer = getWorldGameObject(endX, endY).getPlayer();
			removeGameObject(endX, endY);
			if (!isPlayerStillAlive(otherPlayer)) {
				conquerPlayer(getActivePlayer(), otherPlayer);
			}
		}
		getWorldGameObject(startX, startY).use(Action.ActionType.FIGHT);
		return damage;
	}

	/**
	 * Conquer a player
	 *
	 * @param conqueror the winner
	 * @param loser     the loser
	 */
	private void conquerPlayer(Player conqueror, Player loser) {
		conqueror.addMoney(loser.getMoney());
		for (GameObject gameObject : gameObjects) {
			if (gameObject.getPlayer() == loser) {
				gameObject.setPlayer(conqueror);
			}
		}
	}

	/**
	 * Save the GameWorld
	 *
	 * @param writer the XmlWriter to write to
	 */
	public void save(XmlWriter writer) throws IOException {
		writer.element("GameObjects");
		for (int y = 0; y < mapSizeY; y++) {
			for (int x = 0; x < mapSizeX; x++) {
				if (getWorldGameObject(x, y) != null) {
					writer.element("GameObject");
					if (getWorldGameObject(x, y).getPositionX() != x || getWorldGameObject(x, y).getPositionY() != y) {
						throw new IOException("Position in worldGameObjects and gameObject doesn't correspond!");
					}
					getWorldGameObject(x, y).save(writer);
					writer.pop();
				}
			}
		}
		writer.pop();
	}

	/**
	 * load the GameWorld
	 *
	 * @param reader the XmlReader.Element to read from
	 */
	public void load(XmlReader.Element reader) {
		worldGameObjects = new GameObject[mapSizeY][mapSizeX];
		XmlReader.Element gameObjects = reader.getChildByName("GameObjects");
		for (XmlReader.Element gameObjectXML : gameObjects.getChildrenByName("GameObject")) {
			GameObject gameObject = new GameObject(gameObjectXML);
			worldGameObjects[gameObject.getPositionY()][gameObject.getPositionX()] = gameObject;
			this.gameObjects.add(gameObject);
		}
	}
}
