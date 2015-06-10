package com.smeanox.games.sg002.util;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * A Wrapper for a BitmapFont so it can be changed on resize
 *
 * @author Benjamin Schmid
 */
public class BitmapFontRapper {
	public BitmapFont bitmapFont;

	public BitmapFontRapper() {
	}

	public BitmapFontRapper(BitmapFont bitmapFont) {
		this.bitmapFont = bitmapFont;
	}
}
