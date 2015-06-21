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
import com.smeanox.games.sg002.world.MapObject;

import com.smeanox.games.sg002.data.Point;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	private boolean needCacheRefresh;
	private Map<Action.ActionType, Set<Point>> canActionToCache;
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
		canActionToCache = new HashMap<Action.ActionType, Set<Point>>();
		for(Action.ActionType actionType : Action.ActionType.values()){
			canActionToCache.put(actionType, new HashSet<Point>());
		}
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
		needCacheRefresh = true;
	}

	public int getActiveY() {
		return activeY;
	}

	public void setActiveY(int activeY) {
		this.activeY = activeY;
		needCacheRefresh = true;
	}

	/**
	 * Return the GameObject at the position [acitveX, acitveY]
	 *
	 * @return the GameObject or null if there is no GameObject
	 */
	public GameObject getActiveGameObject() {
		return gameWorld.getWorldGameObject(activeX, activeY);
	}

	/**
	 * Return the Field that belongs to the given coordinates in world space
	 *
	 * @param x coordinates in world space
	 * @param y coordinates in world space
	 * @return the coordinates of the field
	 */
	public Point getFieldByPosition(float x, float y) {
		int newActiveX, newActiveY;
		newActiveX = (int) (x / (Consts.fieldSizeX * Consts.devScaleY * zoom));
		newActiveY = (int) (y / (Consts.fieldSizeY * Consts.devScaleY * zoom));
		return new Point(newActiveX, newActiveY);
	}

	/**
	 * Return the Field that belongs to the given coordinates in world space
	 *
	 * @param vector2 coordinates in world space
	 * @return the coordinates of the field
	 */
	public Point getFieldByPosition(Vector2 vector2) {
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
		needCacheRefresh = true;
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
		renderField(spriteBatch, texture, x, y, 0f, 0f, 1f, 1f);
	}

	/**
	 * render a Texture on a given field with a specified offset and size
	 *
	 * @param spriteBatch spriteBatch to use
	 * @param texture     the texture to draw
	 * @param x           coordinate of the field to draw
	 * @param y           coordinate of the field to draw
	 * @param offX        relative offset of the texture, 0.0 is left border, 1.0 is right border of the field
	 * @param offY        relative offset of the texture, 0.0 is top border, 1.0 is bottom border of the field
	 * @param width       relative width of the texture, 1.0 is width of the field
	 * @param height      relative height of the texture, 1.0 is height of the field
	 */
	private void renderField(SpriteBatch spriteBatch, Texture texture, int x, int y, float offX, float offY, float width, float height) {
		spriteBatch.draw(texture,
				(x + offX) * aFieldSizeX,
				(y + offY) * aFieldSizeY,
				aFieldSizeX * width,
				aFieldSizeY * height);
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

		if(needCacheRefresh){
			rebuildCache();
		}

		GameObject gameObject;
		MapObject mapObject;
		GameObject activeGameObject = gameWorld.getWorldGameObject(activeX, activeY);
		for(int y = 0; y < gameWorld.getMapSizeY(); y++){
			for(int x = 0; x < gameWorld.getMapSizeX(); x++){
				//render MapObjects
				mapObject = gameWorld.getWorldMapObject(x, y);
				spriteBatch.setColor(Color.WHITE);
				renderField(spriteBatch, mapObject.getMapObjectType().getTexture(), x, y);

				//render GameObjects
				gameObject = gameWorld.getWorldGameObject(x, y);
				if (gameObject != null) {
					spriteBatch.setColor(((float)gameObject.getHp() / gameObject.getGameObjectType().getDefaultHP() > 0.4) ? Color.GREEN : Color.RED);
					renderField(spriteBatch, Assets.healthbar, x, y, 0f, 0f, (float)gameObject.getHp() / gameObject.getGameObjectType().getDefaultHP(), 1f);

					spriteBatch.setColor(gameObject.getPlayer().getColor());
					if(gameWorld.wasUsed(x, y) && gameWorld.getWorldGameObject(x, y).getPlayer().equals(activePlayer)
							&& !gameObject.getGameObjectType().isCanDoAction(Action.ActionType.NONE)){
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
				}
			}
		}

		if(activeGameObject != null) {
			for (Action.ActionType actionType : Action.ActionType.values()) {
				Color actionColor = Consts.usedColor;
				switch (actionType) {
					case MOVE:
						actionColor = Consts.canMoveColor;
						break;
					case FIGHT:
						actionColor = Consts.canFightColor;
						break;
					case PRODUCE:
						actionColor = Consts.canProduceColor;
						break;
				}
				spriteBatch.setColor(activeGameObject.wasUsed(actionType) ? Consts.usedColor : actionColor);

				Texture fieldTexture = null;

				switch (actionType) {
					case MOVE:
						fieldTexture = Assets.possibleFieldMove;
						break;
					case FIGHT:
						fieldTexture = Assets.possibleFieldFight;
						break;
					case PRODUCE:
						fieldTexture = Assets.possibleFieldProduce;
						break;
				}

				if (fieldTexture != null) {
					for (Point point : canActionToCache.get(actionType)) {
						renderField(spriteBatch, fieldTexture, point.x, point.y);
					}
				}
			}
		}
	}

	/**
	 * Rebuild the cache of fields that are available for a given action
	 */
	private void rebuildCache(){
		needCacheRefresh = false;
		for(Action.ActionType actionType : Action.ActionType.values()){
			canActionToCache.get(actionType).clear();
		}
		GameObject activeGameObject = gameWorld.getWorldGameObject(activeX, activeY);
		if(activeGameObject == null) {
			return;
		}
		for(int y = 0; y < gameWorld.getMapSizeY(); y++) {
			for (int x = 0; x < gameWorld.getMapSizeX(); x++) {
				if (activeGameObject.canMoveTo(x, y)) {
					canActionToCache.get(Action.ActionType.MOVE).add(new Point(x, y));
				}
				if (activeGameObject.canFightTo(x, y)) {
					canActionToCache.get(Action.ActionType.FIGHT).add(new Point(x, y));
				}
				if (activeGameObject.canProduceTo(x, y)) {
					canActionToCache.get(Action.ActionType.PRODUCE).add(new Point(x, y));
				}
			}
		}
	}
}
