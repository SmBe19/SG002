package com.smeanox.games.sg002.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.smeanox.games.sg002.util.Assets;

/**
 * Screen displayed at startup of game while loading assets
 *
 * @author Benjamin Schmid
 */
public class SplashScreen extends AbstractScreen {

	/**
	 * smeanox logo
	 */
	public static Sprite smeanox;

	/**
	 * how long the splash screen is shown in seconds
	 */
	private final float splashDuration = 4f;
	/**
	 * how many seconds already passed
	 */
	private float timePassed;

	/**
	 * Create a new instance
	 */
	public SplashScreen() {
		super();
	}

	@Override
	public void show() {
		super.show();

		Assets.prepareLoadAssets();

		timePassed = 0;

		smeanox = new Sprite(Assets.smeanox);
	}

	@Override
	public void render(float delta) {
		timePassed += delta;

		// if not all assets are loaded we let the logo stay for longer
		if (!Assets.loadAssets()) {
			timePassed = Math.min(timePassed, splashDuration / 2f);
		}

		if (timePassed >= splashDuration) {
			ScreenManager.showMenu();
		}

		clearScreen();

		spriteBatch.begin();

		// calculate the alpha value depending on the passed time
		smeanox.setColor(1, 1, 1, 1 - ((MathUtils.cos((timePassed / splashDuration) * MathUtils.PI2) + 1) / 2f));
		smeanox.draw(spriteBatch);

		spriteBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		scaleSprites();
	}

	/**
	 * Scale all local sprites according to dewScale
	 */
	private void scaleSprites() {
		float scale = (float) Gdx.graphics.getWidth() / Assets.smeanox.getWidth() * 0.8f;
		smeanox.setSize(Assets.smeanox.getWidth() * scale, Assets.smeanox.getHeight() * scale);
		smeanox.setCenter(0, 0);
	}
}
