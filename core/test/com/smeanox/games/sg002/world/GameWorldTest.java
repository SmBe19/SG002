package com.smeanox.games.sg002.world;

import com.smeanox.games.sg002.TestUtil;
import com.smeanox.games.sg002.player.LocalPlayer;
import com.smeanox.games.sg002.player.Player;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test the GameWorld
 *
 * @author Benjamin Schmid
 */
public class GameWorldTest {

	@Test
	public void testInitScenario() {
		TestUtil.setupConfig();
		Scenario scenario = TestUtil.createTestScenario();
		GameWorld gameWorld = new GameWorld(scenario);

		assertEquals(15, gameWorld.getMapSizeX());
		assertEquals(20, gameWorld.getMapSizeY());
	}

	@Test
	public void testGetWorldMap() {
		TestUtil.setupConfig();
		Scenario scenario = TestUtil.createTestScenario();
		GameWorld gameWorld = new GameWorld(scenario);
		Player player = new LocalPlayer();

		for (int y = 0; y < gameWorld.getMapSizeY(); y++) {
			for (int x = 0; x < gameWorld.getMapSizeX(); x++) {
				if (y % 2 == 0 || x % 3 == 0) {
					gameWorld.getWorldMap()[y][x] = new GameObject(GameObjectType.getStartGameObjectType(),
							player);
				}
			}
		}

		assertNull(gameWorld.getWorldMap(-1, 0));
		assertNull(gameWorld.getWorldMap(0, -1));
		assertNull(gameWorld.getWorldMap(-1, -1));
		assertNull(gameWorld.getWorldMap(15, 0));
		assertNull(gameWorld.getWorldMap(0, 20));
		assertNull(gameWorld.getWorldMap(15, 20));

		for (int y = 0; y < gameWorld.getMapSizeY(); y++) {
			for (int x = 0; x < gameWorld.getMapSizeX(); x++) {
				if (y % 2 == 0 || x % 3 == 0) {
					assertNotNull(gameWorld.getWorldMap(x, y));
				} else {
					assertNull(gameWorld.getWorldMap(x, y));
				}
			}
		}
	}
}
