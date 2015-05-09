package com.smeanox.games.sg002.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

	/** button */
	public static Texture button;

	/** liberation font */
	public static FreeType liberation;
	public static BitmapFontRapper liberationSmall;
	public static BitmapFontRapper liberationMedium;
	public static BitmapFontRapper liberationLarge;

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

		liberationSmall = new BitmapFontRapper();
		liberationMedium = new BitmapFontRapper();
		liberationLarge = new BitmapFontRapper();

		manager.load("images/button.png", Texture.class);
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
		button = manager.get("images/button.png", Texture.class);

		createFonts();
	}

	/**
	 * Creates fonts on the fly for the active screen size
	 */
	public static void createFonts(){
		if(liberationSmall == null) {
			liberationSmall = new BitmapFontRapper();
		}
		if(liberationMedium == null) {
			liberationMedium = new BitmapFontRapper();
		}
		if(liberationLarge == null) {
			liberationLarge = new BitmapFontRapper();
		}

		if(liberationSmall.bitmapFont != null) {
			liberationSmall.bitmapFont.dispose();
		}
		if(liberationMedium.bitmapFont != null) {
			liberationMedium.bitmapFont.dispose();
		}
		if(liberationLarge.bitmapFont != null) {
			liberationLarge.bitmapFont.dispose();
		}

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/LiberationSans-Regular.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();

		parameter.size = (int)Math.ceil(Consts.fontSizeSmall * Consts.devScaleY);
		liberationSmall.bitmapFont = generator.generateFont(parameter);
		parameter.size = (int)Math.ceil(Consts.fontSizeMedium * Consts.devScaleY);
		liberationMedium.bitmapFont = generator.generateFont(parameter);
		parameter.size = (int) Math.ceil(Consts.fontSizeLarge * Consts.devScaleY);
		liberationLarge.bitmapFont = generator.generateFont(parameter);

		generator.dispose();
	}

	/**
	 * Disposes all assets
	 */
	public static void unload(){
		manager.dispose();
	}
}
