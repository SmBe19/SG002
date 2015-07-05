package com.smeanox.games.sg002.player;

import com.badlogic.gdx.math.MathUtils;
import com.smeanox.games.sg002.data.Point;
import com.smeanox.games.sg002.world.GameObjectType;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * AI No1 by Benjamin
 *
 * @author Benjamin Schmid
 */
public class AIPlayer_BenNo1 extends LocalAIPlayer {

	@Override
	protected void playAI() {
		HashMap<String, LinkedList<Point>> positions = new HashMap<String, LinkedList<Point>>();
		for (GameObjectType gameObjectType : GameObjectType.getAllGameObjectTypes()) {
			positions.put(gameObjectType.getId(), getMyPositions(gameObjectType));
		}

		GameObjectType[] military = new GameObjectType[]{
				GameObjectType.getGameObjectTypeById("villager"),
				GameObjectType.getGameObjectTypeById("infantry"),
				GameObjectType.getGameObjectTypeById("knight"),
				GameObjectType.getGameObjectTypeById("archer")
		};

		// we're still alive
		if (positions.get("townCenter").size() > 0) {
			// we need more villagers
			for (Point pos : positions.get("townCenter")) {
				if (MathUtils.randomBoolean(0.15f)) {
					continue;
				}

				int toProduce = MathUtils.random(military.length - 1);
				Point field = getFirstFreeProduceField(pos, military[toProduce]);
				if (field != null) {
					gameWorld.produce(pos.x, pos.y, field.x, field.y, military[toProduce]);
				}
			}

			// move villagers / build something
			for (Point pos : positions.get("villager")) {
				// build
				Point field = getFirstFreeProduceField(pos, GameObjectType.getGameObjectTypeById("goldMine"));
				if (MathUtils.randomBoolean(field != null ? 0.95f : 0.1f)) {
					String toBuild = MathUtils.randomBoolean(0.1f) ? "townCenter" : "goldMine";
					field = getFirstFreeProduceField(pos, GameObjectType.getGameObjectTypeById(toBuild));
					if (field != null) {
						gameWorld.produce(pos.x, pos.y, field.x, field.y,
								GameObjectType.getGameObjectTypeById(toBuild));
					}
					// move
				} else {
					field = getRandomFreeMoveField(pos);
					if (field != null) {
						gameWorld.move(pos.x, pos.y, field.x, field.y);
					}
				}
			}

			// we have an emergency !!!
		} else {
			for (Point pos : positions.get("villager")) {
				Point field = getRandomFreeProduceField(pos, GameObjectType.getGameObjectTypeById("townCenter"));
				if (field != null) {
					gameWorld.produce(pos.x, pos.y, field.x, field.y,
							GameObjectType.getGameObjectTypeById("townCenter"));
				}
			}
		}

		// use the army
		for (GameObjectType aMilitary : military) {
			for (Point pos : positions.get(aMilitary.getId())) {
				// attack
				if (MathUtils.randomBoolean(0.5f)) {
					Point field = getRandomFightField(pos);
					if (field != null) {
						gameWorld.fight(pos.x, pos.y, field.x, field.y);
					}
					// move
				} else {
					Point field = getRandomFreeMoveField(pos);
					if (field != null) {
						gameWorld.move(pos.x, pos.y, field.x, field.y);
					}
				}
			}
		}

		setFinishedPlaying();
	}
}
