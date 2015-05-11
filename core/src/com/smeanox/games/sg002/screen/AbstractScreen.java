package com.smeanox.games.sg002.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.smeanox.games.sg002.screen.gui.GUIElement;
import com.smeanox.games.sg002.util.Consts;

import java.util.LinkedList;

/**
 * Implements some methods most screens can use
 * @author Benjamin Schmid
 */
public abstract class AbstractScreen implements Screen {
	/** list of all guielements on the screen */
	protected LinkedList<GUIElement> guiElements;

	/** sprite batch */
	protected SpriteBatch spriteBatch;

	protected Camera camera;
	private Camera guiCamera;
	private Vector2 vector2;
	private Vector3 vector3;

	/** Constructor */
	public AbstractScreen(){
		guiElements = new LinkedList<GUIElement>();

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		guiCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		spriteBatch = new SpriteBatch();

		vector2 = new Vector2();
		vector3 = new Vector3();
	}

	/**
	 * Adds a {@link GUIElement} to the screen
	 * @param guiElement the clickable
	 */
	protected void addGUIElement(GUIElement guiElement){
		guiElements.add(guiElement);
	}

	/**
	 * Removes a {@link GUIElement} from the screen
	 * @param guiElement the clickable
	 */
	protected void removeGUIElement(GUIElement guiElement){
		guiElements.remove(guiElement);
	}

	protected Vector2 unproject(float x, float y){
		vector3.set(x, y, 0);
		vector3.set(camera.unproject(vector3));
		vector2.set(vector3.x, vector3.y);
		return vector2;
	}

	protected Vector2 unproject(Vector2 v2){
		return unproject(v2.x, v2.y);
	}

	protected Vector2 unprojectGUI(float x, float y){
		vector3.set(x, y, 0);
		vector3.set(guiCamera.unproject(vector3));
		vector2.set(vector3.x, vector3.y);
		return vector2;
	}

	protected Vector2 unprojectGUI(Vector2 v2){
		return unprojectGUI(v2.x, v2.y);
	}

	/**
	 * Called when this screen becomes the current screen for a Game.
	 */
	@Override
	public void show() {
	}

	/**
	 * Clears the screen using black
	 */
	protected void clearScreen(){
		Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	/**
	 * Updates all GUI elements
	 * @param delta The time in seconds since the last render.
	 * @param wasClick true if there was already a click in this frame.
	 * @return true if some gui element was clicked.
	 */
	protected boolean updateGUI(float delta, boolean wasClick){
		boolean wasClicked = false;
		vector2 = unprojectGUI(Gdx.input.getX(), Gdx.input.getY());
		for(GUIElement guiElement : guiElements){
			if(guiElement.updateClickable(vector2, wasClick || wasClicked)){
				wasClicked = true;
			}
		}
		return wasClicked;
	}

	/**
	 * Renders all GUI elements
	 * @param delta The time in seconds since the last render.
	 */
	protected void renderGUI(float delta){
		spriteBatch.setProjectionMatrix(guiCamera.combined);
		spriteBatch.begin();
		for(GUIElement guiElement : guiElements){
			guiElement.render(delta, spriteBatch);
		}
		spriteBatch.end();
		spriteBatch.setProjectionMatrix(camera.combined);
	}

	/**
	 * Called when the Application is resized.
	 * @param width
	 * @param height
	 */
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

		for(GUIElement guiElement : guiElements){
			guiElement.resize(width, height);
		}
	}

	/**
	 * moves the camera
	 * @param x offset
	 * @param y offset
	 */
	protected void moveCamera(float x, float y){
		camera.translate(x, y, 0);
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
	}

	protected void setCameraPosition(float x, float y){
		camera.position.set(x, y, 0);
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
	}

	/**
	 * Called when the Application is paused, usually when it's not active or visible on screen.
	 */
	@Override
	public void pause() {

	}

	/**
	 * Called when the Application is resumed from a paused state, usually when it regains focus.
	 */
	@Override
	public void resume() {

	}

	/**
	 * Called when this screen is no longer the current screen for a Game.
	 */
	@Override
	public void hide() {

	}

	/**
	 * Called when this screen should release all resources.
	 */
	@Override
	public void dispose() {

	}
}
