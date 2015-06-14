package com.smeanox.games.sg002.world;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Describes a Scenario
 *
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
	private boolean multipleActionsPerObject;
	private boolean goldMountains;
	private int goldMountainCount;
	private int startGameObjectMinDistance;
	private long seed;

	public Scenario(String id,
					String name,
					int startMoney,
					int maxPlayerCount,
					int mapSizeX,
					int mapSizeY,
					boolean walkDiagonal,
					boolean multipleActionsPerObject,
					boolean goldMountains,
					int goldMountainCount,
					int startGameObjectMinDistance,
					long seed) {
		this.id = id;
		this.name = name;
		this.startMoney = startMoney;
		this.maxPlayerCount = maxPlayerCount;
		this.mapSizeX = mapSizeX;
		this.mapSizeY = mapSizeY;
		this.walkDiagonal = walkDiagonal;
		this.multipleActionsPerObject = multipleActionsPerObject;
		this.goldMountains = goldMountains;
		this.goldMountainCount = goldMountainCount;
		this.startGameObjectMinDistance = startGameObjectMinDistance;
		this.seed = seed;

		idToScenario.put(id, this);
	}

	/**
	 * ID of the scenario
	 *
	 * @return ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * Name of the scenario
	 *
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The amount of money each player starts with
	 *
	 * @return amount of money
	 */
	public int getStartMoney() {
		return startMoney;
	}

	/**
	 * Maxmimal number of players that can participate
	 *
	 * @return number of players
	 */
	public int getMaxPlayerCount() {
		return maxPlayerCount;
	}

	/**
	 * Size of the map
	 *
	 * @return size
	 */
	public int getMapSizeX() {
		return mapSizeX;
	}

	/**
	 * Size of the map
	 *
	 * @return size
	 */
	public int getMapSizeY() {
		return mapSizeY;
	}

	/**
	 * Whether the radius is measured in diffX + diffY (true) or max(diffX, diffY) (false)
	 *
	 * @return radius measurement
	 */
	public boolean isWalkDiagonal() {
		return walkDiagonal;
	}

	/**
	 * Whether an object can perform one action per round (false) or one action per type per round (true)
	 *
	 * @return mode
	 */
	public boolean isMultipleActionsPerObject() {
		return multipleActionsPerObject;
	}

	/**
	 * Whether goldMines can only be built at specific places (true) or everywhere (false)
	 *
	 * @return mode
	 */
	public boolean isGoldMountains() {
		return goldMountains;
	}

	/**
	 * The number of goldMountains that are available on the map
	 *
	 * @return number of goldMountains
	 */
	public int getGoldMountainCount() {
		return goldMountainCount;
	}

	/**
	 * The minimal distance between two start objects
	 *
	 * @return distance
	 */
	public int getStartGameObjectMinDistance() {
		return startGameObjectMinDistance;
	}

	/**
	 * The seed used for randomness
	 *
	 * @return seed
	 */
	public long getSeed() {
		return seed;
	}

	/**
	 * return the Scenario with the given name
	 *
	 * @param id id of the scanario
	 * @return the Scenario
	 */
	public static Scenario getScanarioById(String id) {
		return idToScenario.get(id);
	}

	/**
	 * return all Scenarios (not sorted)
	 *
	 * @return a List of all Scenarios
	 */
	public static Collection<Scenario> getAllScenarios() {
		return idToScenario.values();
	}

	/**
	 * return all Scenarios sorted
	 *
	 * @return a sorted List of all Scenarios
	 */
	public static LinkedList<Scenario> getAllScenariosSorted() {
		LinkedList<Scenario> scenarios = new LinkedList<Scenario>(idToScenario.values());
		Collections.sort(scenarios, new Comparator<Scenario>() {
			@Override
			public int compare(Scenario o1, Scenario o2) {
				if (o1.getMaxPlayerCount() != o2.getMaxPlayerCount()) {
					return o1.getMaxPlayerCount() - o2.getMaxPlayerCount();
				}
				if (o1.getStartMoney() != o2.getStartMoney()) {
					return o1.getStartMoney() - o2.getStartMoney();
				}
				return o1.getName().compareTo(o2.getName());
			}
		});
		return scenarios;
	}
}
