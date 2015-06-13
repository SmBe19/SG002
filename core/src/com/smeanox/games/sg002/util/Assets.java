package com.smeanox.games.sg002.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.smeanox.games.sg002.debug.Timer;
import com.smeanox.games.sg002.world.GameObjectType;

/**
 * Manages all Assets
 *
 * @author Benjamin Schmid
 */
public class Assets {
	private static AssetManager manager;

	/**
	 * smeanox logo
	 */
	public static Texture smeanox;

	/**
	 * button
	 */
	public static Texture button;

	/**
	 * background
	 */
	public static Texture background;

	/**
	 * selection
	 */
	public static Texture selection;
	/**
	 * grid
	 */
	public static Texture grid;
	/**
	 * possible field for movement
	 */
	public static Texture possibleFieldMove;
	/**
	 * possible field for fight
	 */
	public static Texture possibleFieldFight;
	/**
	 * possible field for production
	 */
	public static Texture possibleFieldProduce;

	/**
	 * liberation font
	 */
	public static FreeType liberation;
	public static BitmapFontRapper liberationMicroShadow;
	public static BitmapFontRapper liberationMicro;
	public static BitmapFontRapper liberationSmall;
	public static BitmapFontRapper liberationMedium;
	public static BitmapFontRapper liberationLarge;

	private static boolean finishedCompletly;

	private Assets() {
	}

	/**
	 * loads all assets necessary to display the splash screen
	 */
	public static void loadAssetsSplashScreen() {
		if (manager == null) {
			manager = new AssetManager();
		}

		TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
		param.minFilter = Consts.textureFilter;
		param.genMipMaps = param.minFilter == Texture.TextureFilter.MipMapLinearLinear;
		manager.load("smeanox.png", Texture.class, param);

		manager.finishLoading();

		smeanox = manager.get("smeanox.png", Texture.class);
	}

	/**
	 * prepares the AssetManager to load the assets
	 */
	public static void prepareLoadAssets() {
		if (manager == null) {
			manager = new AssetManager();
		}

		liberationSmall = new BitmapFontRapper();
		liberationMedium = new BitmapFontRapper();
		liberationLarge = new BitmapFontRapper();

		TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
		param.minFilter = Consts.textureFilter;
		param.genMipMaps = param.minFilter == Texture.TextureFilter.MipMapLinearLinear;

		manager.load("images/button.png", Texture.class);
		manager.load("images/background.png", Texture.class, param);
		manager.load("images/selection.png", Texture.class, param);
		manager.load("images/grid.png", Texture.class, param);
		manager.load("images/possibleFieldMove.png", Texture.class, param);
		manager.load("images/possibleFieldFight.png", Texture.class, param);
		manager.load("images/possibleFieldProduce.png", Texture.class, param);

		finishedCompletly = false;
	}

	/**
	 * Adds the asset to be loaded
	 *
	 * @param filename filename of the asset to be loaded
	 * @param type     class of the asset
	 */
	public static void addToLoadQueue(String filename, Class type) {
		manager.load(filename, type);
	}

	/**
	 * loads the assets
	 *
	 * @return true if the assets are loaded
	 */
	public static boolean loadAssets() {
		if (manager.update()) {
			if (!finishedCompletly) {
				finishedLoading();
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * returns the loaded asset
	 *
	 * @param name filename of the loaded asset
	 * @param <T>
	 * @return the asset
	 */
	public static <T> T getAsset(String name) {
		return (T) manager.get(name);
	}

	/**
	 * assigns the loaded assets to variables
	 */
	private static void finishedLoading() {
		button = manager.get("images/button.png", Texture.class);
		background = manager.get("images/background.png", Texture.class);
		selection = manager.get("images/selection.png", Texture.class);
		grid = manager.get("images/grid.png", Texture.class);
		possibleFieldMove = manager.get("images/possibleFieldMove.png", Texture.class);
		possibleFieldFight = manager.get("images/possibleFieldFight.png", Texture.class);
		possibleFieldProduce = manager.get("images/possibleFieldProduce.png", Texture.class);


		setGameObjectTypeTextures();

		createFonts();

		finishedCompletly = true;
	}

	private static void setGameObjectTypeTextures() {
		for (GameObjectType gameObjectType : GameObjectType.getAllGameObjectTypes()) {
			gameObjectType.setTexture(manager.get(gameObjectType.getTextureName(), Texture.class));
		}
	}

	/**
	 * Creates fonts on the fly for the active screen size
	 */
	public static void createFonts() {
		if (liberationMicroShadow == null) {
			liberationMicroShadow = new BitmapFontRapper();
		}
		if (liberationMicro == null) {
			liberationMicro = new BitmapFontRapper();
		}
		if (liberationSmall == null) {
			liberationSmall = new BitmapFontRapper();
		}
		if (liberationMedium == null) {
			liberationMedium = new BitmapFontRapper();
		}
		if (liberationLarge == null) {
			liberationLarge = new BitmapFontRapper();
		}

		if (liberationSmall.bitmapFont != null) {
			liberationSmall.bitmapFont.dispose();
		}
		if (liberationMedium.bitmapFont != null) {
			liberationMedium.bitmapFont.dispose();
		}
		if (liberationLarge.bitmapFont != null) {
			liberationLarge.bitmapFont.dispose();
		}

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/LiberationSans-Regular.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();

		// add Eur sign
		parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "\u20AC";

		parameter.size = (int) Math.ceil(Consts.fontSizeMicro * Consts.devScaleY);
		liberationMicro.bitmapFont = generator.generateFont(parameter);
		parameter.size = (int) Math.ceil(Consts.fontSizeSmall * Consts.devScaleY);
		liberationSmall.bitmapFont = generator.generateFont(parameter);
		parameter.size = (int) Math.ceil(Consts.fontSizeMedium * Consts.devScaleY);
		liberationMedium.bitmapFont = generator.generateFont(parameter);
		parameter.size = (int) Math.ceil(Consts.fontSizeLarge * Consts.devScaleY);
		liberationLarge.bitmapFont = generator.generateFont(parameter);

		parameter.size = (int) Math.ceil(Consts.fontSizeMicro * Consts.devScaleY);
		parameter.shadowColor = Color.BLACK;
		parameter.shadowOffsetX = (int) Math.ceil(0.5 * Consts.devScaleX);
		parameter.shadowOffsetY = (int) Math.ceil(0.5 * Consts.devScaleY);
		liberationMicroShadow.bitmapFont = generator.generateFont(parameter);

		generator.dispose();
	}

	/**
	 * Disposes all assets
	 */
	public static void unload() {
		manager.dispose();
	}
}
