package com.smeanox.games.sg002.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/**
 * Manages the different Screens
 * @author Benjamin Schmid
 */
public class ScreenManager {

	private static Game game;

	private static Screen splashScreen;
	private static Screen gameScreen;
	private static Screen menuScreen;

	private ScreenManager(){
	}

	/**
	 * Initializes the screen manager
	 * @param game the game to set the screens to
	 */
	public static void init(Game game){
		ScreenManager.game = game;

		splashScreen = new SplashScreen();
		gameScreen = new GameScreen();
		menuScreen = new MenuScreen();
	}

	/**
	 * Sets the splash screen as active screen
	 */
	public static void showSplash(){
		game.setScreen(splashScreen);
	}

	/**
	 * Sets the splash screen as active screen
	 */
	public static void showMenu(){
		game.setScreen(menuScreen);
	}

	/**
	 * Sets the game screen as active screen
	 */
	public static void showGame(){
		game.setScreen(gameScreen);
	}
}
