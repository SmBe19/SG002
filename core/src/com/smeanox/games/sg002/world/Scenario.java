package com.smeanox.games.sg002.world;

import java.awt.Point;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

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
	private int startGameObjectMinDistance;
	private Random random;
	private Point[] startPos;
	private Point[] goldPos;

	public Scenario(String id, String name, int startMoney, int maxPlayerCount, int mapSizeX,
					int mapSizeY, boolean walkDiagonal, int startGameObjectMinDistance, int seed,
					int maxGold, boolean multipleActionsPerObject) {
		this.random = new Random(seed);
		Set<Point> usedPos = new HashSet();
		Point[] goldPos = new Point[maxGold];
		Point[] startPos = new Point[maxPlayerCount];

		while(maxGold > 0){
			maxGold --;
			Point pt;
			do{
				pt = new Point(random.nextInt(mapSizeX-1),random.nextInt(mapSizeY-1));
			}while(usedPos.contains(pt));
			usedPos.add(pt);
			goldPos[maxGold] = pt;
		}

		for (int i = 0; i < maxPlayerCount; i++){
			Point pt;
			do{
				pt = new Point(random.nextInt(mapSizeX-1),random.nextInt(mapSizeY-1));
			}while(usedPos.contains(pt));
			usedPos.add(pt);
			startPos[i] = pt;
		}

		this.id = id;
		this.name = name;
		this.startMoney = startMoney;
		this.maxPlayerCount = maxPlayerCount;
		this.mapSizeX = mapSizeX;
		this.mapSizeY = mapSizeY;
		this.walkDiagonal = walkDiagonal;
		this.startGameObjectMinDistance = startGameObjectMinDistance;
		this.goldPos = goldPos;
		this.startPos = startPos;
		this.multipleActionsPerObject = multipleActionsPerObject;

		idToScenario.put(id, this);
	}

	public Scenario(String id, String name, int startMoney, int maxPlayerCount, int mapSizeX,
					int mapSizeY, boolean walkDiagonal, int startGameObjectMinDistance, int seed, Point[] goldPos, Point[] startPos, boolean multipleActionsPerObject) {
		this.id = id;
		this.name = name;
		this.startMoney = startMoney;
		this.maxPlayerCount = maxPlayerCount;
		this.mapSizeX = mapSizeX;
		this.mapSizeY = mapSizeY;
		this.walkDiagonal = walkDiagonal;
		this.multipleActionsPerObject = multipleActionsPerObject;
		this.startGameObjectMinDistance = startGameObjectMinDistance;
		this.random = new Random(seed);
		this.goldPos = goldPos;
		this.startPos = startPos;

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

	public boolean isMultipleActionsPerObject(){
		return multipleActionsPerObject;
	}

	public boolean isWalkDiagonal() {
		return walkDiagonal;
	}

	public int getStartGameObjectMinDistance() {
		return startGameObjectMinDistance;
	}

	/**
	 * Get starting position for each player
	 * @param player the id of the player in the range [0,maxPlayerCount]
	 * @return a point representing x and y coordinate of the starting position
	 */
	public Point getStartPos(int player) {
		assert player < maxPlayerCount && player >= 0;
		return startPos[player];
	}

	/**
	 * Get positions of gold resources on the map
	 * @return an array of points representing x and y coordinates of the gold resources
	 */
	public Point[] getGoldPos() {
		return goldPos.clone();
	}

	/**
	 * returns the Scenario with the given name
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
