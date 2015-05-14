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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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

	private Button moveButton;
	private Button fightButton;
	private Button produceButton;
	private Button cancelButton;
	private Button nextPlayerButton;
	private Button moneyLabel;
	private Button nameLabel;
	private LinkedList<Button> produceButtons;
	private HashMap<GameObjectType, Button> gameObjectTypeToProduceButton;

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
				centerCameraOnActivePlayer();
			}
		});

		createUI();

		vector2 = new Vector2();
		vector3 = new Vector3();

		moveCamera((gameController.getGameWorld().getMapSizeX() * Consts.fieldSizeX * gameView.getZoom()),
				(gameController.getGameWorld().getMapSizeY() * Consts.fieldSizeY * gameView.getZoom()));

		setActionButtonsVisible(false);
		setProduceButtonsVisible(false);

		gameController.startGame();
	}

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
	 * Called when the screen should render itself.
	 *
	 * @param delta The time in seconds since the last render.
	 */
	@Override
	public void render(float delta) {
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
	 * Updates the Input
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
								vector2 = gameView.getFieldByPosition(unproject(Gdx.input.getX(), Gdx.input.getY()));
								finishAction((int) vector2.x, (int) vector2.y);
								break;
						}
					}
				}
			}
			wasDrag = false;
		}

		wasTouchDown = Gdx.input.isTouched();
	}

	private void centerCameraOnActivePlayer(){
		for(int y = 0; y < gameController.getGameWorld().getMapSizeY(); y++){
			for(int x = 0; x < gameController.getGameWorld().getMapSizeX(); x++){
				if(gameController.getGameWorld().getWorldMap(x, y) != null
						&& gameController.getGameWorld().getWorldMap(x, y).getPlayer()
						== gameController.getActivePlayer()){
					setCameraPosition(gameView.getPositionByField(x, y));
				}
			}
		}
	}

	/**
	 * Updates the labels
	 */
	private void updateLabels() {
		moneyLabel.setText(Language.getStrings().format("gameScreen.currency", gameController.getActivePlayer().getMoney()));
		moneyLabel.setTextColor(gameController.getActivePlayer().getColor());

		nameLabel.setText(gameController.getActivePlayer().getName());
		nameLabel.setTextColor(gameController.getActivePlayer().getColor());
	}

	private void setActionButtonsVisible(boolean visible) {
		moveButton.setVisible(visible);
		fightButton.setVisible(visible);
		produceButton.setVisible(visible);
		cancelButton.setVisible(visible);
	}

	private void setActionButtonsActive(boolean active) {
		moveButton.setActive(active);
		fightButton.setActive(active);
		produceButton.setActive(active);
		cancelButton.setActive(active);
	}

	/**
	 * activates only the buttons the active gameObject supports
	 *
	 * @param activeGameObject the active GameObject
	 */
	private void setActionButtonsActive(GameObject activeGameObject) {
		setActionButtonsActive(false);
		moveButton.setActive(activeGameObject.getGameObjectType().getRadiusWalkMax() > 0);
		fightButton.setActive(activeGameObject.getGameObjectType().isCanFight());
		produceButton.setActive(activeGameObject.getGameObjectType().isCanProduce());
		cancelButton.setActive(false);
	}

	private void setProduceButtonsVisible(boolean visible) {
		for (Button b : produceButtons) {
			b.setVisible(visible);
		}
	}

	private void setProduceButtonsActive(boolean active) {
		for (Button b : produceButtons) {
			b.setActive(active);
		}
	}

	/**
	 * activates only the buttons the active gameObject supports and the player can afford
	 *
	 * @param activeGameObject the active GameObject
	 */
	private void setProduceButtonsActive(GameObject activeGameObject) {
		setProduceButtonsActive(false);
		for (GameObjectType got : activeGameObject.getGameObjectType().getCanProduceList()) {
			if(gameController.getActivePlayer().getMoney() >= got.getValue()) {
				gameObjectTypeToProduceButton.get(got).setActive(true);
			}
		}
	}

	private void zoomIn() {
		vector3.set(camera.position);
		vector3.x *= Consts.zoomStep;
		vector3.y *= Consts.zoomStep;
		setCameraPosition(vector3.x, vector3.y);
		gameView.zoomIn();
	}

	private void zoomOut() {
		vector3.set(camera.position);
		vector3.x /= Consts.zoomStep;
		vector3.y /= Consts.zoomStep;
		setCameraPosition(vector3.x, vector3.y);
		gameView.zoomOut();
	}

	private void startMove() {
		aAction.actionType = Action.ActionType.MOVE;
		setProduceButtonsVisible(false);
		cancelButton.setActive(true);
	}

	private void startFight() {
		aAction.actionType = Action.ActionType.FIGHT;
		setProduceButtonsVisible(false);
		cancelButton.setActive(true);
	}

	private void startProduce() {
		aAction.actionType = Action.ActionType.NONE;
		setProduceButtonsVisible(true);
		cancelButton.setActive(true);
		setProduceButtonsActive(gameView.getActiveGameObject());
	}

	private void cancelAction() {
		setProduceButtonsVisible(false);
		aAction.actionType = Action.ActionType.NONE;
		cancelButton.setActive(false);
	}

	private void proposeEndPlaying() {
		if(gameController.getActivePlayer().proposeEndPlaying()){
			cancelAction();
		}
	}

	private void selectProduceGameObjectType(GameObjectType gameObjectType) {
		aAction.actionType = Action.ActionType.PRODUCE;
		aAction.produceGameObjectType = gameObjectType;
		setProduceButtonsVisible(false);
	}

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
}
