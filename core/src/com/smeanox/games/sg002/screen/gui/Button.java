package com.smeanox.games.sg002.screen.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.smeanox.games.sg002.util.BitmapFontRapper;

/**
 * A button
 *
 * @author Benjamin Schmid
 */
public class Button extends AbstractGUIElement {

	private Sprite sprite;
	private BitmapFontRapper font;
	private String text;
	private Color textColor;
	private Color backgroundColor;
	private Color backgroundColorHover;
	private Color backgroundColorInactive;
	private GlyphLayout glyphLayout;

	/**
	 * Create a new instance
	 */
	public Button() {
		super();
		glyphLayout = new GlyphLayout();
	}

	/**
	 * Create a new instance
	 *
	 * @param sprite                  the sprite used as background
	 * @param font                    the font used to display the text
	 * @param text                    the text to display
	 * @param textColor               the color of the texet
	 * @param backgroundColor         the color used to tint the background
	 * @param backgroundColorHover    the color used to tint the background when the mouse hovers over the button
	 * @param backgroundColorInactive the color used to tint the background when the button is inactive
	 */
	public Button(Sprite sprite, BitmapFontRapper font, String text, Color textColor,
				  Color backgroundColor, Color backgroundColorHover, Color backgroundColorInactive) {
		this();
		this.sprite = sprite;
		this.font = font;
		this.text = text;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
		this.backgroundColorHover = backgroundColorHover;
		this.backgroundColorInactive = backgroundColorInactive;
		if (sprite != null) {
			size.set(sprite.getWidth(), sprite.getHeight());
			position.set(sprite.getX(), sprite.getY());
		}
	}

	/**
	 * return the sprite. You should not manipulate position or size.
	 *
	 * @return the sprite
	 */
	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public BitmapFontRapper getFont() {
		return font;
	}

	public void setFont(BitmapFontRapper font) {
		this.font = font;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Color getBackgroundColorHover() {
		return backgroundColorHover;
	}

	public void setBackgroundColorHover(Color backgroundColorHover) {
		this.backgroundColorHover = backgroundColorHover;
	}

	public Color getBackgroundColorInactive() {
		return backgroundColorInactive;
	}

	public void setBackgroundColorInactive(Color backgroundColorInactive) {
		this.backgroundColorInactive = backgroundColorInactive;
	}

	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		if (sprite != null) {
			sprite.setSize(width, height);
		}
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		if (sprite != null) {
			sprite.setPosition(x, y);
		}
	}

	@Override
	public void setCenter(float x, float y) {
		super.setCenter(x, y);
		if (sprite != null) {
			sprite.setCenter(x, y);
		}
	}

	@Override
	public void render(float delta, SpriteBatch spriteBatch) {
		if (!visible) {
			return;
		}
		if (sprite != null) {
			sprite.setColor(active ? (boundingBox.contains(lastTouchPos)
					? backgroundColorHover : backgroundColor) : backgroundColorInactive);
			sprite.draw(spriteBatch);
		}
		if (font != null) {
			font.bitmapFont.setColor(textColor);
			glyphLayout.setText(font.bitmapFont, text);
			font.bitmapFont.draw(spriteBatch, glyphLayout,
					boundingBox.getX() + (boundingBox.getWidth() - glyphLayout.width) / 2f,
					boundingBox.getY() + (boundingBox.getHeight() + glyphLayout.height) / 2f);
		}
	}
}
