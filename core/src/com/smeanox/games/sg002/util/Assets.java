package com.smeanox.games.sg002.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

/**
 * Manages all Assets
 * @author Benjamin Schmid
 */
public class Assets {
	private static AssetManager manager;

	/** smeanox logo */
	public static Texture smeanox;

	/** liberation font */
	public static FreeType liberation;
	public static BitmapFont liberationSmall;
	public static BitmapFont liberationMedium;
	public static BitmapFont liberationLarge;

	private Assets(){
	}

	/**
	 * loads all assets necessary to display the splash screen
	 */
	public static void loadAssetsSplashScreen(){
		if(manager == null) {
			manager = new AssetManager();
		}

		manager.load("smeanox.png", Texture.class);

		manager.finishLoading();

		smeanox = manager.get("smeanox.png", Texture.class);
	}

	/**
	 * prepares the AssetManager to load the assets
	 */
	public static void prepareLoadAssets(){
		if(manager == null) {
			manager = new AssetManager();
		}
	}

	/**
	 * loads the assets
	 * @return true if the assets are loaded
	 */
	public static boolean loadAssets(){
		if(manager.update()){
			finishedLoading();
			return true;
		}
		return false;
	}

	/**
	 * assigns the loaded assets to variables
	 */
	private static void finishedLoading(){
		createFonts();
	}

	/**
	 * Creates fonts on the fly for the active screen size
	 */
	public static void createFonts(){
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/LiberationSans-Regular.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();

		parameter.size = (int)(Consts.fontSizeSmall * Consts.devScaleY);
		liberationSmall = generator.generateFont(parameter);
		parameter.size = (int)(Consts.fontSizeMedium * Consts.devScaleY);
		liberationMedium = generator.generateFont(parameter);
		parameter.size = (int)(Consts.fontSizeLarge * Consts.devScaleY);
		liberationLarge = generator.generateFont(parameter);

		generator.dispose();
	}
}
