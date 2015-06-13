package com.smeanox.games.sg002.screen.gui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * A GUIElement
 *
 * @author Benjamin Schmid
 */
public interface GUIElement {
	/**
	 * Called when the object should render itself.
	 *
	 * @param delta       The time in seconds since the last render.
	 * @param spriteBatch the spritebatch to use to draw.
	 */
	void render(float delta, SpriteBatch spriteBatch);

	/**
	 * called once per frame to check if the object is clicked
	 *
	 * @param touchPos the position where the user clicked last
	 * @param wasClick true if there was already a click in this frame.
	 * @return true if the element was clicked
	 */
	boolean updateClickable(Vector2 touchPos, boolean wasClick);

	/**
	 * add a {@link ClickHandler}
	 *
	 * @param handler the ClickHandler
	 */
	void addClickHandler(ClickHandler handler);

	/**
	 * remove a {@link ClickHandler}
	 *
	 * @param handler the ClickHandler
	 */
	void removeClickHandler(ClickHandler handler);

	/**
	 * Return the bounding box of this object
	 *
	 * @return the bounding box
	 */
	Rectangle getBoundingBox();

	/**
	 * Update the position and size of the object when the window size changed
	 *
	 * @param width  size of the window
	 * @param height size of the window
	 */
	void resize(float width, float height);

	boolean isVisible();

	void setVisible(boolean visible);

	boolean isActive();

	void setActive(boolean active);
}
