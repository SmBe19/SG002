package com.smeanox.games.sg002.world;

import com.badlogic.gdx.math.MathUtils;
import com.smeanox.games.sg002.player.Player;

import java.util.HashSet;

/**
 * Contains all information about the active game
 * @author Benjamin Schmid
 */
public class GameWorld {
	private int mapSizeX;
	private int mapSizeY;

	private GameObject[][] worldMap;

	private Player activePlayer;
	private HashSet<GameObject> usedGameObjects;

	public GameWorld(Scenario scenario){
		mapSizeX = scenario.getMapSizeX();
		mapSizeY = scenario.getMapSizeY();

		worldMap = new GameObject[mapSizeY][mapSizeX];

		usedGameObjects = new HashSet<GameObject>();
	}

	public int getMapSizeX() {
		return mapSizeX;
	}

	public int getMapSizeY() {
		return mapSizeY;
	}

	public GameObject getWorldMap(int x, int y){
		if(x < 0 || y < 0 || x >= mapSizeX || y >= mapSizeY){
			return null;
		}
		return worldMap[y][x];
	}

	public GameObject[][] getWorldMap() {
		return worldMap;
	}

	/**
	 * Sets the starting GameObjects for the given player at a random position
	 * @param player
	 */
	public void addStartGameObjects(Player player, GameObjectType gameObjectType){
		int x, y;
		do{
			x = MathUtils.random(mapSizeX-1);
			y = MathUtils.random(mapSizeY-1);
		} while(getWorldMap(x, y) != null);

		worldMap[y][x] = new GameObject(gameObjectType, player);
		worldMap[y][x].setPositionX(x);
		worldMap[y][x].setPositionY(y);
	}

	/**
	 * Called when a player starts his round
	 * @param activePlayer the player
	 */
	public void startRound(Player activePlayer){
		this.activePlayer = activePlayer;
		usedGameObjects.clear();
		activePlayer.addMoney(calcMoneyPerRound(activePlayer));
	}

	private int calcMoneyPerRound(Player activePlayer){
		int sol = 0;
		for(int y = 0; y < mapSizeY; y++){
			for(int x = 0; x < mapSizeX; x++){
				if(getWorldMap(x, y) != null && getWorldMap(x, y).getPlayer() == activePlayer){
					sol += getWorldMap(x, y).getGameObjectType().getValuePerRound();
				}
			}
		}
		return sol;
	}

