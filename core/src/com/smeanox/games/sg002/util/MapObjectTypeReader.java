package com.smeanox.games.sg002.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.smeanox.games.sg002.world.GameObjectType;
import com.smeanox.games.sg002.world.MapObjectType;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Reads a given XML file and creates the mapObjectTypes
 *
 * @author Fabian Lyck
 */
public class MapObjectTypeReader {
	private MapObjectTypeReader() {
	}

	/**
	 * Reads all the MapObjectTypes from the given file
	 *
	 * @param file file to read from
	 * @return list with all the read ids
	 */
	public static ArrayList<String> readMapObjectTypes(FileHandle file){
		ArrayList<String> ids = new ArrayList<String>();
		XmlReader reader = new XmlReader();
		try {
			XmlReader.Element root = reader.parse(file);
			for(XmlReader.Element mapObjectType : root.getChildByName("MapObjectTypes").getChildrenByName("MapObjectType")){

				String id = mapObjectType.getAttribute("id");
				String textureName = mapObjectType.getAttribute("texture");
				Set<GameObjectType> allowedGameObjects = new HashSet();
				try {
					Array<XmlReader.Element> allowedIds = mapObjectType.getChildByName("AllowedGameObjects").getChildrenByName("GameObject");
					for (XmlReader.Element e : allowedIds) {
						allowedGameObjects.add(GameObjectType.getGameObjectTypeById(e.getAttribute("id")));
					}
				} catch (NullPointerException e) {
					for (GameObjectType got : GameObjectType.getAllGameObjectTypes()) {
						allowedGameObjects.add(got);
					}
				}
				try {
					Array<XmlReader.Element> allowedIds = mapObjectType.getChildByName("ForbiddenGameObjects").getChildrenByName("GameObject");
					for (XmlReader.Element e : allowedIds) {
						allowedGameObjects.remove(GameObjectType.getGameObjectTypeById(e.getAttribute("id")));
					}
				} catch (NullPointerException ignored) {
				}

				MapObjectType aMapObjectType = new MapObjectType(textureName, id, allowedGameObjects);
				if(!ProgramArguments.noGUI) {
					Assets.addToLoadQueue(textureName, Texture.class);
				}
				ids.add(id);

				if (mapObjectType.getBooleanAttribute("default", false)) {
					MapObjectType.setDefaultMapObjectType(aMapObjectType);
				}
			}

		} catch (IOException e) {
			System.out.println("Config file (MapObjectTypes) not found: " + file.name());
			e.printStackTrace();
		} catch (NullPointerException e){
			System.out.println("Config file (MapObjectTypes) is bad: " + file.name());
			e.printStackTrace();
		}
		return ids;
	}

}
