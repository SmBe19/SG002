package com.smeanox.games.sg002.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.smeanox.games.sg002.player.Player;
import com.smeanox.games.sg002.util.Assets;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.world.Action;
import com.smeanox.games.sg002.world.GameObject;
import com.smeanox.games.sg002.world.GameWorld;

/**
 * Render the GameWorld
 *
 * @author Benjamin Schmid
 */
public class GameView {
	private GameWorld gameWorld;
	private GlyphLayout glyphLayout;
	private float zoom;
	private float aFieldSizeX;
	private float aFieldSizeY;
	private int activeX;
	private int activeY;
	private TextureRegion backgroundRegions[][];

	/**
	 * Create a new instance
	 *
	 * @param gameWorld the gameWorld to display
	 */
	public GameView(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
		activeX = -1;
		activeY = -1;
		zoom = 0.1f;
		glyphLayout = new GlyphLayout();
		initBackgroundRegions();
	}

	/**
	 * Initialize the list of background regions. The background is split into several texture regions
	 */
	private void initBackgroundRegions() {
		backgroundRegions = new TextureRegion[Consts.backgroundFieldsY][Consts.backgroundFieldsX];
		int backgroundRegionWidth, backgroundRegionHeight;
		backgroundRegionWidth = Assets.background.getWidth() / Consts.backgroundFieldsX;
		backgroundRegionHeight = Assets.background.getHeight() / Consts.backgroundFieldsY;
		for (int y = 0; y < Consts.backgroundFieldsY; y++) {
			for (int x = 0; x < Consts.backgroundFieldsX; x++) {
				backgroundRegions[Consts.backgroundFieldsY - y - 1][x] = new TextureRegion(Assets.background,
						x * backgroundRegionWidth, y * backgroundRegionHeight,
						backgroundRegionWidth, backgroundRegionHeight);
			}
		}
	}

	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}

	public void zoomIn() {
		zoom *= Consts.zoomStep;
	}

	public void zoomOut() {
		zoom /= Consts.zoomStep;
	}

	public int getActiveX() {
		return activeX;
	}

	public void setActiveX(int activeX) {
		this.activeX = activeX;
	}

	public int getActiveY() {
		return activeY;
	}

	public void setActiveY(int activeY) {
		this.activeY = activeY;
	}

	/**
	 * Return the GameObject at the position [acitveX, acitveY]
	 *
	 * @return the GameObject or null if there is no GameObject
	 */
	public GameObject getActiveGameObject() {
		return gameWorld.getWorldMap(activeX, activeY);
	}

	/**
	 * Return the Field that belongs to the given coordinates in world space
	 *
	 * @param x coordinates in world space
	 * @param y coordinates in world space
	 * @return the coordinates of the field
	 */
	public Vector2 getFieldByPosition(float x, float y) {
		int newActiveX, newActiveY;
		newActiveX = (int) (x / (Consts.fieldSizeX * Consts.devScaleY * zoom));
		newActiveY = (int) (y / (Consts.fieldSizeY * Consts.devScaleY * zoom));
		return new Vector2(newActiveX, newActiveY);
	}

	/**
	 * Return the Field that belongs to the given coordinates in world space
	 *
	 * @param vector2 coordinates in world space
	 * @return the coordinates of the field
	 */
	public Vector2 getFieldByPosition(Vector2 vector2) {
		return getFieldByPosition(vector2.x, vector2.y);
	}

	/**
	 * Return the coordinates in world space of the lower left corner of the given field
	 *
	 * @param x coordinates of the field
	 * @param y coordinates of the field
	 * @return coordinates in world space
	 */
	public Vector2 getPositionByField(int x, int y) {
		float positionX, positionY;
		positionX = x * Consts.fieldSizeX * Consts.devScaleY * zoom;
		positionY = y * Consts.fieldSizeX * Consts.devScaleY * zoom;
		return new Vector2(positionX, positionY);
	}

	/**
	 * Set the active field to the field at the given coordinates
	 *
	 * @param x coordinates in world space
	 * @param y coordinates in world space
	 */
	public void setActiveByPosition(float x, float y) {
		int newActiveX, newActiveY;
		newActiveX = (int) (x / (Consts.fieldSizeX * Consts.devScaleY * zoom));
		newActiveY = (int) (y / (Consts.fieldSizeX * Consts.devScaleY * zoom));

		if (x >= 0 && newActiveX < gameWorld.getMapSizeX()
				&& y >= 0 && newActiveY < gameWorld.getMapSizeY()) {
			activeX = newActiveX;
			activeY = newActiveY;
		}
	}

	/**
	 * Set the active field to the field at the given coordinates
	 *
	 * @param vector2 coordinates in world space
	 */
	public void setActiveByPosition(Vector2 vector2) {
		setActiveByPosition(vector2.x, vector2.y);
	}

	/**
	 * render a Texture on a given field
	 *
	 * @param spriteBatch spriteBatch to use
	 * @param texture     the texture to draw
	 * @param x           coordinate of the field to draw
	 * @param y           coordinate of the field to draw
	 */
	private void renderField(SpriteBatch spriteBatch, Texture texture, int x, int y) {
		spriteBatch.draw(texture,
				x * aFieldSizeX,
				y * aFieldSizeY,
				aFieldSizeX,
				aFieldSizeY);
	}

	/**
	 * render the background
	 *
	 * @param spriteBatch spriteBatch
	 */
	private void renderBackground(SpriteBatch spriteBatch) {
		spriteBatch.setColor(Color.WHITE);
		for (int y = 0; y < gameWorld.getMapSizeY(); y++) {
			for (int x = 0; x < gameWorld.getMapSizeX(); x++) {
				spriteBatch.draw(backgroundRegions[y % Consts.backgroundFieldsY][x % Consts.backgroundFieldsX],
						x * aFieldSizeX, y * aFieldSizeY, aFieldSizeX, aFieldSizeY);
			}
		}
	}

	/**
	 * render the grid
	 *
	 * @param spriteBatch spriteBatch
	 */
	private void renderGrid(SpriteBatch spriteBatch) {
		spriteBatch.setColor(Consts.gridColor);
		for (int y = 0; y < gameWorld.getMapSizeY(); y++) {
			for (int x = 0; x < gameWorld.getMapSizeX(); x++) {
				renderField(spriteBatch, Assets.grid, x, y);
			}
		}
	}

	/**
	 * render the GameWorld
	 *
	 * @param spriteBatch spriteBatch
	 * @param activePlayer the player that is playing
	 */
	public void render(SpriteBatch spriteBatch, Player activePlayer) {
		aFieldSizeX = (Consts.fieldSizeX * Consts.devScaleY * zoom);
		aFieldSizeY = (Consts.fieldSizeY * Consts.devScaleY * zoom);

		renderBackground(spriteBatch);
		renderGrid(spriteBatch);

		GameObject gameObject;
		GameObject activeGameObject = gameWorld.getWorldMap(activeX, activeY);
		for (int y = 0; y < gameWorld.getMapSizeY(); y++) {
			for (int x = 0; x < gameWorld.getMapSizeX(); x++) {
				gameObject = gameWorld.getWorldMap(x, y);
				if (gameObject != null) {
					spriteBatch.setColor(gameObject.getPlayer().getColor());
					if (gameWorld.wasUsed(x, y)) {
						spriteBatch.setColor(Consts.usedColor);
					}
					renderField(spriteBatch, gameObject.getGameObjectType().getTexture(), x, y);

					if (zoom >= Consts.hpDisplayMinZoom) {
						Assets.liberationMicroShadow.bitmapFont.setColor(Consts.hpColor);
						glyphLayout.setText(Assets.liberationMicroShadow.bitmapFont, "" + gameObject.getHp());

						Assets.liberationMicroShadow.bitmapFont.draw(spriteBatch, glyphLayout,
								x * aFieldSizeX + (aFieldSizeX - glyphLayout.width) / 2f,
								y * aFieldSizeY + aFieldSizeY * 0.95f);
					}
				}

				if (x == activeX && y == activeY) {
					spriteBatch.setColor(activePlayer.getColor());
					renderField(spriteBatch, Assets.selection, x, y);
				} else {
					if (activeGameObject != null && activeGameObject.canMoveTo(x, y)) {
						spriteBatch.setColor(activeGameObject.wasUsed(Action.ActionType.MOVE) ? Consts.usedColor : Consts.canMoveColor);
						renderField(spriteBatch, Assets.possibleFieldMove, x, y);
					}
					if (activeGameObject != null && activeGameObject.canFightTo(x, y)) {
						spriteBatch.setColor(activeGameObject.wasUsed(Action.ActionType.FIGHT) ? Consts.usedColor : Consts.canFightColor);
						renderField(spriteBatch, Assets.possibleFieldFight, x, y);
					}
					if (activeGameObject != null && activeGameObject.canProduceTo(x, y)) {
						spriteBatch.setColor(activeGameObject.wasUsed(Action.ActionType.PRODUCE) ? Consts.usedColor : Consts.canProduceColor);
						renderField(spriteBatch, Assets.possibleFieldProduce, x, y);
					}
				}
			}
		}
	}
}
