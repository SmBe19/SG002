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
 *
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

	/**
	 * Create a new instance and load the state from the given reader
	 *
	 * @param reader the reader to read from
	 */
	public GameObject(XmlReader.Element reader) {
		load(reader);
	}

	/**
	 * Create a new instance of the given type for the given player
	 *
	 * @param gameObjectType the typo of the new object
	 * @param player         the player which owns the object
	 */
	public GameObject(GameObjectType gameObjectType, Player player) {
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

	public void addHp(int hp) {
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

	/**
	 * Whether the object can do the given action in its current state (e.g. the type allows the action and the action wasn't performed this round
	 *
	 * @param action the action to check
	 * @return true if the action can be performed
	 */
	public boolean isCanDoAction(ActionType action) {
		return !usedActions.contains(action) && getGameObjectType().isCanDoAction(action);
	}

	/**
	 * reenable all actions
	 */
	public void resetUsedActions() {
		usedActions.clear();
	}

	/**
	 * Check whether the specified action was already used in this round
	 *
	 * @param action the action to check
	 * @return true if it was already used
	 */
	public boolean wasUsed(ActionType action) {
		if (action == null) return false;
		return usedActions.contains(action);
	}

	/**
	 * Use the given action
	 *
	 * @param action the used action
	 */
	public void use(ActionType action) {
		if (action == null) return;
		usedActions.add(action);

		if(!Consts.multipleActionsPerObject){
			usedActions.add(ActionType.FIGHT);
			usedActions.add(ActionType.MOVE);
			usedActions.add(ActionType.PRODUCE);
		}
	}

	/**
	 * Return the difference between the position of this object to the given field
	 *
	 * @param x the coordinates of the field
	 * @param y the coordinates of the field
	 * @return distance to the given field
	 */
	private int getDiff(int x, int y) {
		int diffX, diffY, diffTot;
		diffX = Math.abs(positionX - x);
		diffY = Math.abs(positionY - y);
		if (Consts.walkDiagonal) {
			diffTot = Math.max(diffX, diffY);
		} else {
			diffTot = diffX + diffY;
		}
		return diffTot;
	}

	/**
	 * Check if the new position is within reach to walk
	 *
	 * @param x new position
	 * @param y new position
	 * @return true if the object can move to this position
	 */
	public boolean canMoveTo(int x, int y) {
		int diffTot = getDiff(x, y);
		return diffTot >= gameObjectType.getRadiusWalkMin() && diffTot <= gameObjectType.getRadiusWalkMax();
	}

	/**
	 * Checks if the new position is within reach to produce
	 *
	 * @param x new position
	 * @param y new position
	 * @return true if the object can produce to this position
	 */
	public boolean canProduceTo(int x, int y) {
		int diffTot = getDiff(x, y);
		return diffTot >= gameObjectType.getRadiusProduceMin() && diffTot <= gameObjectType.getRadiusProduceMax();
	}

	/**
	 * Checks if the new position is within reach to fight
	 *
	 * @param x new position
	 * @param y new position
	 * @return true if the object can fight this position
	 */
	public boolean canFightTo(int x, int y) {
		int diffTot = getDiff(x, y);
		return diffTot >= gameObjectType.getRadiusFightMin() && diffTot <= gameObjectType.getRadiusFightMax();
	}

	/**
	 * Check if the gameObject can be fought
	 *
	 * @param gameObject the GameObject to fight against
	 * @return true if the GameObject can be fought
	 */
	public boolean canFight(GameObject gameObject) {
		return gameObjectType.isCanFight()
				&& canFightTo(gameObject.getPositionX(), gameObject.getPositionY())
				&& !usedActions.contains(ActionType.FIGHT)
				&& gameObject.getPlayer() != player;
	}

	/**
	 * Fight the given GameObject
	 *
	 * @param gameObject the GameObject to fight against
	 * @return the difference in HP
	 */
	public int fight(GameObject gameObject) {
		if (!canFight(gameObject)) {
			return 0;
		}

		int damage = -gameObjectType.getDamage(gameObject.getGameObjectType());
		gameObject.addHp(damage);
		return damage;
	}

	/**
	 * Save the GameObject
	 *
	 * @param writer the XmlWriter to save to
	 */
	public void save(XmlWriter writer) throws IOException {
		writer.attribute("x", positionX);
		writer.attribute("y", positionY);
		writer.attribute("hp", hp);
		writer.attribute("gameObjectType", gameObjectType.getId());
		writer.attribute("player", player.getId());
		writer.element("usedActions");
		for (ActionType action : usedActions) {
			writer.element("action");
			writer.attribute("name", action.name());
			writer.pop();
		}
		writer.pop();
	}

	/**
	 * Load the GameObject
	 *
	 * @param reader the XmlReader.Element to read from
	 */
	public void load(XmlReader.Element reader) {
		positionX = reader.getIntAttribute("x");
		positionY = reader.getIntAttribute("y");
		hp = reader.getIntAttribute("hp");
		gameObjectType = GameObjectType.getGameObjectTypeById(reader.getAttribute("gameObjectType"));
		player = Player.getPlayerById(reader.getIntAttribute("player"));
		for (XmlReader.Element element : reader.getChildByName("usedActions").getChildrenByName("action")) {
			usedActions.add(ActionType.valueOf(element.getAttribute("name")));
		}
	}
}
