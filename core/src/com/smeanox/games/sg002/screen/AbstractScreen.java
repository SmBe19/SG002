package com.smeanox.games.sg002.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.smeanox.games.sg002.screen.gui.AbstractGUIElement;
import com.smeanox.games.sg002.screen.gui.GUIElement;
import com.smeanox.games.sg002.screen.gui.Resizer;
import com.smeanox.games.sg002.util.Consts;

import java.util.LinkedList;

/**
 * Implement some methods most screens can use
 *
 * @author Benjamin Schmid
 */
public abstract class AbstractScreen implements Screen {
	/**
	 * list of all guielements on the screen
	 */
	protected LinkedList<GUIElement> guiElements;

	/**
	 * sprite batch
	 */
	protected SpriteBatch spriteBatch;

	/**
	 * camera used to draw the content
	 */
	protected Camera camera;
	/**
	 * Camera used to draw the gui
	 */
	private Camera guiCamera;
	private Vector2 vector2;
	private Vector3 vector3;

	/**
	 * Create a new instance
	 */
	public AbstractScreen() {
		guiElements = new LinkedList<GUIElement>();

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		guiCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		spriteBatch = new SpriteBatch();

		vector2 = new Vector2();
		vector3 = new Vector3();
	}

	/**
	 * Add a {@link GUIElement} to the screen
	 *
	 * @param guiElement the clickable
	 */
	protected void addGUIElement(GUIElement guiElement) {
		guiElements.add(guiElement);
	}

	/**
	 * Remove a {@link GUIElement} from the screen
	 *
	 * @param guiElement the clickable
	 */
	protected void removeGUIElement(GUIElement guiElement) {
		guiElements.remove(guiElement);
	}

	/**
	 * Unproject the given coordinates in screen space to world space
	 *
	 * @param x position in screen coordinates
	 * @param y position in screen coordinates
	 * @return position in world coordinates
	 */
	protected Vector2 unproject(float x, float y) {
		vector3.set(x, y, 0);
		vector3.set(camera.unproject(vector3));
		vector2.set(vector3.x, vector3.y);
		return vector2;
	}

	/**
	 * Unprojet the given coordinates in screen space to world space
	 *
	 * @param v2 position in screen coordinates
	 * @return position in world coordinates
	 */
	protected Vector2 unproject(Vector2 v2) {
		return unproject(v2.x, v2.y);
	}

	/**
	 * Unproject the given coordinates in screen space to world space for the GUI (using guicamera)
	 *
	 * @param x position in screen coordinates
	 * @param y position in screen coordinates
	 * @return position in world coordinates
	 */
	protected Vector2 unprojectGUI(float x, float y) {
		vector3.set(x, y, 0);
		vector3.set(guiCamera.unproject(vector3));
		vector2.set(vector3.x, vector3.y);
		return vector2;
	}

	/**
	 * Unproject the given coordinates in screen space to world space for the GUI (using guicamera)
	 *
	 * @param v2 position in screen coordinates
	 * @return position in world coordinates
	 */
	protected Vector2 unprojectGUI(Vector2 v2) {
		return unprojectGUI(v2.x, v2.y);
	}

	@Override
	public void show() {
	}

	/**
	 * Clear the screen using black
	 */
	protected void clearScreen() {
		Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	/**
	 * Update all GUI elements
	 *
	 * @param delta    The time in seconds since the last render.
	 * @param wasClick true if there was already a click in this frame.
	 * @return true if some gui element was clicked.
	 */
	protected boolean updateGUI(float delta, boolean wasClick) {
		boolean wasClicked = false;
		vector2 = unprojectGUI(Gdx.input.getX(), Gdx.input.getY());
		for (GUIElement guiElement : guiElements) {
			if (guiElement.updateClickable(vector2, wasClick || wasClicked)) {
				wasClicked = true;
			}
		}
		return wasClicked;
	}

	/**
	 * Render all GUI elements
	 *
	 * @param delta The time in seconds since the last render.
	 */
	protected void renderGUI(float delta) {
		spriteBatch.setProjectionMatrix(guiCamera.combined);
		spriteBatch.begin();
		for (GUIElement guiElement : guiElements) {
			guiElement.render(delta, spriteBatch);
		}
		spriteBatch.end();
		spriteBatch.setProjectionMatrix(camera.combined);
	}

	@Override
	public void resize(int width, int height) {
		Consts.updateScale();
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
		guiCamera.viewportWidth = width;
		guiCamera.viewportHeight = height;
		guiCamera.update();
		spriteBatch.setProjectionMatrix(camera.combined);

		for (GUIElement guiElement : guiElements) {
			guiElement.resize(width, height);
		}
	}

	/**
	 * layout the given objects
	 *
	 * @param toLayout      list of objects to layout
	 * @param cols          number of columns
	 * @param rows          number of rows
	 * @param orientationX  -1 - left; 0 - center; 1 - right
	 * @param orientationY  -1 - bottom; 0 - center; 1 - top
	 * @param startX        offset to the center of the screen relative to the size
	 * @param startY        offset to the center of the screen relative to the size
	 * @param elementWidth  width of one element
	 * @param elementHeight height of one element
	 * @param gapX          gap between two elements
	 * @param gapY          gab between two elements
	 */
	protected void layout(LinkedList<? extends AbstractGUIElement> toLayout,
						  final int cols, final int rows,
						  int orientationX, int orientationY,
						  final float startX, final float startY,
						  final float elementWidth, final float elementHeight,
						  final float gapX, final float gapY) {
		int aNum = 0;
		final float offsetPosX, offsetPosY;
		if (orientationX < 0) {
			// left
			offsetPosX = 0;
		} else if (orientationX > 0) {
			// right
			offsetPosX = -((cols * (elementWidth + gapX) - gapX));
		} else {
			// center
			offsetPosX = -((cols * (elementWidth + gapX) - gapX) / 2);
		}
		if (orientationY < 0) {
			// bottom
			offsetPosY = 0;
		} else if (orientationY > 0) {
			// top
			offsetPosY = -((rows * (elementHeight + gapY) - gapY));
		} else {
			// center
			offsetPosY = -((rows * (elementHeight + gapY) - gapY) / 2);
		}
		for (AbstractGUIElement e : toLayout) {
			final int aNumFinal = aNum;
			e.setResizer(new Resizer() {
				@Override
				public Rectangle getNewSize(float width, float height) {
					int aCol, aRow;
					aCol = aNumFinal % cols;
					aRow = aNumFinal / cols;
					float x, y, sWidth, sHeight;
					x = (offsetPosX + aCol * (elementWidth + gapX)) * Consts.devScaleX + startX * width;
					y = (offsetPosY + aRow * (elementHeight + gapY)) * Consts.devScaleY + startY * height;
					sWidth = elementWidth * Consts.devScaleX;
					sHeight = elementHeight * Consts.devScaleY;
					return new Rectangle(x, y, sWidth, sHeight);
				}
			});
			aNum++;
		}
	}

	/**
	 * move the camera
	 *
	 * @param x offset
	 * @param y offset
	 */
	protected void moveCamera(float x, float y) {
		camera.translate(x, y, 0);
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
	}

	/**
	 * set the camera position
	 *
	 * @param x position
	 * @param y position
	 */
	protected void setCameraPosition(float x, float y) {
		camera.position.set(x, y, 0);
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
	}

	/**
	 * set the camera position
	 *
	 * @param postition postition
	 */
	protected void setCameraPosition(Vector2 postition) {
		setCameraPosition(postition.x, postition.y);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

	}
}
