package com.smeanox.games.sg002.player;

import com.smeanox.games.sg002.data.Point;
import com.smeanox.games.sg002.log.GameLogger;
import com.smeanox.games.sg002.screen.ScreenManager;
import com.smeanox.games.sg002.util.ConfigFileUtil;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.util.ProgramArguments;
import com.smeanox.games.sg002.world.Action;
import com.smeanox.games.sg002.world.GameController;
import com.smeanox.games.sg002.world.GameObjectType;
import com.smeanox.games.sg002.world.Scenario;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;

/**
 * Load a replay
 *
 * @author Benjamin Schmid
 */
public class ReplayLoader extends Thread {

	GameController result;

	@Override
	public void run() {
		result = null;

		GameLogger logger = GameLogger.createDummyLogger();
		logger.setPrintStdOut(ProgramArguments.printStdOut);
		logger.setPrintStdErr(ProgramArguments.printStdErr);

		File replayFile = new File (ProgramArguments.replay);
		try {
			LinkedList<String> lines = ConfigFileUtil.readAllLines(replayFile);

			String[] configLine = lines.get(0).split(" ");
			String[] playerNames = lines.get(1).split(" ");

			int playerCount = Integer.parseInt(configLine[0]);
			int goldCount = Integer.parseInt(configLine[4]);

			Point[] playerPos = new Point[playerCount];
			Point[] goldPos = new Point[goldCount];

			for (int i = 0; i < goldCount; i++) {
				String[] parts = lines.get(i + 2).split(" ");
				goldPos[i] = new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
			}

			for (int i = 0; i < playerCount; i++) {
				String[] parts = lines.get(i + goldCount + 2).split(" ");
				playerPos[i] = new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
			}

			LinkedList<LinkedList<LinkedList<Action>>> actions = new LinkedList<LinkedList<LinkedList<Action>>>();
			for (int i = 0; i < playerCount; i++) {
				actions.add(new LinkedList<LinkedList<Action>>());
			}

			int aPlayer = 0;
			for (int i = playerCount + goldCount + 2; i < lines.size(); i++) {
				if(Consts.NEXT_ROUND_ID.equals(lines.get(i))){
					if(i < lines.size() - 1){
						aPlayer = Integer.parseInt(lines.get(i + 1));
						i++;
						actions.get(aPlayer).add(new LinkedList<Action>());
					}
					continue;
				}

				String[] parts = lines.get(i).split(" ");
				Action action = new Action();
				int[] points = new int[4];
				for (int j = 0; j < 4; j++) {
					points[j] = Integer.parseInt(parts[j+1]);
				}
				action.startX = points[0];
				action.startY = points[1];
				action.endX = points[2];
				action.endY = points[3];
				if(Consts.MOVE_ID.equals(parts[0])){
					action.actionType = Action.ActionType.MOVE;
				} else if (Consts.FIGHT_ID.equals(parts[0])){
					action.actionType = Action.ActionType.FIGHT;
				} else if (Consts.PRODUCE_ID.equals(parts[0])){
					action.actionType = Action.ActionType.PRODUCE;
					int gameObjectTypeId = Integer.parseInt(parts[5]);
					for (GameObjectType gameObjectType : GameObjectType.getAllGameObjectTypes()) {
						if (gameObjectType.getExternalId() == gameObjectTypeId) {
							action.produceGameObjectType = gameObjectType;
							break;
						}
					}
				}

				actions.get(aPlayer).getLast().add(action);
			}

			Scenario replayScenario = new Scenario("replay", "replay", Integer.parseInt(configLine[1]),
					playerCount, Integer.parseInt(configLine[2]), Integer.parseInt(configLine[3]),
					Consts.walkDiagonal, Consts.startGameObjectMinDistance, Consts.seed,
					goldCount, Consts.multipleActionsPerObject, goldPos, playerPos);

			GameController gameController = new GameController(replayScenario, logger);

			for (int i = 0; i < playerCount; i++) {
				ReplayPlayer player;
				player = new ReplayPlayer();
				gameController.addPlayer(player);
				player.setColor(Consts.playerColors[i % Consts.playerColors.length]);
				player.setShowGUI(false);
				player.setName(playerNames[i]);

				player.setActiveRound(0);
				player.setActions(actions.get(i));
			}

			result = gameController;
		} catch (FileNotFoundException ignored) {
		}
	}

	public GameController getResult() {
		return result;
	}
}
