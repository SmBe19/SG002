/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Benjamin Schmid
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.smeanox.games.sg002.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.smeanox.games.sg002.screen.gui.Button;
import com.smeanox.games.sg002.screen.gui.ClickHandler;
import com.smeanox.games.sg002.screen.gui.Resizer;
import com.smeanox.games.sg002.util.Assets;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.view.GameView;
import com.smeanox.games.sg002.world.GameController;

/**
 * Main game screen. That's where the action happens.
 * @author Benjamin Schmid
 */
public class GameScreen extends AbstractScreen {
	private GameView gameView;
	private GameController gameController;

	private boolean wasTouchDown;
	private boolean wasDrag;
	private Vector2 vector2;
	private Vector3 vector3;

	public GameScreen(GameController gameController){
		super();
		this.gameController = gameController;
		gameView = new GameView(gameController.getGameWorld());

		// GUI
		Button b;
		// +
		b = new Button(new Sprite(Assets.button), Assets.liberationMedium, "+", Color.BLACK);
		b.setResizer(new Resizer() {
			@Override
			public Rectangle getNewSize(float width, float height) {
				return new Rectangle(-width / 2 + 10 * Consts.devScaleX, -height / 2 + 10 * Consts.devScaleY, 40 * Consts.devScaleX, 40 * Consts.devScaleY);
			}
		});
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				zoomIn();
			}
		});
		addGUIElement(b);
		// -
		b = new Button(new Sprite(Assets.button), Assets.liberationMedium, "-", Color.BLACK);
		b.setResizer(new Resizer() {
			@Override
			public Rectangle getNewSize(float width, float height) {
				return new Rectangle(-width/2 + 60 * Consts.devScaleX, -height/2 + 10 * Consts.devScaleY, 40 * Consts.devScaleX, 40 * Consts.devScaleY);
			}
		});
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				zoomOut();
			}
		});
		addGUIElement(b);

		vector2 = new Vector2();
		vector3 = new Vector3();

		gameController.startGame();
	}

	/**
	 * Called when the screen should render itself.
	 * @param delta The time in seconds since the last render.
	 */
	@Override
	public void render(float delta) {
		updateGUI(delta);
		updateInput(delta);
		gameController.update(delta);

		clearScreen();
		spriteBatch.begin();
		gameView.render(spriteBatch);
		spriteBatch.end();
		renderGUI(delta);
	}

	/**
	 * Updates the Input
	 * @param delta The time in seconds since the last render.
	 */
	private void updateInput(float delta){
		if(wasTouchDown && Gdx.input.isTouched()) {
			moveCamera(-Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
			if(Math.abs(Gdx.input.getDeltaX() * Gdx.input.getDeltaY()) > 10){
				wasDrag = true;
			}
		} else {
			if(!wasDrag && wasTouchDown && !Gdx.input.isTouched()){
				gameView.setActiveByPosition(unproject(Gdx.input.getX(), Gdx.input.getY()));
			}
			wasDrag = false;
		}

		wasTouchDown = Gdx.input.isTouched();
	}

	private void zoomIn(){
		vector3.set(camera.position);
		vector3.x *= Consts.zoomStep;
		vector3.y *= Consts.zoomStep;
		setCameraPosition(vector3.x, vector3.y);
		gameView.zoomIn();
	}

	private void zoomOut(){
		vector3.set(camera.position);
		vector3.x /= Consts.zoomStep;
		vector3.y /= Consts.zoomStep;
		setCameraPosition(vector3.x, vector3.y);
		gameView.zoomOut();
	}
}
