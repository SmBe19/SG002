package com.smeanox.games.sg002.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.smeanox.games.sg002.world.Action;
import com.smeanox.games.sg002.world.GameController;
import com.smeanox.games.sg002.world.GameWorld;

import java.io.IOException;
import java.util.HashMap;

/**
 * a player that can play the game
 *
 * @author Benjamin Schmid
 */
public abstract class Player {
	protected GameController gameController;
	protected GameWorld gameWorld;

	protected static HashMap<Integer, Player> idToPlayer = new HashMap<Integer, Player>();

	protected int id = -1;
	protected String name;
	protected int money;
	protected boolean isPlaying;
	protected Color color;
	protected boolean showGUI;

	/**
	 * Inform the player that he can start playing
	 */
	public final void startPlaying() {
		isPlaying = true;
		gameWorld = gameController.getGameWorld();
		play();
	}

	/**
	 * Inform the gameController that the player finished playing
	 */
	protected final void endPlaying() {
		isPlaying = false;
		gameController.finishedRound();
	}

	/**
	 * Perform the moves for this round
	 */
	protected abstract void play();

	/**
	 * Update the Player
	 *
	 * @param delta time passed since last update
	 */
	public abstract void update(float delta);

	/**
	 * Propose the action to be performed (e.g. the GUI can propose an action)
	 *
	 * @param action the action to perform
	 * @return true if the action was performed
	 */
	public boolean proposeAction(Action action) {
		return false;
	}

	/**
	 * Propose to end the round (e.g. the GUI can propose this)
	 *
	 * @return true if the it was performed
	 */
	public boolean proposeEndPlaying() {
		return false;
	}

	/**
	 * Save the Player to the given writer
	 *
	 * @param writer the writer to save to
	 * @throws IOException
	 */
	public final void save(XmlWriter writer) throws IOException {
		writer.attribute("class", this.getClass().getName());
		writer.attribute("id", id);
		writer.attribute("name", name);
		writer.attribute("money", money);
		writer.attribute("color", color);
		writer.attribute("showGUI", showGUI);
		saveImpl(writer);
	}

	/**
	 * Save implementation specific things
	 *
	 * @param writer the writer to save to
	 * @throws IOException
	 */
	protected void saveImpl(XmlWriter writer) throws IOException {
	}

	/**
	 * Load the Player from the given reader
	 *
	 * @param reader the reader to read from
	 */
	public final void load(XmlReader.Element reader) {
		id = reader.getIntAttribute("id");
		name = reader.getAttribute("name");
		money = reader.getIntAttribute("money");
		color = Color.valueOf(reader.getAttribute("color"));
		showGUI = reader.getBooleanAttribute("showGUI");
		loadImpl(reader);
	}

	/**
	 * Load implementation specific things
	 *
	 * @param reader the reader to read from
	 */
	protected void loadImpl(XmlReader.Element reader) {
	}

	/**
	 * Create a Player instance of the type defined in the saved XML
	 *
	 * @param reader the reder to read from
	 * @return a new instance of the defined class
	 * @throws IOException
	 */
	public static Player loadStatic(XmlReader.Element reader) throws IOException {
		try {
			Player player = (Player) ClassReflection.newInstance(ClassReflection.forName(reader.getAttribute("class")));
			return player;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public void setGameController(GameController gameController) {
		this.gameController = gameController;
	}

	public final int getId() {
		return id;
	}

	public final void setId(int id) {
		if (idToPlayer.containsKey(this.id)) {
			idToPlayer.remove(this.id);
		}
		this.id = id;
		idToPlayer.put(id, this);
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final int getMoney() {
		return money;
	}

	public final void setMoney(int money) {
		this.money = money;
	}

	public final void addMoney(int amount) {
		money += amount;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isShowGUI() {
		return showGUI;
	}

	public void setShowGUI(boolean showGUI) {
		this.showGUI = showGUI;
	}

	/**
	 * Return the player associated to the given id
	 *
	 * @param id the id to search
	 * @return the player with the given id
	 */
	public static Player getPlayerById(int id) {
		return idToPlayer.get(id);
	}

	/**
	 * Reset the list of ids of players
	 */
	public static void resetPlayerIds() {
		idToPlayer.clear();
	}
}