	/**
	 * executes the given action
	 * @param action the action
	 * @return true if the action was successful
	 */
	public boolean doAction(Action action){
		switch (action.actionType){
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
	 * Checks if the given GameObject was used
	 * @param x coordinates
	 * @param y coordinates
	 * @return true if it was used
	 */
	public boolean wasUsed(int x, int y){
		if(getWorldMap(x, y) == null){
			return false;
		}
		return usedGameObjects.contains(getWorldMap(x, y));
	}

	/**
	 * removes the GameObject from the map
	 * @param x coordinates
	 * @param y coordinates
	 */
	public void removeGameObject(int x, int y){
		worldMap[y][x] = null;
	}

	/**
	 * checks if the GameObject can be moved
	 * @param startX start coordinates
	 * @param startY start coordinates
	 * @param endX end coordinates
	 * @param endY end coordinates
	 * @return true if it can be moved
	 */
	public boolean canMove(int startX, int startY, int endX, int endY){
		if(startX < 0 || startY < 0 || startX >= mapSizeX || startY >= mapSizeY){
			return false;
		}
		if(endX < 0 || endY < 0 || endX >= mapSizeX || endY >= mapSizeY){
			return false;
		}

		GameObject gameObject = getWorldMap(startX, startY);
		// there is no GameObject at the start
		if(gameObject == null){
			return false;
		}
		// the destination is blocked
		if(getWorldMap(endX, endY) != null){
			return false;
		}
		// the destination is not within radius
		if(!gameObject.canMoveTo(endX, endY)){
			return false;
		}
		// the gameObject has been used already
		if(usedGameObjects.contains(gameObject)){
			return false;
		}
		return true;
	}

	/**
	 * moves the GameObject
	 * @param startX start coordinates
	 * @param startY start coordinates
	 * @param endX end coordinates
	 * @param endY end coordinates
	 * @return true if the move was successful
	 */
	public boolean move(int startX, int startY, int endX, int endY){
		if(!canMove(startX, startY, endX, endY)){
			return false;
		}
		worldMap[endY][endX] = worldMap[startY][startX];
		worldMap[startY][startX] = null;
		getWorldMap(endX, endY).setPositionX(endX);
		getWorldMap(endX, endY).setPositionY(endY);
		usedGameObjects.add(getWorldMap(endX, endY));
		return true;
	}

	/**
	 * checks if the GameObject can produce
	 * @param startX start coordinates
	 * @param startY start coordinates
	 * @param endX end coordinates
	 * @param endY end coordinates
	 * @return true if it can produce
	 */
	public boolean canProduce(int startX, int startY, int endX, int endY, GameObjectType gameObjectType){
		if(startX < 0 || startY < 0 || startX >= mapSizeX || startY >= mapSizeY){
			return false;
		}
		if(endX < 0 || endY < 0 || endX >= mapSizeX || endY >= mapSizeY){
			return false;
		}

		GameObject gameObject = getWorldMap(startX, startY);
		// there is no GameObject at the start
		if(gameObject == null){
			return false;
		}
		// the destination is blocked
		if(getWorldMap(endX, endY) != null){
			return false;
		}
		// the destination is not within radius
		if(!gameObject.canProduceTo(endX, endY)){
			return false;
		}
		// the active GameObjectType can't produce the desired GameObjectType
		if(!gameObject.getGameObjectType().getCanProduceList().contains(gameObjectType)){
			return false;
		}
		// the new GameObject is too expensive
		if(activePlayer.getMoney() < gameObjectType.getValue()){
			return false;
		}
		// the gameObject has been used already
		if(usedGameObjects.contains(gameObject)){
			return false;
		}
		return true;
	}

	/**
	 * Produces a new GameObject
	 * @param startX start coordinates
	 * @param startY start coordinates
	 * @param endX end coordinates
	 * @param endY end coordinates
	 * @return true if the production was successful
	 */
	public boolean produce(int startX, int startY, int endX, int endY, GameObjectType gameObjectType){
		if(!canProduce(startX, startY, endX, endY, gameObjectType)){
			return false;
		}
		GameObject newGameObject = new GameObject(gameObjectType, activePlayer);
		newGameObject.setPositionX(endX);
		newGameObject.setPositionY(endY);
		worldMap[endY][endX] = newGameObject;
		activePlayer.addMoney(-gameObjectType.getValue());
		usedGameObjects.add(getWorldMap(startX, startY));
		usedGameObjects.add(newGameObject);
		return true;
	}

	/**
	 * checks if the GameObject can fight the other GameObject
	 * @param startX start coordinates
	 * @param startY start coordinates
	 * @param endX end coordinates
	 * @param endY end coordinates
	 * @return true if it can fight
	 */
	public boolean canFight(int startX, int startY, int endX, int endY){
		if(startX < 0 || startY < 0 || startX >= mapSizeX || startY >= mapSizeY){
			return false;
		}
		if(endX < 0 || endY < 0 || endX >= mapSizeX || endY >= mapSizeY){
			return false;
		}

		GameObject gameObject = getWorldMap(startX, startY);
		// there is no GameObject at the start
		if(gameObject == null){
			return false;
		}
		// there is no GameObject at the destination
		if(getWorldMap(endX, endY) == null){
			return false;
		}
		// the destination is not within radius
		if(!gameObject.canFight(getWorldMap(endX, endY))){
			return false;
		}
		// the gameObject has been used already
		if(usedGameObjects.contains(gameObject)){
			return false;
		}
		return true;
	}

	/**
	 * fights the other GameObject
	 * @param startX start coordinates
	 * @param startY start coordinates
	 * @param endX end coordinates
	 * @param endY end coordinates
	 * @return the difference in HP
	 */
	public int fight(int startX, int startY, int endX, int endY){
		if(!canFight(startX, startY, endX, endY)){
			return 0;
		}
		int damage = getWorldMap(startX, startY).fight(getWorldMap(endX, endY));
		if(getWorldMap(endX, endY).getHp() <= 0){
			activePlayer.addMoney(getWorldMap(endX, endY).getGameObjectType().getValueOnDestruction());
			removeGameObject(endX, endY);
		}
		usedGameObjects.add(getWorldMap(startX, startY));
		return damage;
	}
}
