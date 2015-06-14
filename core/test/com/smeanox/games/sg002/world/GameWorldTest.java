package com.smeanox.games.sg002.world;

import com.smeanox.games.sg002.TestUtil;
import com.smeanox.games.sg002.player.LocalPlayer;
import com.smeanox.games.sg002.player.Player;
import com.smeanox.games.sg002.util.Consts;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test the GameWorld
 *
 * @author Benjamin Schmid
 */
public class GameWorldTest {

	Scenario scenario;
	GameController gameController;
	GameWorld gameWorld;

	@Before
	public void setup(){
		TestUtil.setupConfig();
		scenario = TestUtil.createTestScenario();
		gameController = new GameController(scenario);
		gameWorld = gameController.getGameWorld();
	}

	@Test
	public void testInitScenario() {
		assertEquals(15, gameWorld.getMapSizeX());
		assertEquals(20, gameWorld.getMapSizeY());
	}

	@Test
	public void testAddStartGameObject(){
		ArrayList<Player> players = new ArrayList<Player>();
		for (int i = 0; i < 8; i++) {
			players.add(new LocalPlayer());
			players.get(i).setId(i);
		}

		for(Player player : players){
			gameWorld.addStartGameObjects(player, GameObjectType.getStartGameObjectType());
		}

		assertEquals(players.size(), gameWorld.getGameObjects().size());
		assertEquals(scenario.getStartGameObjectMinDistance(), Consts.startGameObjectMinDistance);

		int minDist = Integer.MAX_VALUE;
		for(GameObject gameObject1 : gameWorld.getGameObjects()){
			for(GameObject gameObject2 : gameWorld.getGameObjects()){
				if(gameObject1 == gameObject2){
					continue;
				}
				int distX, distY, dist;
				distX = Math.abs(gameObject1.getPositionX() - gameObject2.getPositionX());
				distY = Math.abs(gameObject1.getPositionY() - gameObject2.getPositionY());
				dist = Math.max(distX, distY);
				minDist = Math.min(minDist, dist);
			}
		}
		assertTrue(minDist + "/" + scenario.getStartGameObjectMinDistance(), minDist >= scenario.getStartGameObjectMinDistance());
	}

	@Test
	public void testStartRound(){
		ArrayList<Player> players = new ArrayList<Player>();
		for (int i = 0; i < 2; i++) {
			players.add(new LocalPlayer());
		}

		for(Player player : players){
			gameController.addPlayer(player);
		}

		for(Player player : players){
			assertEquals(scenario.getStartMoney(), player.getMoney());
		}

		gameWorld.startRound(players.get(0), true);
		assertEquals(scenario.getStartMoney() + GameObjectType.getStartGameObjectType().getValuePerRound(), players.get(0).getMoney());
	}

	@Test
	public void testGetWorldMap() {
		Player player = new LocalPlayer();

		for (int y = 0; y < gameWorld.getMapSizeY(); y++) {
			for (int x = 0; x < gameWorld.getMapSizeX(); x++) {
				if (y % 2 == 0 || x % 3 == 0) {
					gameWorld.getWorldGameObjects()[y][x] = new GameObject(GameObjectType.getStartGameObjectType(),
							player);
				}
			}
		}

		assertNull(gameWorld.getWorldGameObject(-1, 0));
		assertNull(gameWorld.getWorldGameObject(0, -1));
		assertNull(gameWorld.getWorldGameObject(-1, -1));
		assertNull(gameWorld.getWorldGameObject(15, 0));
		assertNull(gameWorld.getWorldGameObject(0, 20));
		assertNull(gameWorld.getWorldGameObject(15, 20));

		for (int y = 0; y < gameWorld.getMapSizeY(); y++) {
			for (int x = 0; x < gameWorld.getMapSizeX(); x++) {
				if (y % 2 == 0 || x % 3 == 0) {
					assertNotNull(gameWorld.getWorldGameObject(x, y));
				} else {
					assertNull(gameWorld.getWorldGameObject(x, y));
				}
			}
		}
	}
}
