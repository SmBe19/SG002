package com.smeanox.games.sg002.util;

import com.badlogic.gdx.Gdx;

/**
 * Contains all constants
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
	public static final int fontSizeSmall = 22;
	/** The font size for medium */
	public static final int fontSizeMedium = 32;
	/** The font size for large */
	public static final int fontSizeLarge = 42;

	/** The size of a single tile on the map */
	public static final int fieldSizeX = 512;
	/** The size of a single tile on the map */
	public static final int fieldSizeY = 512;

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
