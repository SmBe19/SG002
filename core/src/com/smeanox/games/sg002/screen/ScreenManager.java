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
	}

	/**
	 * Sets the splash screen as active screen
	 */
	public static void showSplash(){
		if(splashScreen == null){
			splashScreen = new SplashScreen();
		}
		game.setScreen(splashScreen);
	}

	/**
	 * Sets the splash screen as active screen
	 */
	public static void showMenu(){
		if(menuScreen == null){
			menuScreen = new MenuScreen();
		}
		game.setScreen(menuScreen);
	}

	/**
	 * Sets the game screen as active screen
	 */
	public static void showGame(){
		if(gameScreen == null){
			gameScreen = new GameScreen();
		}
		game.setScreen(gameScreen);
	}
}
