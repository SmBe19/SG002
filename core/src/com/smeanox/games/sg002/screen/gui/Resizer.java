package com.smeanox.games.sg002.screen.gui;

import com.badlogic.gdx.math.Rectangle;

/**
 * Handles resizing
 *
 * @author Benjamin Schmid
 */
public interface Resizer {
	public Rectangle getNewSize(float width, float height);
}
