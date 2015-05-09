/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Benjamin Schmid
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.smeanox.games.sg002.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.smeanox.games.sg002.screen.gui.Button;
import com.smeanox.games.sg002.screen.gui.ClickHandler;
import com.smeanox.games.sg002.screen.gui.Resizer;
import com.smeanox.games.sg002.util.Assets;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.util.Language;

/**
 * Menu screen
 * @author Benjamin Schmid
 */
public class MenuScreen extends AbstractScreen {

	/** Constructor */
	public MenuScreen(){
		super();

		// build menu
		Button b;
		b = new Button(new Sprite(Assets.button), Assets.liberationMedium, Language.getStrings().get("menu.startGame"), Color.BLACK);
		b.setResizer(new Resizer() {
			@Override
			public Rectangle getNewSize(float width, float height) {
				return new Rectangle(0 - 200 * Consts.devScaleX, height * 0.1f - 50 * Consts.devScaleY, 400 * Consts.devScaleX, 100 * Consts.devScaleY);
			}
		});
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				ScreenManager.showGame();
			}
		});
		addGUIElement(b);

		b = new Button(null, Assets.liberationLarge, Language.getStrings().get("game.name"), Color.ORANGE);
		b.setResizer(new Resizer() {
			@Override
			public Rectangle getNewSize(float width, float height) {
				return new Rectangle(0 - 200 * Consts.devScaleX, height * 0.4f - 50 * Consts.devScaleY, 400 * Consts.devScaleX, 100 * Consts.devScaleY);
			}
		});
		addGUIElement(b);
	}

	/**
	 * Called when the screen should render itself.
	 * @param delta The time in seconds since the last render.
	 */
	@Override
	public void render(float delta) {
		clearScreen();
		updateGUI(delta);
		renderGUI(delta);
	}
}
