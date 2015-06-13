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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.smeanox.games.sg002.screen.gui.Button;
import com.smeanox.games.sg002.screen.gui.ClickHandler;
import com.smeanox.games.sg002.util.Assets;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.util.Language;
import com.smeanox.games.sg002.world.GameController;

import java.util.LinkedList;

/**
 * Pause screen
 *
 * @author Benjamin Schmid
 */
public class PauseScreen extends AbstractScreen {

	private GameController gameController;

	private boolean wasBackDown;

	/**
	 * Create a new instance
	 */
	public PauseScreen() {
		super();

		createUI();
	}

	/**
	 * Create all GUI elements
	 */
	private void createUI() {
		LinkedList<Button> toLayout = new LinkedList<Button>();
		Button b;

		// quit game
		b = new Button(new Sprite(Assets.button), Assets.liberationMedium,
				Language.getStrings().get("menu.quitGame"), Color.BLACK, Color.WHITE, Color.LIGHT_GRAY,
				Color.DARK_GRAY);
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				quitGame();
			}
		});
		addGUIElement(b);
		toLayout.add(b);
		// save game
		b = new Button(new Sprite(Assets.button), Assets.liberationMedium,
				Language.getStrings().get("menu.saveGame"), Color.BLACK, Color.WHITE, Color.LIGHT_GRAY,
				Color.DARK_GRAY);
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				saveGame();
			}
		});
		addGUIElement(b);
		toLayout.add(b);
		// load game
		b = new Button(new Sprite(Assets.button), Assets.liberationMedium,
				Language.getStrings().get("menu.loadGame"), Color.BLACK, Color.WHITE, Color.LIGHT_GRAY,
				Color.DARK_GRAY);
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				loadGame();
			}
		});
		addGUIElement(b);
		toLayout.add(b);
		// resume game
		b = new Button(new Sprite(Assets.button), Assets.liberationMedium,
				Language.getStrings().get("menu.resumeGame"), Color.BLACK, Color.WHITE, Color.LIGHT_GRAY,
				Color.DARK_GRAY);
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				resumeGame();
			}
		});
		addGUIElement(b);
		toLayout.add(b);
		// game name
		b = new Button(null, Assets.liberationLarge, Language.getStrings().get("game.name"),
				Color.ORANGE, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
		addGUIElement(b);
		toLayout.add(b);

		layout(toLayout, 1, 5, 0, 0, 0, 0, 300, 60, 0, 30);

		toLayout.clear();
	}

	/**
	 * resume the game
	 */
	private void resumeGame() {
		ScreenManager.showGame();
	}

	/**
	 * save the game (quicksave)
	 */
	private void saveGame() {
		gameController.saveGame(Consts.quickSaveFileName);
		resumeGame();
	}

	/**
	 * load the game (quicksave)
	 */
	private void loadGame() {
		gameController.loadGame(Consts.quickSaveFileName);
		resumeGame();
	}

	/**
	 * Quit the game
	 */
	private void quitGame() {
		Gdx.app.exit();
	}

	public void setGameController(GameController gameController) {
		this.gameController = gameController;
	}

	@Override
	public void render(float delta) {
		clearScreen();
		updateGUI(delta, false);
		renderGUI(delta);

		if (wasBackDown && !(Gdx.input.isKeyPressed(Input.Keys.BACK)
				|| Gdx.input.isKeyPressed(Consts.KeyboardShortcuts.backKey))) {
			resumeGame();
		}

		wasBackDown = Gdx.input.isKeyPressed(Input.Keys.BACK)
				|| Gdx.input.isKeyPressed(Consts.KeyboardShortcuts.backKey);
	}
}
