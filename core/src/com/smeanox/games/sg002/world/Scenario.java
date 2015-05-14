package com.smeanox.games.sg002.world;

import java.util.Collection;
import java.util.HashMap;

/**
 * Describes a Scenario
 * @author Benjamin Schmid
 */
public class Scenario {
	private static HashMap<String, Scenario> idToScenario = new HashMap<String, Scenario>();

	private String id;
	private String name;
	private int startMoney;
	private int maxPlayerCount;
	private int mapSizeX;
	private int mapSizeY;
	private boolean walkDiagonal;
	private int startGameObjectMinDistance;

	public Scenario(String id, String name, int startMoney, int maxPlayerCount, int mapSizeX,
					int mapSizeY, boolean walkDiagonal, int startGameObjectMinDistance) {
		this.id = id;
		this.name = name;
		this.startMoney = startMoney;
		this.maxPlayerCount = maxPlayerCount;
		this.mapSizeX = mapSizeX;
		this.mapSizeY = mapSizeY;
		this.walkDiagonal = walkDiagonal;
		this.startGameObjectMinDistance = startGameObjectMinDistance;

		idToScenario.put(id, this);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getStartMoney() {
		return startMoney;
	}

	public int getMaxPlayerCount() {
		return maxPlayerCount;
	}

	public int getMapSizeX() {
		return mapSizeX;
	}

	public int getMapSizeY() {
		return mapSizeY;
	}

	public boolean isWalkDiagonal(){
		return walkDiagonal;
	}

	public int getStartGameObjectMinDistance(){
		return startGameObjectMinDistance;
	}

	/**
	 * returns the Scenario with the given name
	 * @param id id of the scanario
	 * @return the Scenario
	 */
	public static Scenario getScanarioById(String id){
		return idToScenario.get(id);
	}

	/**
	 * returns all Scenarios
	 * @return a Collection of all Scenarios
	 */
	public static Collection<Scenario> getAllScenarios(){
		return idToScenario.values();
	}
}
