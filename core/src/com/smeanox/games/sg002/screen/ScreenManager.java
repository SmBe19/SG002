package com.smeanox.games.sg002.screen;

import com.badlogic.gdx.Game;
import com.smeanox.games.sg002.world.GameController;

/**
 * Manages the different Screens
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
	 * Initializes the screen manager
	 *
	 * @param game the game to set the screens to
	 */
	public static void init(Game game) {
		ScreenManager.game = game;
	}

	/**
	 * Sets the splash screen as active screen
	 */
	public static void showSplash() {
		if (splashScreen == null) {
			splashScreen = new SplashScreen();
		}
		game.setScreen(splashScreen);
	}

	/**
	 * Sets the splash screen as active screen
	 */
	public static void showMenu() {
		if (menuScreen == null) {
			menuScreen = new MenuScreen();
		}
		game.setScreen(menuScreen);
	}

	/**
	 * Sets the pause menu as active screen
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
	 * Sets the game screen as active screen
	 */
	public static void showGame() {
		game.setScreen(gameScreen);
	}

	/**
	 * Sets the game screen as active screen with the given scenario loaded
	 *
	 * @param gameController the GameController to start the game with
	 */
	public static void showGame(GameController gameController) {
		gameScreen = null;
		gameScreen = new GameScreen(gameController);
		game.setScreen(gameScreen);
	}
}
