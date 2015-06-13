package com.smeanox.games.sg002.screen.gui;

import com.badlogic.gdx.math.Rectangle;

/**
 * Handles resizing
 *
 * @author Benjamin Schmid
 */
public interface Resizer {
	/**
	 * Calculate the new size of the object
	 *
	 * @param width  size of the screen
	 * @param height size of the screen
	 * @return the new size
	 */
	Rectangle getNewSize(float width, float height);
}
