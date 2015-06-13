package com.smeanox.games.sg002.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import java.util.LinkedList;

/**
 * Contains all constants
 *
 * @author Benjamin Schmid
 */
public class Consts {

	/**
	 * Whether the application is running in a headless mode. In this mode {@link Gdx} won't be
	 * available and no Assets are loaded
	 */
	public static boolean headlessMode = false;

	/**
	 * The width in pixels the game was developed on. Everything will be scaled accordingly.
	 */
	public static final int devWidth = 800;
	/**
	 * The height in pixels the game was developed on. Everything will be scaled accordingly.
	 */
	public static final int devHeight = 460;
	/**
	 * The scale between devWidth and screenWidth
	 */
	public static float devScaleX = 1.0f;
	/**
	 * The scale between devHeight and screenHeight
	 */
	public static float devScaleY = 1.0f;

	/**
	 * The textureFilter to use to render the graphics
	 */
	public static Texture.TextureFilter textureFilter = Texture.TextureFilter.Nearest;

	/**
	 * The font size for micro
	 */
	public static final int fontSizeMicro = 16;
	/**
	 * The font size for small
	 */
	public static final int fontSizeSmall = 22;
	/**
	 * The font size for medium
	 */
	public static final int fontSizeMedium = 32;
	/**
	 * The font size for large
	 */
	public static final int fontSizeLarge = 42;

	/**
	 * The size of a single tile on the map
	 */
	public static final int fieldSizeX = 512;
	/**
	 * The size of a single tile on the map
	 */
	public static final int fieldSizeY = 512;
	/**
	 * The number of fields the background covers
	 */
	public static final int backgroundFieldsX = 4;
	/**
	 * The number of fields the background covers
	 */
	public static final int backgroundFieldsY = 4;

	/**
	 * The color to display the hp in
	 */
	public static final Color hpColor = Color.WHITE;
	/**
	 * The color to display if the GameObject was used
	 */
	public static final Color usedColor = Color.DARK_GRAY;
	/**
	 * The color to display the grid in
	 */
	public static final Color gridColor = new Color(0.9f, 0.9f, 0.9f, 0.25f);
	/**
	 * The color to displey the canMove marker in
	 */
	public static final Color canMoveColor = Color.BLUE;
	/**
	 * The color to displey the canMove marker in
	 */
	public static final Color canFightColor = Color.RED;
	/**
	 * The color to displey the canMove marker in
	 */
	public static final Color canProduceColor = Color.ORANGE;

	/**
	 * The minimal zoom factor for the hp text to show up
	 */
	public static final float hpDisplayMinZoom = 0.06f;

	/**
	 * The color for the different players
	 */
	public static final Color playerColors[] = new Color[]{Color.BLUE, Color.RED, Color.GREEN,
			Color.ORANGE, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.OLIVE, Color.NAVY,
			Color.PURPLE, Color.MAROON};

	/**
	 * The amount the zoom factor is multiplied by per click
	 */
	public static final float zoomStep = 1.1f;

	/**
	 * number of seconds the AI should wait until the next player plays
	 */
	public static final float aiSleep = 0.5f;

	// Game Settings, read from scenario
	/**
	 * Whether the GameObjects can move diagonal (different radius form)
	 */
	public static boolean walkDiagonal = false;

	/**
	 * Whether an object can perform multiple actions per round (one of each)
	 */
	public static boolean multipleActionsPerObject = true;

	/**
	 * The minimal distance between two start GameObjects
	 */
	public static int startGameObjectMinDistance = 2;

	/**
	 * The name of the file used for QuickSaving
	 */
	public static final String quickSaveFileName = "quicksave.xml";

	/**
	 * Keyboard Shortcuts
	 */
	public static class KeyboardShortcuts {
		public static final int backKey = Input.Keys.ESCAPE;
		public static final int quickSave = Input.Keys.F5;
		public static final int quickLoad = Input.Keys.F6;
		public static final int nextPlayer = Input.Keys.ENTER;
		public static final int produceArcher = Input.Keys.A;
		public static final int produceGoldMine = Input.Keys.G;
		public static final int produceKnight = Input.Keys.K;
		public static final int produceInfantry = Input.Keys.I;
		public static final int produceTownCenter = Input.Keys.T;
		public static final int produceVillager = Input.Keys.V;
		public static final int produce = Input.Keys.C;
		public static final int move = Input.Keys.M;
		public static final int fight = Input.Keys.F;
		public static final int cancel = Input.Keys.BACKSPACE;

		private static LinkedList<Integer> allShortcuts;

		/**
		 * Return a list of all keyboard shortcuts
		 *
		 * @return the list
		 */
		public static LinkedList<Integer> getAllShortcuts() {
			if (allShortcuts == null) {
				allShortcuts = new LinkedList<Integer>();
				allShortcuts.add(backKey);
				allShortcuts.add(quickLoad);
				allShortcuts.add(quickSave);
				allShortcuts.add(nextPlayer);
				allShortcuts.add(produceArcher);
				allShortcuts.add(produceGoldMine);
				allShortcuts.add(produceKnight);
				allShortcuts.add(produceInfantry);
				allShortcuts.add(produceTownCenter);
				allShortcuts.add(produceVillager);
				allShortcuts.add(produce);
				allShortcuts.add(move);
				allShortcuts.add(fight);
				allShortcuts.add(cancel);
			}
			return allShortcuts;
		}
	}

	private Consts() {
	}

	/**
	 * updates the devScale according to screenSize
	 */
	public static void updateScale() {
		float oldX, oldY;
		oldX = devScaleX;
		oldY = devScaleY;

		devScaleX = (float) Gdx.graphics.getWidth() / devWidth;
		devScaleY = (float) Gdx.graphics.getHeight() / devHeight;

		if (Math.abs(oldX - devScaleX) > 0.001f || Math.abs(oldY - devScaleY) > 0.001f) {
			Assets.createFonts();
		}
	}
}
