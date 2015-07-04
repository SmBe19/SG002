package com.smeanox.games.sg002;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.smeanox.games.sg002.screen.ScreenManager;
import com.smeanox.games.sg002.util.Assets;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.util.GameObjectTypeReader;
import com.smeanox.games.sg002.util.Language;
import com.smeanox.games.sg002.util.MapObjectTypeReader;
import com.smeanox.games.sg002.util.ScenarioReader;

/**
 * Main Class of the game
 *
 * @author Benjamin Schmid
 */
public class SG002 extends Game {

	/**
	 * Initializes the game
	 */
	@Override
	public void create() {
		Language.loadStrings();
		Consts.updateScale();
		Assets.loadAssetsSplashScreen();
		GameObjectTypeReader.readGameObjectTypes(Gdx.files.internal("config/GameObjectTypes.xml"));
		MapObjectTypeReader.readMapObjectTypes(Gdx.files.internal("config/MapObjectTypes.xml"));
		ScenarioReader.readScenarios(Gdx.files.internal("config/Scenarios.xml"));

		Gdx.input.setCatchBackKey(true);

		ScreenManager.init(this);
		ScreenManager.showSplash();
	}

	@Override
	public void dispose() {
		ScreenManager.exit();
	}
}
