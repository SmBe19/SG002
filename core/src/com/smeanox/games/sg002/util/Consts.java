package com.smeanox.games.sg002.util;

import com.badlogic.gdx.Gdx;

/**
 * Contains all constants
 *
 * @author Benjamin Schmid
 */
public class Consts {

	/** The width in pixels the game was developed on. Everything will be scaled accordingly. */
	public static final int devWidth = 800;
	/** The height in pixels the game was developed on. Everything will be scaled accordingly. */
	public static final int devHeight = 460;
	/** The scale between devWidth and screenWidth */
	public static float devScaleX = 1.0f;
	/** The scale between devHeight and screenHeight */
	public static float devScaleY = 1.0f;

	/** The font size for small */
	public static int fontSizeSmall = 22;
	/** The font size for medium */
	public static int fontSizeMedium = 32;
	/** The font size for large */
	public static int fontSizeLarge = 42;

	private Consts(){
	}

	/**
	 * updates the devScale according to screenSize
	 */
	public static void updateScale(){
		devScaleX = (float)Gdx.graphics.getWidth() / devWidth;
		devScaleY = (float)Gdx.graphics.getHeight() / devHeight;

		Assets.createFonts();
	}
}
