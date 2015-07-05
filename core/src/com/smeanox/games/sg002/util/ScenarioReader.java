package com.smeanox.games.sg002.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.smeanox.games.sg002.world.Scenario;

import com.smeanox.games.sg002.data.Point;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Read a given XML file and create the Scenarios
 *
 * @author Benjamin Schmid
 */
public class ScenarioReader {
	private ScenarioReader() {
	}

	/**
	 * Read all the Scenarios from the given file
	 *
	 * @param file file to read from
	 * @return list with all the read ids
	 */
	public static ArrayList<String> readScenarios(FileHandle file) {
		ArrayList<String> ids = new ArrayList<String>();
		XmlReader reader = new XmlReader();
		try {
			XmlReader.Element root = reader.parse(file);
			for(XmlReader.Element scenario : root.getChildByName("Scenarios").getChildrenByName("Scenario")){

				if (scenario.getChildByName("GoldPos") != null && scenario.getChildByName("StartPos") != null){

					List<Point> goldPos = new ArrayList<Point>();
					for (XmlReader.Element pos : scenario.getChildByName("GoldPos").getChildrenByName("Gold")){
						goldPos.add(new Point(pos.getIntAttribute("x"),pos.getIntAttribute("y")));
					}
					List<Point> startPos = new ArrayList<Point>();
					for (XmlReader.Element pos : scenario.getChildByName("StartPos").getChildrenByName("Start")){
						startPos.add(new Point(pos.getIntAttribute("x"),pos.getIntAttribute("y")));
					}

					new Scenario(
							scenario.getAttribute("id"),
							Language.getStrings().get(scenario.getAttribute("name")),
							scenario.getIntAttribute("startMoney"),
							scenario.getIntAttribute("maxPlayerCount"),
							scenario.getIntAttribute("mapSizeX"),
							scenario.getIntAttribute("mapSizeY"),
							scenario.getBooleanAttribute("walkDiagonal"),
							scenario.getIntAttribute("startGameObjectMinDistance"),
							Long.parseLong(scenario.getAttribute("seed")),
							scenario.getIntAttribute("maxGold"),
							scenario.getBooleanAttribute("multipleActionsPerObject"),
							goldPos.toArray(new Point[goldPos.size()]),
							startPos.toArray(new Point[startPos.size()]));
				}else {
					new Scenario(
							scenario.getAttribute("id"),
							Language.getStrings().get(scenario.getAttribute("name")),
							scenario.getIntAttribute("startMoney"),
							scenario.getIntAttribute("maxPlayerCount"),
							scenario.getIntAttribute("mapSizeX"),
							scenario.getIntAttribute("mapSizeY"),
							scenario.getBooleanAttribute("walkDiagonal"),
							scenario.getIntAttribute("startGameObjectMinDistance"),
							Long.parseLong(scenario.getAttribute("seed")),
							scenario.getIntAttribute("maxGold"),
							scenario.getBooleanAttribute("multipleActionsPerObject"));
				}
				ids.add(scenario.getAttribute("id"));
			}
		} catch (IOException e) {
			System.out.println("Config file (GameObjectTypes) not found: " + file.name());
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("Config file (GameObjectTypes) is bad: " + file.name());
			e.printStackTrace();
		}
		return ids;
	}
}
