package com.smeanox.games.sg002.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.XmlReader;
import com.smeanox.games.sg002.world.GameObjectType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Read a given XML file and create the gameObjectTypes
 *
 * @author Benjamin Schmid
 */
public class GameObjectTypeReader {
	private GameObjectTypeReader() {
	}

	/**
	 * Read all the GameObjectTypes from the given file
	 *
	 * @param file file to read from
	 * @return list with all the read ids
	 */
	public static ArrayList<String> readGameObjectTypes(FileHandle file) {
		ArrayList<String> ids = new ArrayList<String>();
		HashMap<GameObjectType, XmlReader.Element> damageTables = new HashMap<GameObjectType, XmlReader.Element>();
		HashMap<GameObjectType, XmlReader.Element> canProduces = new HashMap<GameObjectType, XmlReader.Element>();
		XmlReader reader = new XmlReader();
		try {
			XmlReader.Element root = reader.parse(file);
			for (XmlReader.Element gameObjectType : root.getChildByName("GameObjectTypes").getChildrenByName("GameObjectType")) {
				XmlReader.Element damageTable = gameObjectType.getChildByName("DamageTable");
				XmlReader.Element canProduce = gameObjectType.getChildByName("CanProduce");

				GameObjectType aGameObjectType = readGameObjectType(gameObjectType);
				damageTables.put(aGameObjectType, damageTable);
				canProduces.put(aGameObjectType, canProduce);
				if (!Consts.headlessMode) {
					Assets.addToLoadQueue(gameObjectType.getAttribute("texture"), Texture.class);
				}
				ids.add(gameObjectType.getAttribute("id"));

				if (gameObjectType.getBooleanAttribute("start", false)) {
					GameObjectType.setStartGameObjectType(aGameObjectType);
				}
			}

			for (Map.Entry<GameObjectType, XmlReader.Element> entry : damageTables.entrySet()) {
				if (entry.getKey().isCanFight()) {
					for (XmlReader.Element damageTableEntry : entry.getValue().getChildrenByName("Damage")) {
						entry.getKey().addDamageTableEntry(
								GameObjectType.getGameObjectTypeById(
										damageTableEntry.getAttribute("gameObjectType")),
								damageTableEntry.getIntAttribute("value"));
					}
				}
			}

			for (Map.Entry<GameObjectType, XmlReader.Element> entry : canProduces.entrySet()) {
				if (entry.getKey().isCanProduce()) {
					for (XmlReader.Element canProduceEntry : entry.getValue().getChildrenByName("Produce")) {
						entry.getKey().addCanProduceEntry(
								GameObjectType.getGameObjectTypeById(
										canProduceEntry.getAttribute("gameObjectType")));
					}
				}
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

	/**
	 * Read a single GameObjectType
	 *
	 * @param element the Element to read from
	 * @return the read GameObjectType
	 */
	private static GameObjectType readGameObjectType(XmlReader.Element element) {
		XmlReader.Element hp = element.getChildByName("HP");
		XmlReader.Element value = element.getChildByName("Value");
		XmlReader.Element radius = element.getChildByName("Radius");
		XmlReader.Element damageTable = element.getChildByName("DamageTable");
		XmlReader.Element canProduce = element.getChildByName("CanProduce");

		return new GameObjectType(
				element.getIntAttribute("externalId"),
				element.getAttribute("id"),
				Language.getStrings().get(element.getAttribute("name")),
				element.getAttribute("texture"),
				hp.getIntAttribute("defaultHP"),
				value.getIntAttribute("value"),
				value.getIntAttribute("valuePerRound"),
				value.getIntAttribute("valueOnDestruction"),
				radius.getIntAttribute("radiusWalkMin"),
				radius.getIntAttribute("radiusWalkMax"),
				radius.getIntAttribute("radiusProduceMin"),
				radius.getIntAttribute("radiusProduceMax"),
				radius.getIntAttribute("radiusFightMin"),
				radius.getIntAttribute("radiusFightMax"),
				damageTable.getBooleanAttribute("canFight"),
				new HashMap<GameObjectType, Integer>(),
				canProduce.getBooleanAttribute("canProduce"),
				new ArrayList<GameObjectType>());
	}
}
