package com.smeanox.games.sg002.world;

import com.badlogic.gdx.math.MathUtils;

import com.smeanox.games.sg002.data.Point;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
	private int maxGold;
	private long seed;
	private Point[] startPos;
	private Point[] goldPos;

	public Scenario(String id,
					String name,
					int startMoney,
					int maxPlayerCount,
					int mapSizeX,
					int mapSizeY,
					boolean walkDiagonal,
					int startGameObjectMinDistance,
					long seed,
					int maxGold,
					boolean multipleActionsPerObject) {
		this.id = id;
		this.name = name;
		this.startMoney = startMoney;
		this.maxPlayerCount = maxPlayerCount;
		this.mapSizeX = mapSizeX;
		this.mapSizeY = mapSizeY;
		this.walkDiagonal = walkDiagonal;
		this.startGameObjectMinDistance = startGameObjectMinDistance;
		this.maxGold = maxGold;
		this.multipleActionsPerObject = multipleActionsPerObject;
		this.seed = seed;
		MathUtils.random.setSeed(seed);

		idToScenario.put(id, this);

		Set<Point> usedPos = new HashSet<Point>();
		goldPos = new Point[maxGold];
		startPos = new Point[maxPlayerCount];

		for (int i = 0; i < maxPlayerCount; i++) {
			Point pt;
			do {
				pt = new Point(MathUtils.random(mapSizeX - 1), MathUtils.random(mapSizeY - 1));
			} while (!checkMinDist(usedPos, pt, startGameObjectMinDistance));
			usedPos.add(pt);
			startPos[i] = pt;
		}

		for (int i = 0; i < maxGold; i++) {
			Point pt;
			do {
				pt = new Point(MathUtils.random(mapSizeX - 1), MathUtils.random(mapSizeY - 1));
			} while (usedPos.contains(pt));
			usedPos.add(pt);
			goldPos[i] = pt;
		}
	}

	public Scenario(String id,
					String name,
					int startMoney,
					int maxPlayerCount,
					int mapSizeX,
					int mapSizeY,
					boolean walkDiagonal,
					int startGameObjectMinDistance,
					long seed,
					int maxGold,
					boolean multipleActionsPerObject,
					Point[] goldPos,
					Point[] startPos) {
		this(id, name, startMoney, maxPlayerCount, mapSizeX, mapSizeY, walkDiagonal, startGameObjectMinDistance,
				seed, maxGold, multipleActionsPerObject);
		this.goldPos = goldPos;
		this.startPos = startPos;
	}

	/**
	 * Check whether the minimal distance is obeyed
	 * @param usedPoints the points that are already set
	 * @param newPoint the new point that might get added
	 * @param minDist the minimal distance to obey
	 * @return true if it is possible to add the newPoint
	 */
	private boolean checkMinDist(Set<Point> usedPoints, Point newPoint, int minDist){
		if(usedPoints.contains(newPoint)){
			return false;
		}

		for(Point point : usedPoints){
			int distX, distY;
			distX = Math.abs(point.x - newPoint.x);
			distY = Math.abs(point.y - newPoint.y);
			if(Math.max(distX, distY) < minDist){
				return false;
			}
		}

		return true;
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
	 * Maximal number of players that can participate
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
	 * Whether an object can perform one action per round (false) or one action per type per round (true)
	 *
	 * @return mode
	 */
	public boolean isMultipleActionsPerObject() {
		return multipleActionsPerObject;
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
	 * Get starting position for each player
	 *
	 * @param player the id of the player in the range [0,maxPlayerCount]
	 * @return a point representing x and y coordinate of the starting position
	 */
	public Point getStartPos(int player) {
		if (player >= maxPlayerCount || player < 0) {
			throw new IllegalArgumentException("" + player);
		}
		return startPos[player];
	}

	/**
	 * Get positions of gold resources on the map
	 *
	 * @return an array of points representing x and y coordinates of the gold resources
	 */
	public Point[] getGoldPos() {
		return goldPos.clone();
	}

	/**
	 * returns the Scenario with the given name
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
