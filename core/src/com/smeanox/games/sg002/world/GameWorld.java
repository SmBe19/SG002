package com.smeanox.games.sg002.world;

import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.smeanox.games.sg002.player.Player;
import com.smeanox.games.sg002.util.Consts;

import java.awt.Point;
import java.io.IOException;
import java.util.HashSet;

/**
 * Contains all information about the active game
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

	public GameWorld(Scenario scenario){
		initScenario(scenario);

		gameObjects = new HashSet<GameObject>();
	}

	public void initScenario(Scenario scenario){
		mapSizeX = scenario.getMapSizeX();
		mapSizeY = scenario.getMapSizeY();

		worldGameObjects = new GameObject[mapSizeY][mapSizeX];
		worldMapObjects = new MapObject[mapSizeY][mapSizeX];
		for (int x = 0; x < mapSizeY; x++){
			for (int y = 0; y < mapSizeX; y++){
				worldMapObjects[x][y] = new MapObject(MapObjectType.getDefaultMapObjectType(), y, x);
				//coordinates are such a mess -> i have no idea whether this order is correct
			}
		}
		for (Point point : scenario.getGoldPos()){
			worldMapObjects[point.y][point.x] = new MapObject(MapObjectType.getMapObjectTypeById("gold"), point.x, point.y);
		}
		this.scenario = scenario;
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
		return worldGameObjects[y][x];
	}

	public MapObject getWorldMapObject(int x, int y){
		if(x < 0 || y < 0 || x >= mapSizeX || y >= mapSizeY){
			return null;
		}
		return worldMapObjects[y][x];
	}

	public GameObject[][] getWorldMap() {
		return worldGameObjects;
	}
	public MapObject[][] getWorldMapObjects() {
		return worldMapObjects;
	}

	/**
	 * Sets the starting GameObjects for the given player at a random position
	 * @param player
	 */
	public void addStartGameObjects(Player player, GameObjectType gameObjectType){
		int x, y;/*
		do{
			x = scenario.getRandom().nextInt(mapSizeX-1);
			y = scenario.getRandom().nextInt(mapSizeY-1);
		} while(!canAddStartGameObject(x, y));
*/
		x = scenario.getStartPos(player.getId()).y;
		y = scenario.getStartPos(player.getId()).x;
		worldGameObjects[y][x] = new GameObject(gameObjectType, player);
		worldGameObjects[y][x].setPositionX(x);
		worldGameObjects[y][x].setPositionY(y);
		gameObjects.add(worldGameObjects[y][x]);
	}

	/**
	 * Checks if the startGameObject can be placed here
	 * @param px position
	 * @param py position
	 * @return true if it is possible
	 */
	private boolean canAddStartGameObject(int px, int py){
		if(px < 0 || py < 0 || px >= mapSizeX || py >= mapSizeY){
			return false;
		}
		for(int y = py - Consts.startGameObjectMinDistance; y <= py + Consts.startGameObjectMinDistance; y++){
			for(int x = px - Consts.startGameObjectMinDistance; x <= px + Consts.startGameObjectMinDistance; x++){
				if(x < 0 || y < 0 || x >= mapSizeX || y >= mapSizeY){
					continue;
				}
				if(getWorldMap(x, y) != null){
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Called when a player starts his round
	 * @param activePlayer the player
	 * @param reenableUsedActions whether usedActions should be cleared
	 */
	public void startRound(Player activePlayer, boolean reenableUsedActions){
		this.activePlayer = activePlayer;
		if (reenableUsedActions) {
			for (GameObject go : gameObjects) {
				go.resetUsedActions();
			}
		}
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
	 * Checks if a player is still alive
	 * @param player the player
	 * @return true if the player didn't lose yet
	 */
	public boolean isPlayerStillAlive(Player player){
		for(int y = 0; y < mapSizeY; y++){
			for(int x = 0; x < mapSizeX; x++){
				if(getWorldMap(x, y) != null && getWorldMap(x, y).getPlayer() == player){
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * removes the GameObject from the map
	 * @param x coordinates
	 * @param y coordinates
	 */
	public void removeGameObject(int x, int y){
		worldGameObjects[y][x] = null;
	}

	/**
	 * Checks whether a gameObject has any remaining actions
	 * @param x coordinates
	 * @param y coordinates
	 * @return
	 */
	public boolean wasUsed(int x, int y){
		try {
			/*if (x < 0 || y < 0 || x >= mapSizeX || y >= mapSizeY) {
				return true;//what happened here?
			}
			if (worldGameObjects[x][y] == null) return true; //this shouldn't happen either*/
			return !worldGameObjects[y][x].isCanDoAction(Action.ActionType.MOVE) &&
					!worldGameObjects[y][x].isCanDoAction(Action.ActionType.PRODUCE) &&
					!worldGameObjects[y][x].isCanDoAction(Action.ActionType.FIGHT);
		} catch (NullPointerException ex){
			ex.printStackTrace();
		} catch (IndexOutOfBoundsException ex){ // java 6 compatibility
			ex.printStackTrace();
		}
		return true;
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
		if(gameObject.wasUsed(Action.ActionType.MOVE)){
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
		worldGameObjects[endY][endX] = worldGameObjects[startY][startX];
		worldGameObjects[startY][startX] = null;
		getWorldMap(endX, endY).setPositionX(endX);
		getWorldMap(endX, endY).setPositionY(endY);
		getWorldMap(endX, endY).use(Action.ActionType.MOVE);
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
		if(gameObject.wasUsed(Action.ActionType.PRODUCE)){
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
		worldGameObjects[endY][endX] = newGameObject;
		activePlayer.addMoney(-gameObjectType.getValue());
		getWorldMap(startX, startY).use(Action.ActionType.PRODUCE);
		gameObjects.add(newGameObject);
		for (Action.ActionType a : Action.ActionType.values()){
			newGameObject.use(a);//not able to do anything after being built
		}
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
		if(gameObject.wasUsed(Action.ActionType.FIGHT)){
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
			Player otherPlayer = getWorldMap(endX, endY).getPlayer();
			removeGameObject(endX, endY);
			if(!isPlayerStillAlive(otherPlayer)){
				conquerPlayer(activePlayer, otherPlayer);
			}
		}
		getWorldMap(startX, startY).use(Action.ActionType.FIGHT);
		return damage;
	}

	/**
	 * Conquers a player
	 * @param conqueror the winner
	 * @param loser the loser
	 */
	private void conquerPlayer(Player conqueror, Player loser){
		conqueror.addMoney(loser.getMoney());
	}

	/**
	 * Saves the GameWorld
	 * @param writer the XmlWriter
	 */
	public void save(XmlWriter writer) throws IOException {
		writer.element("GameObjects");
		for(int y = 0; y < mapSizeY; y++){
			for(int x = 0; x < mapSizeX; x++){
				if(getWorldMap(x, y) != null){
					writer.element("GameObject");
					if(getWorldMap(x, y).getPositionX() != x || getWorldMap(x, y).getPositionY() != y){
						throw new IOException("Position in worldGameObjects and gameObject doesn't correspond!");
					}
					getWorldMap(x, y).save(writer);
					writer.pop();
				}
			}
		}
		writer.pop();
	}

	/**
	 * loads the GameWorld
	 * @param reader the XmlReader.Element
	 */
	public void load(XmlReader.Element reader){
		worldGameObjects = new GameObject[mapSizeY][mapSizeX];
		XmlReader.Element gameObjects = reader.getChildByName("GameObjects");
		for(XmlReader.Element gameObjectXML : gameObjects.getChildrenByName("GameObject")){
			GameObject gameObject = new GameObject(gameObjectXML);
			worldGameObjects[gameObject.getPositionY()][gameObject.getPositionX()] = gameObject;
			this.gameObjects.add(gameObject);
		}
	}
}
