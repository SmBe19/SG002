package com.smeanox.games.sg002.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

/**
 * Manages localized strings
 *
 * @author Benjamin Schmid
 */
public class Language {

	private Language() {
	}

	/**
	 * the i18n bundle for strings
	 */
	private static I18NBundle strings;

	/**
	 * Load the i18n bundle for strings with the default locale
	 */
	public static void loadStrings() {
		loadStrings(Locale.getDefault());
	}

	/**
	 * Load the i18n bundle for strings with the given locale
	 *
	 * @param locale the locale to load
	 */
	public static void loadStrings(Locale locale) {
		FileHandle baseFileHandle = Gdx.files.internal("i18n/Strings");
		strings = I18NBundle.createBundle(baseFileHandle, locale);
	}

	/**
	 * Load the i18n bundle for strings with the given locale
	 * <br>
	 * Does not use {@link Gdx#files} and will only work on desktop
	 *
	 * @param locale the locale to load
	 */
	public static void loadStringsForHeadless(Locale locale) {
		strings = I18NBundle.createBundle(new FileHandle("i18n/Strings"), locale);
	}

	/**
	 * Return the i18n bundle for strings
	 *
	 * @return the i18n bundle for strings
	 */
	public static I18NBundle getStrings() {
		return strings;
	}
}
