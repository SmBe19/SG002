package com.smeanox.games.sg002.world;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Defines stats &amp; co. for a GameObjectTye
 * @author Benjamin Schmid
 */
public class GameObjectType {
	private static HashMap<String, GameObjectType> idToGameObjectType = new HashMap<String, GameObjectType>();
	private static GameObjectType startGameObjectType;

	private String id;
	private String name;
	private String textureName;
	private Texture texture;

	// Stats
	private int defaultHP;
	private int value;
	private int valuePerRound;
	private int valueOnDestruction;
	private int radiusWalkMin;
	private int radiusWalkMax;
	private int radiusProduceMin;
	private int radiusProduceMax;
	private int radiusFightMin;
	private int radiusFightMax;
	private boolean canFight;
	private HashMap<GameObjectType, Integer> damageTable;
	private boolean canProduce;
	private ArrayList<GameObjectType> canProduceList;

	public GameObjectType(
			String id,
			String name,
			String textureName,
			int defaultHP,
			int value,
			int valuePerRound,
			int valueOnDestruction,
			int radiusWalkMin,
			int radiusWalkMax,
			int radiusProduceMin,
			int radiusProduceMax,
			int radiusFightMin,
			int radiusFightMax,
			boolean canFight,
			HashMap<GameObjectType,Integer> damageTable,
			boolean canProduce,
			ArrayList<GameObjectType> canProduceList) {
		this.id = id;
		this.name = name;
		this.textureName = textureName;
		this.defaultHP = defaultHP;
		this.value = value;
		this.valuePerRound = valuePerRound;
		this.valueOnDestruction = valueOnDestruction;
		this.radiusWalkMin = radiusWalkMin;
		this.radiusWalkMax = radiusWalkMax;
		this.radiusProduceMin = radiusProduceMin;
		this.radiusProduceMax = radiusProduceMax;
		this.radiusFightMin = radiusFightMin;
		this.radiusFightMax = radiusFightMax;
		this.canFight = canFight;
		this.damageTable = damageTable;
		this.canProduce = canProduce;
		this.canProduceList = canProduceList;

		idToGameObjectType.put(id, this);
	}

	// Getter
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getTextureName(){
		return textureName;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture){
		this.texture = texture;
	}

	public int getDefaultHP() {
		return defaultHP;
	}

	public int getValue() {
		return value;
	}

	public int getValuePerRound() {
		return valuePerRound;
	}

	public int getValueOnDestruction() {
		return valueOnDestruction;
	}

	public int getRadiusWalkMin() {
		return radiusWalkMin;
	}

	public int getRadiusWalkMax() {
		return radiusWalkMax;
	}

	public int getRadiusProduceMin() {
		return radiusProduceMin;
	}

	public int getRadiusProduceMax() {
		return radiusProduceMax;
	}

	public int getRadiusFightMin() {
		return radiusFightMin;
	}

	public int getRadiusFightMax() {
		return radiusFightMax;
	}

	public boolean isCanFight(){
		return canFight;
	}

	public HashMap<GameObjectType, Integer> getDamageTable() {
		return damageTable;
	}

	public boolean isCanProduce(){
		return canProduce;
	}

	public void addDamageTableEntry(GameObjectType gameObjectType, int damage){
		damageTable.put(gameObjectType, damage);
	}

	public int getDamage(GameObjectType gameObjectType){
		return damageTable.get(gameObjectType);
	}

	public ArrayList<GameObjectType> getCanProduceList() {
		return canProduceList;
	}

	public void addCanProduceEntry(GameObjectType gameObjectType){
		canProduceList.add(gameObjectType);
	}

	/**
	 * returns the GameObjectType with the given name
	 * @param id id of the gameObjectType
	 * @return the GameObjectType
	 */
	public static GameObjectType getGameObjectTypeById(String id){
		return idToGameObjectType.get(id);
	}

	/**
	 * returns all GameObjectTypes
	 * @return a List of all GameObjectTypes
	 */
	public static Collection<GameObjectType> getAllGameObjectTypes(){
		return idToGameObjectType.values();
	}

	/**
	 * returns all GameObjectTypes sorted
	 * @return a List of all GameObjectTypes
	 */
	public static LinkedList<GameObjectType> getAllGameObjectTypesSorted(){
		LinkedList<GameObjectType> gameObjectTypes = new LinkedList<GameObjectType>(idToGameObjectType.values());
		Collections.sort(gameObjectTypes, new Comparator<GameObjectType>() {
			@Override
			public int compare(GameObjectType o1, GameObjectType o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return gameObjectTypes;
	}

	public static GameObjectType getStartGameObjectType() {
		return startGameObjectType;
	}

	public static void setStartGameObjectType(GameObjectType startGameObjectType) {
		GameObjectType.startGameObjectType = startGameObjectType;
	}

	public boolean isCanDoAction(Action.ActionType action){
		switch(action){
			case MOVE:
				return getRadiusWalkMax() > 0;
			case FIGHT:
				return isCanFight();
			case PRODUCE:
				return isCanProduce();
		}
		return false;
	}
}
