package com.smeanox.games.sg002.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

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

	/** The font size for micre */
	public static final int fontSizeMicro = 12;
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

	/** The color to display the hp in */
	public static final Color hpColor = Color.WHITE;

	/** The color for the different players */
	public static final Color playerColors[] = new Color[]{Color.BLUE, Color.RED, Color.GREEN,
			Color.ORANGE, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.OLIVE, Color.NAVY,
			Color.PURPLE, Color.MAROON};

	/** The amount the zoom factor is multiplied by per click */
	public static final float zoomStep = 1.1f;

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
