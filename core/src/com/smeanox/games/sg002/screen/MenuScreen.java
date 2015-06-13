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
import com.smeanox.games.sg002.player.AIPlayer_BenNo1;
import com.smeanox.games.sg002.player.LocalPlayer;
import com.smeanox.games.sg002.player.Player;
import com.smeanox.games.sg002.screen.gui.Button;
import com.smeanox.games.sg002.screen.gui.ClickHandler;
import com.smeanox.games.sg002.util.Assets;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.util.Language;
import com.smeanox.games.sg002.world.GameController;
import com.smeanox.games.sg002.world.Scenario;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Menu screen
 *
 * @author Benjamin Schmid
 */
public class MenuScreen extends AbstractScreen {

	private Scenario scenario;
	private int playerCount;
	private int processedPlayerNames;
	private boolean displayDialog;
	private LinkedList<String> playerNames;

	private Button playerCountLabel;
	private Button scenarioLabel;
	private Button scenarioInfoLabel;

	private Iterator<Scenario> scenarioIterator;

	private boolean wasBackDown;

	/**
	 * Create a new instance
	 */
	public MenuScreen() {
		super();

		playerCount = 2;
		displayDialog = false;

		createUI();

		scenarioIterator = Scenario.getAllScenariosSorted().iterator();
		nextScenario();
	}

	/**
	 * Create all GUI elements
	 */
	private void createUI() {
		LinkedList<Button> toLayout = new LinkedList<Button>();
		Button b;

		// playerCountLabel
		b = new Button(new Sprite(Assets.button), Assets.liberationMedium, "playerCount",
				Color.BLACK, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				increasePlayerCount();
			}
		});
		addGUIElement(b);
		playerCountLabel = b;
		toLayout.add(b);
		// scenarioInfLabel
		b = new Button(null, Assets.liberationSmall, "scenarioInfo",
				Color.WHITE, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
		addGUIElement(b);
		scenarioInfoLabel = b;
		toLayout.add(b);
		// scenarioLabel
		b = new Button(new Sprite(Assets.button), Assets.liberationMedium, "scenarioName",
				Color.BLACK, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				nextScenario();
			}
		});
		addGUIElement(b);
		scenarioLabel = b;
		toLayout.add(b);
		// start game
		b = new Button(new Sprite(Assets.button), Assets.liberationMedium,
				Language.getStrings().get("menu.startGame"), Color.BLACK, Color.WHITE, Color.LIGHT_GRAY,
				Color.DARK_GRAY);
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				prepareStart();
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
	 * cycle through scenarios
	 */
	private void nextScenario() {
		if (!scenarioIterator.hasNext()) {
			scenarioIterator = Scenario.getAllScenariosSorted().iterator();
		}
		scenario = scenarioIterator.next();
		playerCount = Math.min(playerCount, scenario.getMaxPlayerCount());

		setScenarioLabelText();
		setPlayerCountText();
	}

	/**
	 * increase the playerCount and reset it if its too high
	 */
	private void increasePlayerCount() {
		playerCount++;
		if (playerCount > scenario.getMaxPlayerCount()) {
			playerCount = 2;
		}

		setPlayerCountText();
	}

	/**
	 * Set the text on the scenarioLabel to the active scenario's name
	 */
	private void setScenarioLabelText() {
		scenarioLabel.setText(scenario.getName());
		scenarioInfoLabel.setText(Language.getStrings().format("menu.scenarioInfo",
				scenario.getMapSizeX(), scenario.getMapSizeY(), scenario.getStartMoney(), scenario.getMaxPlayerCount()));
	}

	private void setPlayerCountText() {
		playerCountLabel.setText(Language.getStrings().format("menu.playerCount", playerCount, scenario.getMaxPlayerCount()));
	}

	/**
	 * load a saved game
	 */
	private void resumeGame() {
	}

	@Override
	public void render(float delta) {
		clearScreen();
		updateGUI(delta, false);
		renderGUI(delta);

		if (processedPlayerNames >= playerCount) {
			startGame();
		} else if (displayDialog) {
			Gdx.input.getTextInput(new PlayerNameListener(processedPlayerNames),
					Language.getStrings().get("menu.playerName.dialog"),
					playerNames.get(processedPlayerNames), "");
			displayDialog = false;
		}

		if (wasBackDown && !(Gdx.input.isKeyPressed(Input.Keys.BACK)
				|| Gdx.input.isKeyPressed(Consts.KeyboardShortcuts.backKey))) {
			Gdx.app.exit();
		}

		wasBackDown = Gdx.input.isKeyPressed(Input.Keys.BACK)
				|| Gdx.input.isKeyPressed(Consts.KeyboardShortcuts.backKey);
	}

	/**
	 * Prepare to start the game (e.g. ask for player names)
	 */
	private void prepareStart() {
		playerNames = new LinkedList<String>();
		for (int i = 0; i < playerCount; i++) {
			playerNames.add(Language.getStrings().format("menu.playerName.default", (i + 1)));
		}
		displayDialog = true;
		processedPlayerNames = 0;
	}

	/**
	 * Start the game
	 */
	private void startGame() {
		GameController gameController = new GameController(scenario);
		for (int i = 0; i < playerCount; i++) {
			Player player;
			// FIXME make nicer
			if ("BenNo1".equals(playerNames.get(i))) {
				player = new AIPlayer_BenNo1();
			} else {
				player = new LocalPlayer();
			}
			// FIXME Remove me
			if (i > 0) {
				//player = new AIPlayer_BenNo1();
			}
			gameController.addPlayer(player);
			player.setColor(Consts.playerColors[i % Consts.playerColors.length]);
			player.setShowGUI(player instanceof LocalPlayer);
			player.setName(playerNames.get(i));
		}
		ScreenManager.showGame(gameController);
	}

	/**
	 * Wait for the user to input a player name
	 */
	private class PlayerNameListener implements Input.TextInputListener {
		private int player;

		public PlayerNameListener(int player) {
			this.player = player;
		}

		@Override
		public void input(String text) {
			playerNames.set(player, text);
			processedPlayerNames++;
			displayDialog = true;
		}

		@Override
		public void canceled() {
			processedPlayerNames = -1;
			displayDialog = false;
		}
	}
}
