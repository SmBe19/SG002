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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.smeanox.games.sg002.debug.FPSTracker;
import com.smeanox.games.sg002.player.Player;
import com.smeanox.games.sg002.screen.gui.Button;
import com.smeanox.games.sg002.screen.gui.ClickHandler;
import com.smeanox.games.sg002.util.Assets;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.util.Language;
import com.smeanox.games.sg002.view.GameView;
import com.smeanox.games.sg002.world.Action;
import com.smeanox.games.sg002.world.GameController;
import com.smeanox.games.sg002.world.GameObject;
import com.smeanox.games.sg002.world.GameObjectType;
import com.smeanox.games.sg002.world.actionHandler.NextPlayerHandler;

import com.smeanox.games.sg002.data.Point;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Main game screen. That's where the action happens.
 *
 * @author Benjamin Schmid
 */
public class GameScreen extends AbstractScreen {
	private GameView gameView;
	private GameController gameController;
	private Action aAction;

	private boolean wasTouchDown;
	private boolean wasDrag;
	private Vector2 vector2;
	private Vector3 vector3;
	private HashMap<Integer, Boolean> wasKeyDown;

	private Button moveButton;
	private Button fightButton;
	private Button produceButton;
	private Button cancelButton;
	private Button nextPlayerButton;
	private Button moneyLabel;
	private Button nameLabel;
	private LinkedList<Button> produceButtons;
	private HashMap<GameObjectType, Button> gameObjectTypeToProduceButton;

	/**
	 * Create a new instance
	 *
	 * @param gameController the GameController that handles the game
	 */
	public GameScreen(GameController gameController) {
		super();
		this.gameController = gameController;
		gameView = new GameView(gameController.getGameWorld());
		aAction = new Action();
		aAction.actionType = Action.ActionType.NONE;

		gameController.addNextPlayerHandler(new NextPlayerHandler() {
			@Override
			public void onNextPlayer(Player nextPlayer) {
				updateLabels();
				if(nextPlayer.isShowGUI()) {
					centerCameraOnActivePlayer();
				}
				cancelAction();
				setActionButtonsVisible(false);
			}
		});

		createUI();

		vector2 = new Vector2();
		vector3 = new Vector3();

		moveCamera((gameController.getGameWorld().getMapSizeX() * Consts.fieldSizeX * gameView.getZoom()),
				(gameController.getGameWorld().getMapSizeY() * Consts.fieldSizeY * gameView.getZoom()));

		setActionButtonsVisible(false);
		setProduceButtonsVisible(false);

		initKeys();

		gameController.startGame();
	}

