package com.smeanox.games.sg002.screen;

import com.badlogic.gdx.Game;
import com.smeanox.games.sg002.world.GameController;

/**
 * Manage the different Screens
 *
 * @author Benjamin Schmid
 */
public class ScreenManager {
	private static Game game;

	private static SplashScreen splashScreen;
	private static GameScreen gameScreen;
	private static MenuScreen menuScreen;
	private static PauseScreen pauseScreen;

	private ScreenManager() {
	}

	/**
	 * Initialize the screen manager
	 *
	 * @param game the game to set the screens to
	 */
	public static void init(Game game) {
		ScreenManager.game = game;
	}

	/**
	 * Set the splash screen as active screen
	 */
	public static void showSplash() {
		if (splashScreen == null) {
			splashScreen = new SplashScreen();
		}
		game.setScreen(splashScreen);
	}

	/**
	 * Set the splash screen as active screen
	 */
	public static void showMenu() {
		if (menuScreen == null) {
			menuScreen = new MenuScreen();
		}
		game.setScreen(menuScreen);
	}

	/**
	 * Set the pause menu as active screen
	 *
	 * @param gameController the GameController to use
	 */
	public static void showPauseMenu(GameController gameController) {
		if (pauseScreen == null) {
			pauseScreen = new PauseScreen();
		}
		pauseScreen.setGameController(gameController);
		game.setScreen(pauseScreen);
	}

	/**
	 * Set the game screen as active screen (the game screen has to have been the active screen before)
	 */
	public static void showGame() {
		game.setScreen(gameScreen);
	}

	/**
	 * Set the game screen as active screen with the given scenario loaded
	 *
	 * @param gameController the GameController to start the game with
	 */
	public static void showGame(GameController gameController) {
		gameScreen = null;
		gameScreen = new GameScreen(gameController);
		game.setScreen(gameScreen);
	}
}
