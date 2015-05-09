package com.smeanox.games.sg002.screen.gui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * A GUIElement
 * @author Benjamin Schmid
 */
public interface GUIElement {
	/**
	 * Called when the object should render itself.
	 * @param delta The time in seconds since the last render.
	 * @param spriteBatch the spritebatch to use to draw.
	 */
	void render(float delta, SpriteBatch spriteBatch);

	/**
	 * called once per frame to check if the object is clicked
	 */
	void updateClickable(Vector2 touchPos);

	/**
	 * adds a {@link ClickHandler}
	 * @param handler the ClickHandler
	 */
	void addClickHandler(ClickHandler handler);

	/**
	 * removes a {@link ClickHandler}
	 * @param handler the ClickHandler
	 */
	void removeClickHandler(ClickHandler handler);

	/**
	 * Returns the bounding box of this object
	 * @return the bounding box
	 */
	Rectangle getBoundingBox();

	/**
	 * Updates the position and size of the object when the window size changed
	 * @param width
	 * @param height
	 */
	void resize(float width, float height);
}
