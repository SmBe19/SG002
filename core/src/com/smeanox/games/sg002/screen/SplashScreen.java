package com.smeanox.games.sg002.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.smeanox.games.sg002.util.Assets;
import com.smeanox.games.sg002.util.Consts;

/**
 * Screen displayed at startup of game while loading assets
 * @author Benjamin Schmid
 */
public class SplashScreen implements Screen {

	/** smeanox logo */
	public static Sprite smeanox;

	/** how long the splash screen is shown in seconds */
	private final float splashDuration = 4f;
	/** how many seconds already passed */
	private float timePassed;

	/** sprite batch */
	private SpriteBatch spriteBatch;

	/** Constructor */
	public SplashScreen(){
		spriteBatch = new SpriteBatch();
	}

	/**
	 * Called when this screen becomes the current screen for a Game.
	 */
	@Override
	public void show() {
		Assets.loadAssets();

		timePassed = 0;

		smeanox = new Sprite(Assets.smeanox);
	}

	/**
	 * Called when the screen should render itself.
	 *
	 * @param delta The time in seconds since the last render.
	 */
	@Override
	public void render(float delta) {
		timePassed += delta;

		// if not all assets are loaded we let the logo stay for longer
		if(!Assets.loadAssets()){
			timePassed = Math.min(timePassed, splashDuration / 2f);
		}

		Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();

		// calculate the alpha value depending on the passed time
		smeanox.setColor(1, 1, 1, 1 - ((MathUtils.cos((timePassed / splashDuration) * MathUtils.PI2) + 1) / 2f));
		smeanox.draw(spriteBatch);

		spriteBatch.end();
	}

	/**
	 * Called when the Application is resized.
	 * @param width
	 * @param height
	 */
	@Override
	public void resize(int width, int height) {
		Consts.updateScale();
		scaleSprites();
	}

	/**
	 * Scales all local sprites according to dewScale
	 */
	private void scaleSprites(){
		float scale = (float)Gdx.graphics.getWidth() / Assets.smeanox.getWidth() * 0.8f;
		smeanox.setSize(Assets.smeanox.getWidth() * scale, Assets.smeanox.getHeight() * scale);
		smeanox.setCenter(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
	}

	/**
	 * Called when the Application is paused, usually when it's not active or visible on screen.
	 */
	@Override
	public void pause() {

	}

	/**
	 * Called when the Application is resumed from a paused state, usually when it regains focus.
	 */
	@Override
	public void resume() {

	}

	/**
	 * Called when this screen is no longer the current screen for a Game.
	 */
	@Override
	public void hide() {

	}

	/**
	 * Called when this screen should release all resources.
	 */
	@Override
	public void dispose() {

	}
}
