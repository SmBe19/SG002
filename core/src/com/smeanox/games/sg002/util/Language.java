package com.smeanox.games.sg002.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;
import java.util.MissingResourceException;

/**
 * Manages localized strings
 *
 * @author Benjamin Schmid
 */
public class Language {

	private Language(){
	}

	/** the i18n bundel for strings */
	private static I18NBundle strings;

	/**
	 * Loads the i18n bundle for strings with the default locale
	 */
	public static void loadStrings(){
		loadStrings(Locale.getDefault());
	}

	/**
	 * Loads the i18n bundle for strings with the given locale
	 * @param locale
	 */
	public static void loadStrings(Locale locale){
		FileHandle baseFileHandle = Gdx.files.internal("i18n/Strings");
		strings = I18NBundle.createBundle(baseFileHandle, locale);
	}

	/**
	 * Returns the i18n bundle for strings
	 * @return the i18n bundle for strings
	 */
	public static I18NBundle getStrings(){
		return strings;
	}
}
