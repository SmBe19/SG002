package com.smeanox.games.sg002.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.smeanox.games.sg002.world.Action;
import com.smeanox.games.sg002.world.GameController;

import java.io.IOException;
import java.util.HashMap;

/**
 * a player that can play the game
 * @author Benjamin Schmid
 */
public abstract class Player {
	protected GameController gameController;

	protected static HashMap<Integer, Player> idToPlayer = new HashMap<Integer, Player>();

	protected int id = -1;
	protected String name;
	protected int money;
	protected boolean isPlaying;
	protected Color color;
	protected boolean showGUI;

	public final void startPlaying(){
		isPlaying = true;
		play();
	}

	protected final void endPlaying(){
		isPlaying = false;
		gameController.finishedRound();
	}

	protected abstract void play();

	public abstract void update(float delta);

	public boolean proposeAction(Action action){
		return false;
	}

	public boolean proposeEndPlaying(){
		return false;
	}

	public final void save(XmlWriter writer) throws IOException {
		writer.attribute("class", this.getClass().getName());
		writer.attribute("id", id);
		writer.attribute("name", name);
		writer.attribute("money", money);
		writer.attribute("color", color);
		writer.attribute("showGUI", showGUI);
		saveImpl(writer);
	}

	protected void saveImpl(XmlWriter writer) throws IOException{
	}

	public final void load(XmlReader.Element reader){
		id = reader.getIntAttribute("id");
		name = reader.getAttribute("name");
		money = reader.getIntAttribute("money");
		color = Color.valueOf(reader.getAttribute("color"));
		showGUI = reader.getBooleanAttribute("showGUI");
		loadImpl(reader);
	}

	protected void loadImpl(XmlReader.Element reader){
	}

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
		if(idToPlayer.containsKey(this.id)){
			idToPlayer.remove(this.id);
		}
		this.id = id;
		idToPlayer.put(id, this);
	}

	public final String getName(){
		return name;
	}

	public final void setName(String name){
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

	public static Player getPlayerById(int id){
		return idToPlayer.get(id);
	}

	public static void resetPlayerIds(){
		idToPlayer.clear();
	}
}
