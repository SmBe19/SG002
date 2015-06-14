package com.smeanox.games.sg002.world;

import com.badlogic.gdx.graphics.Texture;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Fabian Lyck
 */
public class MapObjectType {

	private static Map<String, MapObjectType> idmap = new HashMap<String, MapObjectType>();

	private static MapObjectType defaultMapObjectType;

	private Texture texture;
	private String textureName;

	private Set<GameObjectType> allowedGameObjectTypes;

	public final String id;

	/**
	 * Create a new instance
	 *
	 * @param textureName            the name of the texture to use
	 * @param id                     id of this type
	 * @param allowedGameObjectTypes Set of all allowed GameObjectTypes on this typo of MapObject
	 */
	public MapObjectType(String textureName, String id, Set<GameObjectType> allowedGameObjectTypes) {
		this.id = id;
		this.textureName = textureName;
		this.allowedGameObjectTypes = allowedGameObjectTypes;

		idmap.put(id, this);
	}

	public String getTextureName() {
		System.out.println(textureName);
		return textureName;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public Texture getTexture() {
		return texture;
	}

	/**
	 * Checks whether a gameObjectType is allowed to be placed on this map tile
	 *
	 * @param gameObjectType the GameObjectType to check
	 * @return true if it is allowed
	 */
	public boolean isGameObjectTypeAllowed(GameObjectType gameObjectType) {
		return allowedGameObjectTypes.contains(gameObjectType);
	}

	/**
	 * Returns all gameObjectTypes which are allowed to be placed on this map tile
	 *
	 * @return collection of allowed gameObjectTypes, direct reference - DO NOT MODIFY
	 */
	public Collection<GameObjectType> getAllowedGameObjectTypes() {
		return allowedGameObjectTypes;
	}

	/**
	 * Get a specific MapObjectType identified by their Id
	 *
	 * @param id id of the MapObjectType
	 * @return the MapObjectType or null if it doesn't exist
	 */
	public static MapObjectType getMapObjectTypeById(String id) {
		return idmap.get(id);
	}

	/**
	 * get all loaded mapObjectTypes
	 *
	 * @return Collection of mapObjectTypes
	 */
	public static Collection<MapObjectType> getMapObjectTypes() {
		return idmap.values();
	}

	public static void setDefaultMapObjectType(MapObjectType mot) {
		defaultMapObjectType = mot;
	}

	public static MapObjectType getDefaultMapObjectType() {
		return defaultMapObjectType;
	}

}
