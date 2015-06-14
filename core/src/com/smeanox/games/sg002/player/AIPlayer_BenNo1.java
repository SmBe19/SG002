package com.smeanox.games.sg002.player;

import com.badlogic.gdx.math.MathUtils;
import com.smeanox.games.sg002.world.GameObjectType;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * AI No1 by Benjamin
 *
 * @author Benjamin Schmid
 */
public class AIPlayer_BenNo1 extends AIPlayer {

	@Override
	protected void playAI() {
		HashMap<String, LinkedList<Integer>> positions = new HashMap<String, LinkedList<Integer>>();
		for (GameObjectType gameObjectType : GameObjectType.getAllGameObjectTypes()) {
			positions.put(gameObjectType.getId(), getMyPositions(gameObjectType));
		}

		GameObjectType[] military = new GameObjectType[]{GameObjectType.getGameObjectTypeById("infantry"),
				GameObjectType.getGameObjectTypeById("knight"), GameObjectType.getGameObjectTypeById("archer")};

		// we're still alive
		if (positions.get("townCenter").size() > 0) {
			// we need more villagers
			if (positions.get("villager").size() < 3) {
				int towncenter = positions.get("townCenter").get(0);
				int field = getFirstFreeProduceField(towncenter, GameObjectType.getGameObjectTypeById("villager"));
				if (field >= 0) {
					gameWorld.produce(extractX(towncenter), extractY(towncenter),
							extractX(field), extractY(field),
							GameObjectType.getGameObjectTypeById("villager"));
				}
				// build an army
			} else {
				for (Integer pos : positions.get("townCenter")) {
					if (MathUtils.randomBoolean(0.1f)) {
						continue;
					}

					int toProduce = MathUtils.random(2);
					int field = getFirstFreeProduceField(pos, military[toProduce]);
					if (field >= 0) {
						gameWorld.produce(extractX(pos), extractY(pos), extractX(field), extractY(field),
								military[toProduce]);
					}
				}
			}

			// move villagers / build something
			for (Integer pos : positions.get("villager")) {
				// build
				if (MathUtils.randomBoolean(0.1f)) {
					String toBuild = MathUtils.randomBoolean(0.1f) ? "townCenter" : "goldMine";
					int field = getFirstFreeProduceField(pos, GameObjectType.getGameObjectTypeById(toBuild));
					if (field >= 0) {
						gameWorld.produce(extractX(pos), extractY(pos), extractX(field), extractY(field),
								GameObjectType.getGameObjectTypeById(toBuild));
					}
					// move
				} else {
					int field = getRandomFreeMoveField(pos);
					if (field >= 0) {
						gameWorld.move(extractX(pos), extractY(pos), extractX(field), extractY(field));
					}
				}
			}

			// we have an emergency !!!
		} else {
			for (Integer pos : positions.get("villager")) {
				int field = getRandomFreeProduceField(pos, GameObjectType.getGameObjectTypeById("townCenter"));
				if (field >= 0) {
					gameWorld.produce(extractX(pos), extractY(pos), extractX(field), extractY(field),
							GameObjectType.getGameObjectTypeById("townCenter"));
				}
			}
		}

		// use the army
		for (GameObjectType aMilitary : military) {
			for (Integer pos : positions.get(aMilitary.getId())) {
				// attack
				if (MathUtils.randomBoolean(0.5f)) {
					int field = getRandomFightField(pos);
					if (field >= 0) {
						gameWorld.fight(extractX(pos), extractY(pos), extractX(field), extractY(field));
					}
					// move
				} else {
					int field = getRandomFreeMoveField(pos);
					if (field >= 0) {
						gameWorld.move(extractX(pos), extractY(pos), extractX(field), extractY(field));
					}
				}
			}
		}

		setFinishedPlaying();
	}
}
