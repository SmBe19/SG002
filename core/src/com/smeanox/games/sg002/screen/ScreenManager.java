package com.smeanox.games.sg002.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.smeanox.games.sg002.world.GameController;
import com.smeanox.games.sg002.world.Scenario;

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
		game.setScreen(gameScreen);
	}

	/**
	 * Sets the game screen as active screen with the given scenario loaded
	 */
	public static void showGame(GameController gameController){
		gameScreen = null;
		gameScreen = new GameScreen(gameController);
		game.setScreen(gameScreen);
	}
}
