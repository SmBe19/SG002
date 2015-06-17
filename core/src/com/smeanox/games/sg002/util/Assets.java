package com.smeanox.games.sg002.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.smeanox.games.sg002.world.GameObjectType;
import com.smeanox.games.sg002.world.MapObjectType;

/**
 * Manage all Assets
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
	 * healthbar tex
	 */
	public static Texture healthbar;
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
	/** possible field for goldmine */
	public static Texture gold;

	/**
	 * liberation font
	 */
	public static BitmapFontRapper liberationMicroShadow;
	public static BitmapFontRapper liberationMicro;
	public static BitmapFontRapper liberationSmall;
	public static BitmapFontRapper liberationMedium;
	public static BitmapFontRapper liberationLarge;

	private static boolean finishedCompletly;

	private Assets() {
	}

	/**
	 * load all assets necessary to display the splash screen
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
	 * prepare the AssetManager to load the assets
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
		manager.load("images/gold.png", Texture.class, param);
		manager.load("images/healthbar.png", Texture.class, param);

		finishedCompletly = false;
	}

	/**
	 * Add the asset to be loaded
	 *
	 * @param filename filename of the asset to be loaded
	 * @param type     class of the asset
	 */
	public static void addToLoadQueue(String filename, Class type){

		TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
		param.minFilter = Consts.textureFilter;
		param.genMipMaps =  param.minFilter == Texture.TextureFilter.MipMapLinearLinear;
		manager.load(filename, type, param);
	}

	/**
	 * load the assets
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
	 * return the loaded asset
	 *
	 * @param name filename of the loaded asset
	 * @param <T>  Type of the asset
	 * @return the asset
	 */
	public static <T> T getAsset(String name) {
		return (T) manager.get(name);
	}

	/**
	 * assign the loaded assets to variables
	 */
	private static void finishedLoading() {
		button = manager.get("images/button.png", Texture.class);
		background = manager.get("images/background.png", Texture.class);
		selection = manager.get("images/selection.png", Texture.class);
		grid = manager.get("images/grid.png", Texture.class);
		possibleFieldMove = manager.get("images/possibleFieldMove.png", Texture.class);
		possibleFieldFight = manager.get("images/possibleFieldFight.png", Texture.class);
		possibleFieldProduce = manager.get("images/possibleFieldProduce.png", Texture.class);
		gold = manager.get("images/gold.png", Texture.class);
		healthbar = manager.get("images/healthbar.png", Texture.class);


		setGameObjectTypeTextures();
		setMapObjectTypeTextures();

		createFonts();

		finishedCompletly = true;
	}

	/**
	 * assign the loaded assets to gameObjectTypes
	 */
	private static void setGameObjectTypeTextures(){
		for(GameObjectType gameObjectType : GameObjectType.getAllGameObjectTypes()){
			gameObjectType.setTexture(manager.get(gameObjectType.getTextureName(), Texture.class));
		}
	}

	/**
	 * assign the loaded assets to mapObjectTypes
	 */
	private static void setMapObjectTypeTextures(){
		for(MapObjectType mapObjectType : MapObjectType.getMapObjectTypes()){
			mapObjectType.setTexture(manager.get(mapObjectType.getTextureName(), Texture.class));
		}
	}

	/**
	 * Create fonts on the fly for the active screen size
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
	 * Dispose all assets
	 */
	public static void unload() {
		manager.dispose();
	}
}
