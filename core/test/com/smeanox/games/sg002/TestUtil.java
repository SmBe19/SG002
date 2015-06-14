package com.smeanox.games.sg002;

import com.badlogic.gdx.files.FileHandle;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.util.GameObjectTypeReader;
import com.smeanox.games.sg002.util.Language;
import com.smeanox.games.sg002.util.ScenarioReader;
import com.smeanox.games.sg002.world.Scenario;

import java.util.Locale;

/**
 * Helper methods for testing
 *
 * @author Benjamin Schmid
 */
public class TestUtil {

	public static void setupConfig() {
		Consts.headlessMode = true;
		Language.loadStringsForHeadless(Locale.getDefault());
		GameObjectTypeReader.readGameObjectTypes(new FileHandle("config/GameObjectTypes.xml"));
		ScenarioReader.readScenarios(new FileHandle("config/Scenarios.xml"));
	}

	public static Scenario createTestScenario() {
		return new Scenario("test", "test", 1000, 8, 15, 20, true, 4, 314159265358979L, 2, true);
	}
}
