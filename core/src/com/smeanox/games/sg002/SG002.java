package com.smeanox.games.sg002;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.smeanox.games.sg002.screen.ScreenManager;
import com.smeanox.games.sg002.util.Assets;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.util.Language;

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

		ScreenManager.init(this);
		ScreenManager.showSplash();
	}
}
