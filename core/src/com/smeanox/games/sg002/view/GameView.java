package com.smeanox.games.sg002.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.smeanox.games.sg002.util.Assets;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.world.GameObject;
import com.smeanox.games.sg002.world.GameWorld;

/**
 * Renders the GameWorld
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

	public GameView(GameWorld gameWorld){
		this.gameWorld = gameWorld;
		activeX = -1;
		activeY = -1;
		zoom = 0.1f;
		glyphLayout = new GlyphLayout();
	}

	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}

	public void zoomIn(){
		zoom *= Consts.zoomStep;
	}

	public void zoomOut(){
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

	public void setActiveByPosition(float x, float y){
		int newActiveX, newActiveY;
		newActiveX = (int)(x / (Consts.fieldSizeX * Consts.devScaleY * zoom));
		newActiveY = (int)(y / (Consts.fieldSizeX * Consts.devScaleY * zoom));

		if(newActiveX >= 0 && newActiveX < gameWorld.getMapSizeX()
				&& newActiveY >= 0 && newActiveY < gameWorld.getMapSizeY()){
			activeX = newActiveX;
			activeY = newActiveY;
		}
	}

	public void setActiveByPosition(Vector2 vector2){
		setActiveByPosition(vector2.x, vector2.y);
	}

	/**
	 * renders a Texture on a given field
	 * @param spriteBatch
	 * @param texture
	 * @param x
	 * @param y
	 */
	private void renderField(SpriteBatch spriteBatch, Texture texture, int x, int y){
		spriteBatch.draw(texture,
				x * aFieldSizeX,
				y * aFieldSizeY,
				aFieldSizeX,
				aFieldSizeY);
	}

	/**
	 * renders the GameWorld
	 * @param spriteBatch
	 */
	public void render(SpriteBatch spriteBatch){
		aFieldSizeX = (Consts.fieldSizeX * Consts.devScaleY * zoom);
		aFieldSizeY = (Consts.fieldSizeY * Consts.devScaleY * zoom);

		GameObject gameObject;
		for(int y = 0; y < gameWorld.getMapSizeY(); y++){
			for(int x = 0; x < gameWorld.getMapSizeX(); x++){
				renderField(spriteBatch, Assets.background, x, y);
				gameObject = gameWorld.getWorldMap(x, y);
				if(gameObject != null){
					renderField(spriteBatch, gameObject.getGameObjectType().getTexture(), x, y);

					Assets.liberationSmall.bitmapFont.setColor(Consts.hpColor);
					glyphLayout.setText(Assets.liberationSmall.bitmapFont, "" + gameObject.getHp());
					Assets.liberationSmall.bitmapFont.draw(spriteBatch, glyphLayout,
							x * aFieldSizeX + (aFieldSizeX - glyphLayout.width) / 2f,
							y * aFieldSizeY + aFieldSizeY * 0.9f);
				}

				if(x == activeX && y == activeY){
					Assets.liberationSmall.bitmapFont.setColor(Consts.hpColor);
					glyphLayout.setText(Assets.liberationSmall.bitmapFont, "active");
					Assets.liberationSmall.bitmapFont.draw(spriteBatch, glyphLayout,
							x * aFieldSizeX + (aFieldSizeX - glyphLayout.width) / 2f,
							y * aFieldSizeY + glyphLayout.height * 1.2f);
				}
			}
		}
	}
}
