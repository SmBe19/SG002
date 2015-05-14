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
import com.smeanox.games.sg002.player.Player;
import com.smeanox.games.sg002.screen.gui.Button;
import com.smeanox.games.sg002.screen.gui.ClickHandler;
import com.smeanox.games.sg002.screen.gui.Resizer;
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
	private LinkedList<Button> produceButtons;
	private HashMap<GameObjectType, Button> gameObjectTypeToProduceButton;

	public GameScreen(GameController gameController){
		super();
		this.gameController = gameController;
		gameView = new GameView(gameController.getGameWorld());
		aAction = new Action();
		aAction.actionType = Action.ActionType.NONE;

		gameController.addNextPlayerHandler(new NextPlayerHandler() {
			@Override
			public void onNextPlayer(Player nextPlayer) {
				updateMoney();
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
		// GUI
		Button b;
		// +
		b = new Button(new Sprite(Assets.button), Assets.liberationMedium, "+", Color.BLACK,
				Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
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
		b = new Button(new Sprite(Assets.button), Assets.liberationMedium, "-", Color.BLACK,
				Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
		b.setResizer(new Resizer() {
			@Override
			public Rectangle getNewSize(float width, float height) {
				return new Rectangle(-width / 2 + 60 * Consts.devScaleX, -height / 2 + 10 * Consts.devScaleY, 40 * Consts.devScaleX, 40 * Consts.devScaleY);
			}
		});
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				zoomOut();
			}
		});
		addGUIElement(b);

		// money
		b = new Button(null, Assets.liberationMedium, Language.getStrings().format("gameScreen.currency", 0),
				Color.BLACK, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
		b.setResizer(new Resizer() {
			@Override
			public Rectangle getNewSize(float width, float height) {
				return new Rectangle(width/2 - 200*Consts.devScaleX, -height/2 + 20*Consts.devScaleY, 150*Consts.devScaleX, 40*Consts.devScaleY);
			}
		});
		addGUIElement(b);
		moneyLabel = b;

		// move
		b = new Button(new Sprite(Assets.button), Assets.liberationSmall,
				Language.getStrings().get("gameScreen.move"), Color.BLACK, Color.WHITE,
				Color.LIGHT_GRAY, Color.DARK_GRAY);
		b.setResizer(new Resizer() {
			@Override
			public Rectangle getNewSize(float width, float height) {
				return new Rectangle(-width / 2 + 20 * Consts.devScaleX, height / 2 - 50 * Consts.devScaleY, 150 * Consts.devScaleX, 40 * Consts.devScaleY);
			}
		});
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				startMove();
			}
		});
		addGUIElement(b);
		moveButton = b;
		// fight
		b = new Button(new Sprite(Assets.button), Assets.liberationSmall,
				Language.getStrings().get("gameScreen.fight"), Color.BLACK, Color.WHITE,
				Color.LIGHT_GRAY, Color.DARK_GRAY);
		b.setResizer(new Resizer() {
			@Override
			public Rectangle getNewSize(float width, float height) {
				return new Rectangle(-width / 2 + 190 * Consts.devScaleX, height / 2 - 50 * Consts.devScaleY, 150 * Consts.devScaleX, 40 * Consts.devScaleY);
			}
		});
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				startFight();
			}
		});
		addGUIElement(b);
		fightButton = b;
		// produce
		b = new Button(new Sprite(Assets.button), Assets.liberationSmall,
				Language.getStrings().get("gameScreen.produce"), Color.BLACK, Color.WHITE,
				Color.LIGHT_GRAY, Color.DARK_GRAY);
		b.setResizer(new Resizer() {
			@Override
			public Rectangle getNewSize(float width, float height) {
				return new Rectangle(-width / 2 + 20 * Consts.devScaleX, height / 2 - 100 * Consts.devScaleY, 150 * Consts.devScaleX, 40 * Consts.devScaleY);
			}
		});
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				startProduce();
			}
		});
		addGUIElement(b);
		produceButton = b;
		// cancel
		b = new Button(new Sprite(Assets.button), Assets.liberationSmall,
				Language.getStrings().get("gameScreen.cancel"), Color.BLACK, Color.WHITE,
				Color.LIGHT_GRAY, Color.DARK_GRAY);
		b.setResizer(new Resizer() {
			@Override
			public Rectangle getNewSize(float width, float height) {
				return new Rectangle(-width / 2 + 190 * Consts.devScaleX, height / 2 - 100 * Consts.devScaleY, 150 * Consts.devScaleX, 40 * Consts.devScaleY);
			}
		});
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				cancelAction();
			}
		});
		addGUIElement(b);
		cancelButton = b;
		// nextPlayer
		b = new Button(new Sprite(Assets.button), Assets.liberationSmall,
				Language.getStrings().get("gameScreen.nextPlayer"), Color.BLACK, Color.WHITE,
				Color.LIGHT_GRAY, Color.DARK_GRAY);
		b.setResizer(new Resizer() {
			@Override
			public Rectangle getNewSize(float width, float height) {
				return new Rectangle(width / 2 - 220 * Consts.devScaleX, height / 2 - 50 * Consts.devScaleY, 200 * Consts.devScaleX, 40 * Consts.devScaleY);
			}
		});
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				proposeEndPlaying();
			}
		});
		addGUIElement(b);
		nextPlayerButton = b;

		// produce buttons
		produceButtons = new LinkedList<Button>();
		gameObjectTypeToProduceButton = new HashMap<GameObjectType, Button>();
		int aNum;
		final int cols, rows;
		aNum = 0;
		cols = (int)Math.round(Math.sqrt(GameObjectType.getAllGameObjectTypes().size()/2.0));
		rows = (int)Math.ceil((double)GameObjectType.getAllGameObjectTypes().size()/cols);
		for(final GameObjectType gameObjectType : GameObjectType.getAllGameObjectTypes()){
			final int aNumFinal = aNum;
			b = new Button(new Sprite(Assets.button), Assets.liberationSmall,
					gameObjectType.getName() + " ("
							+ Language.getStrings().format("gameScreen.currency", gameObjectType.getValue()) + ")",
					Color.BLACK, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
			b.setResizer(new Resizer() {
				@Override
				public Rectangle getNewSize(float width, float height) {
					int aCol, aRow;
					aCol = aNumFinal % cols;
					aRow = aNumFinal / cols;
					return new Rectangle(-(cols*255*Consts.devScaleX / 2) + aCol * 255*Consts.devScaleX,
							-(rows*40*Consts.devScaleY / 2) + aRow*40*Consts.devScaleY,
							250*Consts.devScaleX, 40*Consts.devScaleY);
				}
			});
			b.addClickHandler(new ClickHandler() {
				@Override
				public void onClick() {
					selectProduceGameObjectType(gameObjectType);
				}
			});
			addGUIElement(b);
			produceButtons.add(b);
			gameObjectTypeToProduceButton.put(gameObjectType, b);
			aNum++;
		}
	}

	/**
	 * Called when the screen should render itself.
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
	 * @param delta The time in seconds since the last render.
	 * @param wasClick true if there was already a click in this frame
	 */
	private void updateInput(float delta, boolean wasClick){
		if(wasTouchDown && Gdx.input.isTouched()) {
			if(!wasClick){
				moveCamera(-Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
				if(Math.abs(Gdx.input.getDeltaX() * Gdx.input.getDeltaY()) > 10){
					wasDrag = true;
				}
			}
		} else {
			if(!wasDrag && wasTouchDown && !Gdx.input.isTouched()){
				if(!wasClick) {
					if(gameController.getActivePlayer().isShowGUI()) {
						switch (aAction.actionType){
							case NONE:
								gameView.setActiveByPosition(unproject(Gdx.input.getX(), Gdx.input.getY()));
								selectField(gameView.getActiveX(), gameView.getActiveY());
								break;
							case MOVE:
							case FIGHT:
							case PRODUCE:
								vector2 = gameView.getFieldByPosition(unproject(Gdx.input.getX(), Gdx.input.getY()));
								finishAction((int)vector2.x, (int)vector2.y);
								break;
						}
					}
				}
			}
			wasDrag = false;
		}

		wasTouchDown = Gdx.input.isTouched();
	}

	/**
	 * Updates the money label
	 */
	private void updateMoney(){
		moneyLabel.setText(Language.getStrings().format("gameScreen.currency", gameController.getActivePlayer().getMoney()));
		moneyLabel.setTextColor(gameController.getActivePlayer().getColor());
	}

	private void setActionButtonsVisible(boolean visible){
		moveButton.setVisible(visible);
		fightButton.setVisible(visible);
		produceButton.setVisible(visible);
		cancelButton.setVisible(visible);
	}

	private void setActionButtonsActive(boolean active){
		moveButton.setActive(active);
		fightButton.setActive(active);
		produceButton.setActive(active);
		cancelButton.setActive(active);
	}

	/**
	 * activates only the buttons the active gameObject supports
	 * @param activeGameObject the active GameObject
	 */
	private void setActionButtonsActive(GameObject activeGameObject){
		setActionButtonsActive(false);
		moveButton.setActive(activeGameObject.getGameObjectType().getRadiusWalkMax() > 0);
		fightButton.setActive(activeGameObject.getGameObjectType().isCanFight());
		produceButton.setActive(activeGameObject.getGameObjectType().isCanProduce());
		cancelButton.setActive(false);
	}

	private void setProduceButtonsVisible(boolean visible){
		for(Button b : produceButtons){
			b.setVisible(visible);
		}
	}

	private void setProduceButtonsActive(boolean active){
		for(Button b : produceButtons){
			b.setActive(active);
		}
	}

	/**
	 * activates only the buttons the active gameObject supports
	 * @param activeGameObject the active GameObject
	 */
	private void setProduceButtonsActive(GameObject activeGameObject){
		setProduceButtonsActive(false);
		for(GameObjectType got : activeGameObject.getGameObjectType().getCanProduceList()){
			gameObjectTypeToProduceButton.get(got).setActive(true);
		}
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

	private void startMove(){
		aAction.actionType = Action.ActionType.MOVE;
		setProduceButtonsVisible(false);
		cancelButton.setActive(true);
	}

	private void startFight(){
		aAction.actionType = Action.ActionType.FIGHT;
		setProduceButtonsVisible(false);
		cancelButton.setActive(true);
	}

	private void startProduce(){
		aAction.actionType = Action.ActionType.NONE;
		setProduceButtonsVisible(true);
		cancelButton.setActive(true);
		setProduceButtonsActive(gameView.getActiveGameObject());
	}

	private void cancelAction(){
		setProduceButtonsVisible(false);
		aAction.actionType = Action.ActionType.NONE;
		cancelButton.setActive(false);
	}

	private void proposeEndPlaying(){
		gameController.getActivePlayer().proposeEndPlaying();
	}

	private void selectProduceGameObjectType(GameObjectType gameObjectType){
		aAction.actionType = Action.ActionType.PRODUCE;
		aAction.produceGameObjectType = gameObjectType;
		setProduceButtonsVisible(false);
	}

	private void selectField(int x, int y){
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

	private void finishAction(int x, int y){
		aAction.endX = x;
		aAction.endY = y;
		if(gameController.getActivePlayer().proposeAction(aAction)) {
			updateMoney();

			setActionButtonsVisible(false);
			setProduceButtonsVisible(false);
			aAction.actionType = Action.ActionType.NONE;
		}
	}
}
