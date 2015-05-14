package com.smeanox.games.sg002.world;

import com.smeanox.games.sg002.player.Player;
import com.smeanox.games.sg002.util.Consts;

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
		return diffTot >= gameObjectType.getRadiusWalkMin() && diffTot <= gameObjectType.getRadiusWalkMax();
	}

	/**
	 * Checks if the new position is within reach to produce
	 * @param x
	 * @param y
	 * @return true if the object can produce to this position
	 */
	public boolean canProduceTo(int x, int y){
		int diffTot = getDiff(x, y);
		return diffTot >= gameObjectType.getRadiusProduceMin() && diffTot <= gameObjectType.getRadiusProduceMax();
	}

	/**
	 * Checks if the new position is within reach to fight
	 * @param x
	 * @param y
	 * @return true if the object can fight this position
	 */
	public boolean canFightTo(int x, int y){
		int diffTot = getDiff(x, y);
		return diffTot >= gameObjectType.getRadiusFightMin() && diffTot <= gameObjectType.getRadiusFightMax();
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
}