	/**
	 * Create all GUI elements
	 */
	private void createUI() {
		LinkedList<Button> toLayout = new LinkedList<Button>();

		// GUI
		Button b;
		// +
		b = new Button(new Sprite(Assets.button), Assets.liberationMedium, "+", Color.BLACK,
				Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				zoomIn();
			}
		});
		addGUIElement(b);
		toLayout.add(b);
		// -
		b = new Button(new Sprite(Assets.button), Assets.liberationMedium, "-", Color.BLACK,
				Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				zoomOut();
			}
		});
		addGUIElement(b);
		toLayout.add(b);

		layout(toLayout, 2, 1, -1, -1, -0.48f, -0.48f, 40, 40, 10, 0);

		toLayout.clear();
		// money label
		b = new Button(null, Assets.liberationMedium, Language.getStrings().format("gameScreen.currency", 0),
				Color.BLACK, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
		addGUIElement(b);
		toLayout.add(b);
		moneyLabel = b;
		// name label
		b = new Button(null, Assets.liberationMedium, "name",
				Color.BLACK, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
		addGUIElement(b);
		toLayout.add(b);
		nameLabel = b;

		layout(toLayout, 1, 2, 1, -1, 0.48f, -0.48f, 150, 40, 0, 10);

		toLayout.clear();
		// produce
		b = new Button(new Sprite(Assets.button), Assets.liberationSmall,
				Language.getStrings().get("gameScreen.produce"), Color.BLACK, Color.WHITE,
				Color.LIGHT_GRAY, Color.DARK_GRAY);
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				startProduce();
			}
		});
		addGUIElement(b);
		toLayout.add(b);
		produceButton = b;
		// cancel
		b = new Button(new Sprite(Assets.button), Assets.liberationSmall,
				Language.getStrings().get("gameScreen.cancel"), Color.BLACK, Color.WHITE,
				Color.LIGHT_GRAY, Color.DARK_GRAY);
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				cancelAction();
			}
		});
		addGUIElement(b);
		toLayout.add(b);
		cancelButton = b;
		// move
		b = new Button(new Sprite(Assets.button), Assets.liberationSmall,
				Language.getStrings().get("gameScreen.move"), Color.BLACK, Color.WHITE,
				Color.LIGHT_GRAY, Color.DARK_GRAY);
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				startMove();
			}
		});
		addGUIElement(b);
		toLayout.add(b);
		moveButton = b;
		// fight
		b = new Button(new Sprite(Assets.button), Assets.liberationSmall,
				Language.getStrings().get("gameScreen.fight"), Color.BLACK, Color.WHITE,
				Color.LIGHT_GRAY, Color.DARK_GRAY);
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				startFight();
			}
		});
		addGUIElement(b);
		toLayout.add(b);
		fightButton = b;

		layout(toLayout, 2, 2, -1, 1, -0.48f, 0.48f, 150, 40, 10, 5);

		toLayout.clear();
		// nextPlayer
		b = new Button(new Sprite(Assets.button), Assets.liberationSmall,
				Language.getStrings().get("gameScreen.nextPlayer"), Color.BLACK, Color.WHITE,
				Color.LIGHT_GRAY, Color.DARK_GRAY);
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				proposeEndPlaying();
			}
		});
		addGUIElement(b);
		toLayout.add(b);
		nextPlayerButton = b;

		layout(toLayout, 1, 1, 1, 1, 0.48f, 0.48f, 200, 40, 0, 0);

		// produce buttons
		produceButtons = new LinkedList<Button>();
		gameObjectTypeToProduceButton = new HashMap<GameObjectType, Button>();
		int cols, rows;
		cols = (int) Math.round(Math.sqrt(GameObjectType.getAllGameObjectTypes().size() / 2.0));
		rows = (int) Math.ceil((double) GameObjectType.getAllGameObjectTypes().size() / cols);
		for (final GameObjectType gameObjectType : GameObjectType.getAllGameObjectTypesSorted()) {
			b = new Button(new Sprite(Assets.button), Assets.liberationSmall,
					gameObjectType.getName() + " ("
							+ Language.getStrings().format("gameScreen.currency", gameObjectType.getValue()) + ")",
					Color.BLACK, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
			b.addClickHandler(new ClickHandler() {
				@Override
				public void onClick() {
					selectProduceGameObjectType(gameObjectType);
				}
			});
			addGUIElement(b);
			produceButtons.add(b);
			gameObjectTypeToProduceButton.put(gameObjectType, b);
		}

		layout(produceButtons, cols, rows, 0, 0, 0, 0, 250, 40, 5, 5);
	}

	/**
	 * inilialize all keyboard shortcuts
	 */
	private void initKeys() {
		wasKeyDown = new HashMap<Integer, Boolean>();

		for (Integer key : Consts.KeyboardShortcuts.getAllShortcuts()) {
			wasKeyDown.put(key, false);
		}
	}

	@Override
	public void render(float delta) {
		FPSTracker.get().frame(delta);

		boolean wasClick = updateGUI(delta, wasDrag);
		updateInput(delta, wasClick);
		gameController.update(delta);

		clearScreen();
		spriteBatch.begin();
		gameView.render(spriteBatch, gameController.getActivePlayer());
		spriteBatch.end();
		renderGUI(delta);
	}

	/**
	 * Update the Input
	 *
	 * @param delta    The time in seconds since the last render.
	 * @param wasClick true if there was already a click in this frame
	 */
	private void updateInput(float delta, boolean wasClick) {
		if (wasTouchDown && Gdx.input.isTouched()) {
			if (!wasClick) {
				moveCamera(-Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
				if (Math.abs(Gdx.input.getDeltaX() * Gdx.input.getDeltaY()) > 10) {
					wasDrag = true;
				}
			}
		} else {
			if (!wasDrag && wasTouchDown && !Gdx.input.isTouched()) {
				if (!wasClick) {
					if (gameController.getActivePlayer().isShowGUI()) {
						switch (aAction.actionType) {
							case NONE:
								gameView.setActiveByPosition(unproject(Gdx.input.getX(), Gdx.input.getY()));
								selectField(gameView.getActiveX(), gameView.getActiveY());
								break;
							case MOVE:
							case FIGHT:
							case PRODUCE:
								Point point = gameView.getFieldByPosition(unproject(Gdx.input.getX(), Gdx.input.getY()));
								finishAction(point.x, point.y);
								break;
						}
					}
				}
			}
			wasDrag = false;
		}

		updateKeyboardShortcuts();

		wasTouchDown = Gdx.input.isTouched();

		for (Integer key : Consts.KeyboardShortcuts.getAllShortcuts()) {
			wasKeyDown.put(key, Gdx.input.isKeyPressed(key));
		}

		// in the above loop
		if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
			wasKeyDown.put(Consts.KeyboardShortcuts.backKey, true);
		}
	}

	/**
	 * Check whether a keyboard shortcut was pressed
	 */
	private void updateKeyboardShortcuts() {
		if (wasKeyDown.get(Consts.KeyboardShortcuts.quickSave)
				&& !Gdx.input.isKeyPressed(Consts.KeyboardShortcuts.quickSave)) {
			gameController.saveGame(Consts.quickSaveFileName);
		}
		if (wasKeyDown.get(Consts.KeyboardShortcuts.quickLoad)
				&& !Gdx.input.isKeyPressed(Consts.KeyboardShortcuts.quickLoad)) {
			gameController.loadGame(Consts.quickSaveFileName);
		}
		if (wasKeyDown.get(Consts.KeyboardShortcuts.nextPlayer)
				&& !Gdx.input.isKeyPressed(Consts.KeyboardShortcuts.nextPlayer)) {
			proposeEndPlaying();
		}
		if (wasKeyDown.get(Consts.KeyboardShortcuts.produceArcher)
				&& !Gdx.input.isKeyPressed(Consts.KeyboardShortcuts.produceArcher)) {
			startProduce();
			selectProduceGameObjectType(GameObjectType.getGameObjectTypeById("archer"));
		}
		if (wasKeyDown.get(Consts.KeyboardShortcuts.produceGoldMine)
				&& !Gdx.input.isKeyPressed(Consts.KeyboardShortcuts.produceGoldMine)) {
			startProduce();
			selectProduceGameObjectType(GameObjectType.getGameObjectTypeById("goldMine"));
		}
		if (wasKeyDown.get(Consts.KeyboardShortcuts.produceKnight)
				&& !Gdx.input.isKeyPressed(Consts.KeyboardShortcuts.produceKnight)) {
			startProduce();
			selectProduceGameObjectType(GameObjectType.getGameObjectTypeById("knight"));
		}
		if (wasKeyDown.get(Consts.KeyboardShortcuts.produceInfantry)
				&& !Gdx.input.isKeyPressed(Consts.KeyboardShortcuts.produceInfantry)) {
			startProduce();
			selectProduceGameObjectType(GameObjectType.getGameObjectTypeById("infantry"));
		}
		if (wasKeyDown.get(Consts.KeyboardShortcuts.produceTownCenter)
				&& !Gdx.input.isKeyPressed(Consts.KeyboardShortcuts.produceTownCenter)) {
			startProduce();
			selectProduceGameObjectType(GameObjectType.getGameObjectTypeById("townCenter"));
		}
		if (wasKeyDown.get(Consts.KeyboardShortcuts.produceVillager)
				&& !Gdx.input.isKeyPressed(Consts.KeyboardShortcuts.nextPlayer)) {
			startProduce();
			selectProduceGameObjectType(GameObjectType.getGameObjectTypeById("villager"));
		}
		if (wasKeyDown.get(Consts.KeyboardShortcuts.produce)
				&& !Gdx.input.isKeyPressed(Consts.KeyboardShortcuts.nextPlayer)) {
			startProduce();
		}
		if (wasKeyDown.get(Consts.KeyboardShortcuts.move)
				&& !Gdx.input.isKeyPressed(Consts.KeyboardShortcuts.move)) {
			startMove();
		}
		if (wasKeyDown.get(Consts.KeyboardShortcuts.fight)
				&& !Gdx.input.isKeyPressed(Consts.KeyboardShortcuts.fight)) {
			startFight();
		}
		if (wasKeyDown.get(Consts.KeyboardShortcuts.cancel)
				&& !Gdx.input.isKeyPressed(Consts.KeyboardShortcuts.cancel)) {
			cancelAction();
		}

		if (wasKeyDown.get(Consts.KeyboardShortcuts.backKey)
				&& !(Gdx.input.isKeyPressed(Input.Keys.BACK)
				|| Gdx.input.isKeyPressed(Consts.KeyboardShortcuts.backKey))) {
			pauseGame();
		}
	}

	/**
	 * Center the camera on the first GameObject found of the current player
	 */
	private void centerCameraOnActivePlayer() {
		for (int y = 0; y < gameController.getGameWorld().getMapSizeY(); y++) {
			for (int x = 0; x < gameController.getGameWorld().getMapSizeX(); x++) {
				if (gameController.getGameWorld().getWorldGameObject(x, y) != null
						&& gameController.getGameWorld().getWorldGameObject(x, y).getPlayer()
						== gameController.getActivePlayer()) {
					setCameraPosition(gameView.getPositionByField(x, y));
				}
			}
		}
	}

	/**
	 * Update the labels
	 */
	private void updateLabels() {
		moneyLabel.setText(Language.getStrings().format("gameScreen.currency", gameController.getActivePlayer().getMoney()));
		moneyLabel.setTextColor(gameController.getActivePlayer().getColor());

		nameLabel.setText(gameController.getActivePlayer().getName());
		nameLabel.setTextColor(gameController.getActivePlayer().getColor());

		if(gameController.countLivingPlayers() < 2){
			nextPlayerButton.setActive(false);
		}
	}

	/**
	 * Set the visibility of all action buttons to the given value
	 *
	 * @param visible whether the buttons should be visible
	 */
	private void setActionButtonsVisible(boolean visible) {
		moveButton.setVisible(visible);
		fightButton.setVisible(visible);
		produceButton.setVisible(visible);
		cancelButton.setVisible(visible);
	}

	/**
	 * Set the active flag of all action buttons to the given value
	 *
	 * @param active whether the buttons should be active
	 */
	private void setActionButtonsActive(boolean active) {
		moveButton.setActive(active);
		fightButton.setActive(active);
		produceButton.setActive(active);
		cancelButton.setActive(active);
	}

	/**
	 * activate only the buttons the active gameObject supports
	 *
	 * @param activeGameObject the active GameObject
	 */
	private void setActionButtonsActive(GameObject activeGameObject) {
		setActionButtonsActive(false);
		moveButton.setActive(activeGameObject.isCanDoAction(Action.ActionType.MOVE));
		fightButton.setActive(activeGameObject.isCanDoAction(Action.ActionType.FIGHT));
		produceButton.setActive(activeGameObject.isCanDoAction(Action.ActionType.PRODUCE));
		cancelButton.setActive(false);
	}

	/**
	 * Set the visibility of all produce buttons to the given value
	 *
	 * @param visible whether the buttons should be visible
	 */
	private void setProduceButtonsVisible(boolean visible) {
		for (Button b : produceButtons) {
			b.setVisible(visible);
		}
	}

	/**
	 * Set the active flag of all produce buttons to the given value
	 *
	 * @param active whether the buttons should be active
	 */
	private void setProduceButtonsActive(boolean active) {
		for (Button b : produceButtons) {
			b.setActive(active);
		}
	}

	/**
	 * activate only the buttons the active gameObject supports and the player can afford
	 *
	 * @param activeGameObject the active GameObject
	 */
	private void setProduceButtonsActive(GameObject activeGameObject) {
		if (activeGameObject == null) {
			return;
		}
		setProduceButtonsActive(false);
		for (GameObjectType got : activeGameObject.getGameObjectType().getCanProduceList()) {
			if (gameController.getActivePlayer().getMoney() >= got.getValue()) {
				gameObjectTypeToProduceButton.get(got).setActive(true);
			}
		}
	}

	/**
	 * Zoom in the GameView
	 */
	private void zoomIn() {
		vector3.set(camera.position);
		vector3.x *= Consts.zoomStep;
		vector3.y *= Consts.zoomStep;
		setCameraPosition(vector3.x, vector3.y);
		gameView.zoomIn();
	}

	/**
	 * Zoom out the GameView
	 */
	private void zoomOut() {
		vector3.set(camera.position);
		vector3.x /= Consts.zoomStep;
		vector3.y /= Consts.zoomStep;
		setCameraPosition(vector3.x, vector3.y);
		gameView.zoomOut();
	}

	/**
	 * The user clicked the "move" button
	 */
	private void startMove() {
		aAction.actionType = Action.ActionType.MOVE;
		setProduceButtonsVisible(false);
		cancelButton.setActive(true);
	}

	/**
	 * The user clicked the "fight" button
	 */
	private void startFight() {
		aAction.actionType = Action.ActionType.FIGHT;
		setProduceButtonsVisible(false);
		cancelButton.setActive(true);
	}

	/**
	 * The user clicked the "produce" button
	 */
	private void startProduce() {
		if (gameView.getActiveGameObject() == null) {
			return;
		}
		aAction.actionType = Action.ActionType.NONE;
		setProduceButtonsVisible(true);
		cancelButton.setActive(true);
		setProduceButtonsActive(gameView.getActiveGameObject());
	}

	/**
	 * The user clicked the "cancel" button
	 */
	private void cancelAction() {
		setProduceButtonsVisible(false);
		aAction.actionType = Action.ActionType.NONE;
		cancelButton.setActive(false);
	}

	/**
	 * The user finished clicked "next player"
	 */
	private void proposeEndPlaying() {
		if(gameController.countLivingPlayers() < 2){
			return;
		} else if (gameController.getActivePlayer().proposeEndPlaying()) {
			cancelAction();
		}
	}

	/**
	 * The user clicked on a produce button
	 *
	 * @param gameObjectType the GameObjectType of the produce button
	 */
	private void selectProduceGameObjectType(GameObjectType gameObjectType) {
		if (gameView.getActiveGameObject() == null
				|| !gameView.getActiveGameObject().getGameObjectType().getCanProduceList().contains(gameObjectType)) {
			return;
		}
		aAction.actionType = Action.ActionType.PRODUCE;
		aAction.produceGameObjectType = gameObjectType;
		setProduceButtonsVisible(false);
	}

	/**
	 * select the given field in the GameView
	 *
	 * @param x coordinates in world space
	 * @param y coordinates in world space
	 */
	private void selectField(int x, int y) {
		gameView.setActiveX(x);
		gameView.setActiveY(y);
		if (gameView.getActiveGameObject() != null
				&& gameView.getActiveGameObject().getPlayer() == gameController.getActivePlayer()
				&& !gameController.getGameWorld().wasUsed(x, y)) {
			setActionButtonsVisible(true);
			setActionButtonsActive(gameView.getActiveGameObject());
			setProduceButtonsVisible(false);

			aAction.startX = x;
			aAction.startY = y;
		} else {
			setActionButtonsVisible(false);
			setProduceButtonsVisible(false);
			aAction.actionType = Action.ActionType.NONE;
		}
	}

	/**
	 * The user finished an action, the action is proposed to the active player
	 *
	 * @param x coordinates in world space of the end field of the action
	 * @param y coordinates in world space of the end field of the action
	 */
	private void finishAction(int x, int y) {
		aAction.endX = x;
		aAction.endY = y;
		if (gameController.getActivePlayer().proposeAction(aAction)) {
			updateLabels();

			setActionButtonsVisible(false);
			setProduceButtonsVisible(false);
			aAction.actionType = Action.ActionType.NONE;
		}
	}

	/**
	 * Pause the game
	 */
	private void pauseGame() {
		ScreenManager.showPauseMenu(gameController);
	}
}
