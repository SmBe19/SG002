package com.smeanox.games.sg002.world;

import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.smeanox.games.sg002.player.Player;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.world.Action.ActionType;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Describes an active GameObject
 * @author Benjamin Schmid
 */
public class GameObject {
	private GameObjectType gameObjectType;
	private int positionX;
	private int positionY;
	private Player player;

	// Stats
	private int hp;

	private Set<ActionType> usedActions = new HashSet();

	public GameObject(XmlReader.Element reader){
		load(reader);
	}

	public GameObject(GameObjectType gameObjectType, Player player){
		this.gameObjectType = gameObjectType;
		this.player = player;
		this.hp = gameObjectType.getDefaultHP();
	}

	public GameObjectType getGameObjectType() {
		return gameObjectType;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public void addHp(int hp){
		this.hp += hp;
	}

	public int getPositionX() {
		return positionX;
	}

	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public boolean isCanDoAction(ActionType action){
		return !usedActions.contains(action) && getGameObjectType().isCanDoAction(action);
	}

	/**
	 * reenables all actions
	 */
	public void resetUsedActions(){
		usedActions.clear();
	}

	/**
	 * Checks whether the specified action was already used in this round
	 * @param action
	 * @return true if it was already used
	 */
	public boolean wasUsed(ActionType action){
		if (action == null) return false;
		return usedActions.contains(action);
	}
	/**
	 * Uses a specific action
	 * @param action
	 * @return true if it was already used
	 */
	public void use(ActionType action){
		if (action == null) return;
		usedActions.add(action);
	}

	private int getDiff(int x, int y){
		int diffX, diffY, diffTot;
		diffX = Math.abs(positionX - x);
		diffY = Math.abs(positionY - y);
		if(Consts.walkDiagonal){
			diffTot = Math.max(diffX, diffY);
		} else {
			diffTot = diffX + diffY;
		}
		return diffTot;
	}

	/**
	 * Checks if the new position is within reach to walk
	 * @param x
	 * @param y
	 * @return true if the object can move to this position
	 */
	public boolean canMoveTo(int x, int y){
		int diffTot = getDiff(x, y);
		return diffTot >= gameObjectType.getRadiusWalkMin() && diffTot <= gameObjectType.getRadiusWalkMax() &&
				!usedActions.contains(ActionType.MOVE);
	}

	/**
	 * Checks if the new position is within reach to produce
	 * @param x
	 * @param y
	 * @return true if the object can produce to this position
	 */
	public boolean canProduceTo(int x, int y){
		int diffTot = getDiff(x, y);
		return diffTot >= gameObjectType.getRadiusProduceMin() && diffTot <= gameObjectType.getRadiusProduceMax() &&
				!usedActions.contains(ActionType.PRODUCE);
	}

	/**
	 * Checks if the new position is within reach to fight
	 * @param x
	 * @param y
	 * @return true if the object can fight this position
	 */
	public boolean canFightTo(int x, int y){
		int diffTot = getDiff(x, y);
		return diffTot >= gameObjectType.getRadiusFightMin() && diffTot <= gameObjectType.getRadiusFightMax() &&
				!usedActions.contains(ActionType.FIGHT);
	}

	/**
	 * Checks if the gameObject can be fought
	 * @param gameObject the GameObject to fight against
	 * @return true if the GameObject can be fought
	 */
	public boolean canFight(GameObject gameObject){
		return gameObjectType.isCanFight()
				&& canFightTo(gameObject.getPositionX(), gameObject.getPositionY())
				&& gameObject.getPlayer() != player;
	}

	/**
	 * Fights the given GameObject
	 * @param gameObject the GameObject to fight against
	 * @return the difference in HP
	 */
	public int fight(GameObject gameObject){
		if(!canFight(gameObject)){
			return 0;
		}

		int damage = -gameObjectType.getDamage(gameObject.getGameObjectType());
		gameObject.addHp(damage);
		return damage;
	}

	/**
	 * Saves the GameObject
	 * @param writer the XmlWriter
	 */
	public void save(XmlWriter writer) throws IOException {
		writer.attribute("x", positionX);
		writer.attribute("y", positionY);
		writer.attribute("hp", hp);
		writer.attribute("gameObjectType", gameObjectType.getId());
		writer.attribute("player", player.getId());
		writer.element("usedActions");
		for (ActionType action : usedActions){
			writer.element("action");
			writer.attribute("name",action.name());
			writer.pop();
		}
		writer.pop();
	}

	/**
	 * Loads the GameObject
	 * @param reader the XmlReader.Element
	 */
	public void load(XmlReader.Element reader){
		positionX = reader.getIntAttribute("x");
		positionY = reader.getIntAttribute("y");
		hp = reader.getIntAttribute("hp");
		gameObjectType = GameObjectType.getGameObjectTypeById(reader.getAttribute("gameObjectType"));
		player = Player.getPlayerById(reader.getIntAttribute("player"));
		for (XmlReader.Element element : reader.getChildByName("usedActions").getChildrenByName("action")){
			usedActions.add(ActionType.valueOf(element.getAttribute("name")));
		}
	}
}
