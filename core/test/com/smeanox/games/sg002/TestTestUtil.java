package com.smeanox.games.sg002;

import com.smeanox.games.sg002.util.Language;
import com.smeanox.games.sg002.world.GameObjectType;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test the TestUtil
 * @author Benjamin Schmid
 */
public class TestTestUtil {

	@Test
	public void testSetupConfig(){
		TestUtil.setupConfig();
		assertNotNull(Language.getStrings());
		assertNotNull(GameObjectType.getStartGameObjectType());
		assertNotNull(GameObjectType.getAllGameObjectTypes());
		assertTrue(GameObjectType.getAllGameObjectTypesSorted().size() > 0);
	}
}
