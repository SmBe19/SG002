package com.smeanox.games.sg002.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.smeanox.games.sg002.world.Scenario;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Reads a given XML file and creates the Scenarios
 * @author Benjamin Schmid
 */
public class ScenarioReader {
	private ScenarioReader(){
	}

	/**
	 * Reads all the Scenarios from the given file
	 * @param file file to read from
	 * @return list with all the read ids
	 */
	public static ArrayList<String> readScenarios(FileHandle file){
		ArrayList<String> ids = new ArrayList<String>();
		XmlReader reader = new XmlReader();
		try {
			XmlReader.Element root = reader.parse(file);
			for(XmlReader.Element scenario : root.getChildByName("Scenarios").getChildrenByName("Scenario")){
				new Scenario(
						scenario.getAttribute("id"),
						Language.getStrings().get(scenario.getAttribute("name")),
						scenario.getIntAttribute("startMoney"),
						scenario.getIntAttribute("maxPlayerCount"),
						scenario.getIntAttribute("mapSizeX"),
						scenario.getIntAttribute("mapSizeY"),
						scenario.getBooleanAttribute("walkDiagonal"),
						scenario.getIntAttribute("startGameObjectMinDistance"));
				ids.add(scenario.getAttribute("id"));
			}
		} catch (IOException e) {
			System.out.println("Config file (GameObjectTypes) not found: " + file.name());
			e.printStackTrace();
		} catch (NullPointerException e){
			System.out.println("Config file (GameObjectTypes) is bad: " + file.name());
			e.printStackTrace();
		}
		return ids;
	}
}
