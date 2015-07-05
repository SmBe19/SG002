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
import com.smeanox.games.sg002.log.GameLogger;
import com.smeanox.games.sg002.player.AIPlayer_BenNo1;
import com.smeanox.games.sg002.player.ExternalAIPlayer;
import com.smeanox.games.sg002.player.LocalPlayer;
import com.smeanox.games.sg002.player.Player;
import com.smeanox.games.sg002.player.ReplayLoader;
import com.smeanox.games.sg002.screen.gui.Button;
import com.smeanox.games.sg002.screen.gui.ClickHandler;
import com.smeanox.games.sg002.util.Assets;
import com.smeanox.games.sg002.util.ConfigFileUtil;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.util.Language;
import com.smeanox.games.sg002.util.ProgramArguments;
import com.smeanox.games.sg002.world.GameController;
import com.smeanox.games.sg002.world.Scenario;

import java.io.File;
import java.io.FileNotFoundException;
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
	private int processedPlayerCommands;
	private boolean displayNameDialog;
	private boolean displayCommandDialog;
	private LinkedList<String> playerNames;
	private LinkedList<String> playerCommands;

	private Button playerCountLabel;
	private Button scenarioLabel;
	private Button scenarioInfoLabel;

	private Iterator<Scenario> scenarioIterator;

	private boolean wasBackDown;

	private ReplayLoader replayLoader;
	private boolean replayLoaderStarted;

	/**
	 * Create a new instance
	 */
	public MenuScreen() {
		super();

		playerCount = 2;
		displayNameDialog = false;
		displayCommandDialog = false;

		if(ProgramArguments.replay == null) {
			createUI();

			scenarioIterator = Scenario.getAllScenariosSorted().iterator();
			nextScenario();

			if (ProgramArguments.scenario != null) {
				if (!forwardScenario(ProgramArguments.scenario)) {
					System.out.println("scenario not found: " + ProgramArguments.scenario);
					ScreenManager.exit();
				}
			}

			if (ProgramArguments.playerCount >= 2) {
				playerCount = ProgramArguments.playerCount - 1;
				increasePlayerCount();
			}
		} else {
			createGUILoading();
		}
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
	 * Create all GUI elements for loading
	 */
	private void createGUILoading(){
		LinkedList<Button> toLayout = new LinkedList<Button>();
		Button b;

		// loading
		b = new Button(null, Assets.liberationMedium, Language.getStrings().get("menu.loading"),
				Color.BLUE, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
		addGUIElement(b);
		toLayout.add(b);

		// game name
		b = new Button(null, Assets.liberationLarge, Language.getStrings().get("game.name"),
				Color.ORANGE, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
		addGUIElement(b);
		toLayout.add(b);

		layout(toLayout, 1, 2, 0, 0, 0, 0, 300, 60, 0, 30);

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
	 * Forward to the scenario with the given id
	 *
	 * @param id the id
	 * @return true if the scenario was found
	 */
	private boolean forwardScenario(String id) {
		String startId = scenario.getId();
		if (startId.equals(id)) {
			return true;
		}
		do {
			nextScenario();
		} while (!id.equals(scenario.getId()) && !startId.equals(scenario.getId()));

		return !startId.equals(scenario.getId());
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
		if (ProgramArguments.replay != null) {
			startReplay();

			clearScreen();
			updateGUI(delta, false);
			renderGUI(delta);
			return;
		}

		if (ProgramArguments.autoStart) {
			prepareStart();
			startGame();
			return;
		}

		clearScreen();
		updateGUI(delta, false);
		renderGUI(delta);

		if (processedPlayerNames >= playerCount && processedPlayerCommands >= playerCount) {
			startGame();
		} else if (displayNameDialog) {
			Gdx.input.getTextInput(new PlayerInfoListener(processedPlayerNames, playerNames, new PlayerInfoHandler() {
						@Override
						public void infoReceived() {
							processedPlayerNames++;
							displayCommandDialog = true;
						}

						@Override
						public void infoCanceled() {
							displayNameDialog = false;
							displayCommandDialog = false;
							processedPlayerNames = -1;
							processedPlayerCommands = -1;
						}
					}),
					Language.getStrings().get("menu.playerName.dialog"),
					playerNames.get(processedPlayerNames), "");
			displayNameDialog = false;
		} else if (displayCommandDialog) {
			Gdx.input.getTextInput(new PlayerInfoListener(processedPlayerCommands, playerCommands, new PlayerInfoHandler() {
						@Override
						public void infoReceived() {
							processedPlayerCommands++;
							displayNameDialog = true;
						}

						@Override
						public void infoCanceled() {
							displayNameDialog = false;
							displayCommandDialog = false;
							processedPlayerNames = -1;
							processedPlayerCommands = -1;
						}
					}),
					Language.getStrings().get("menu.playerCommand.dialog"),
					playerCommands.get(processedPlayerCommands), "");
			displayCommandDialog = false;
		}

		if (wasBackDown && !(Gdx.input.isKeyPressed(Input.Keys.BACK)
				|| Gdx.input.isKeyPressed(Consts.KeyboardShortcuts.backKey))) {
			ScreenManager.exit();
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

		playerCommands = new LinkedList<String>();
		for (int i = 0; i < playerCount; i++) {
			playerCommands.add(Consts.COMMAND_LOCAL);
		}

		processedPlayerNames = 0;
		processedPlayerCommands = 0;

		if (ProgramArguments.namesFile != null) {
			File namesFile = new File(ProgramArguments.namesFile);
			try {
				LinkedList<String> names = ConfigFileUtil.readAllLines(namesFile);
				for (int i = 0; i < names.size() && i < playerCount; i++) {
					playerNames.set(i, names.get(i));
				}
				if (!ProgramArguments.allowNamesOverride) {
					processedPlayerNames = names.size();
				}
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		if (ProgramArguments.playersFile != null) {
			File playersFile = new File(ProgramArguments.playersFile);
			try {
				LinkedList<String> players = ConfigFileUtil.readAllLines(playersFile);
				for (int i = 0; i < players.size() && i < playerCount; i++) {
					playerCommands.set(i, players.get(i));
				}
				if (!ProgramArguments.allowPlayersOverride) {
					processedPlayerCommands = players.size();
				}
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		displayNameDialog = true;
	}

	/**
	 * Start the game
	 */
	private void startGame() {
		GameLogger logger = new GameLogger();
		logger.setLogFolder(ProgramArguments.logFolder);
		logger.setGameLogPath(ProgramArguments.gameLog);
		logger.setBehaviourLogPath(ProgramArguments.behaviourLog);
		logger.setPrintStdOut(ProgramArguments.printStdOut);
		logger.setPrintStdErr(ProgramArguments.printStdErr);

		GameController gameController = new GameController(scenario, logger);
		for (int i = 0; i < playerCount; i++) {
			Player player;
			if (Consts.COMMAND_LOCAL.equals(playerCommands.get(i))) {
				player = new LocalPlayer();
			} else if (Consts.COMMAND_BENNO1.equals(playerCommands.get(i))) {
				player = new AIPlayer_BenNo1();
			} else {
				player = new ExternalAIPlayer();
				((ExternalAIPlayer) player).setCommand(playerCommands.get(i));
			}
			gameController.addPlayer(player);
			player.setColor(Consts.playerColors[i % Consts.playerColors.length]);
			player.setShowGUI(player instanceof LocalPlayer);
			player.setName(playerNames.get(i));
		}
		ScreenManager.showGame(gameController);
	}

	private void startReplay() {
		if (replayLoaderStarted) {
			if (!replayLoader.isAlive()) {
				GameController gameController = replayLoader.getResult();
				if (gameController == null) {
					System.out.println("Loading of replay failed");
					ScreenManager.exit();
				} else {
					ScreenManager.showGame(gameController);
				}
				replayLoaderStarted = false;
			}
		} else {
			replayLoader = new ReplayLoader();
			replayLoader.start();
			replayLoaderStarted = true;
		}
	}

	/**
	 * Wait for the user to input a player name
	 */
	private class PlayerInfoListener implements Input.TextInputListener {
		private int player;
		private LinkedList<String> list;
		private PlayerInfoHandler handler;

		public PlayerInfoListener(int player, LinkedList<String> list, PlayerInfoHandler handler) {
			this.player = player;
			this.list = list;
			this.handler = handler;
		}

		@Override
		public void input(String text) {
			list.set(player, text);
			handler.infoReceived();
		}

		@Override
		public void canceled() {
			handler.infoCanceled();
		}
	}

	private interface PlayerInfoHandler {
		void infoReceived();

		void infoCanceled();
	}
}
