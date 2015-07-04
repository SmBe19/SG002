package com.smeanox.games.sg002.player;

import com.smeanox.games.sg002.data.Point;
import com.smeanox.games.sg002.log.GameLogger;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.world.Action;
import com.smeanox.games.sg002.world.GameObject;
import com.smeanox.games.sg002.world.GameObjectType;
import com.smeanox.games.sg002.world.Scenario;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Player played by an external program
 *
 * @author Benjamin Schmid
 */
public class ExternalAIPlayer extends AIPlayer {
	private GameLogger logger;

	private String command;
	private Process process;
	private PrintWriter programIn;
	private BufferedReader programOut, programErr;

	private ArrayBlockingQueue<String> programOutQueue;
	private ProgramOutToQueueThread programOutToQueueThread;
	private StdErrHandlerThread stdErrHandlerThread;

	private boolean terminating;
	private int usedTimeouts;

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		if (process != null) {
			throw new IllegalStateException("the command can not be changed after the process was started");
		}
		this.command = command;
	}

	/**
	 * Start the process and set up all reader and writer, but no data is fed to the process
	 *
	 * @throws IOException
	 */
	private void startProcess() throws IOException {
		if (process != null) {
			throw new IllegalStateException("the process can not be started multiple times");
		}
		if (command == null || command.isEmpty()) {
			throw new IllegalStateException("the command has to be set before the process can be started");
		}

		logger = gameController.getLogger();
		if (logger == null) {
			logger = GameLogger.createDummyLogger();
		}

		logger.tech("gonna start \"" + name + "\" with command \"" + command + "\"");

		process = Runtime.getRuntime().exec(command);
		programIn = new PrintWriter(process.getOutputStream(), false);
		programOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
		programErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		programOutQueue = new ArrayBlockingQueue<String>(10, true);
		programOutToQueueThread = new ProgramOutToQueueThread(programOutQueue, programOut);
		stdErrHandlerThread = new StdErrHandlerThread(id, name, programErr);

		programOutToQueueThread.start();
		stdErrHandlerThread.start();

		terminating = false;
		usedTimeouts = 0;

		logger.tech("started \"" + name + "\"");
	}

	/**
	 * Terminate the process and close all reader and writer
	 *
	 * @throws IOException
	 */
	public void terminate() throws IOException {
		if (process == null) {
			return;
		}

		if (terminating) {
			return;
		}
		terminating = true;

		logger.tech("gonna terminate \"" + name + "\"");

		programOutToQueueThread.setTerminating(true);
		programOutToQueueThread.interrupt();
		stdErrHandlerThread.setTerminating(true);
		// no need to interrupt stdErrHandlerThread, it will be terminated because stderr is closed

		Thread terminator = new Thread() {
			@Override
			public void run() {
				try {
					int exitStatus = process.waitFor();
					logger.progBehaviour(id + "/" + name, "exited with status " + exitStatus);
				} catch (InterruptedException e) {
					process.destroy();
					logger.progBehaviour(id + "/" + name, "did not exit properly and was therefore killed");
				}
			}
		};
		terminator.start();

		try {
			terminator.join(50);
			if (terminator.isAlive()) {
				terminator.interrupt();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		programOut.close();
		programIn.close();
		programErr.close();

		process = null;
	}

	/**
	 * Join the given values, separated by one space
	 *
	 * @param values the values
	 * @return the joined string
	 */
	private String joinValues(Object... values) {
		LinkedList<String> vals = new LinkedList<String>();
		for (Object o : values) {
			vals.add(o.toString());
		}
		return String.join(" ", vals);
	}

	/**
	 * Feed the values of the scenario to the program
	 */
	private void feedScenario() {
		Scenario scenario = gameController.getScenario();
		programIn.println(joinValues(gameController.getPlayers().size(), scenario.getStartMoney(),
				scenario.getMapSizeX(), scenario.getMapSizeY(), id, scenario.getMaxGold()));

		for (Point point : scenario.getGoldPos()) {
			programIn.println(joinValues(point.x, point.y));
		}

		programIn.flush();
	}

	/**
	 * Feed the data for the start of a new round
	 */
	private void feedRound() {
		feedCurrentPlayerState();
		feedGameState();
		feedPerformedActions();
	}

	/**
	 * Feed the data for the current state of all players
	 */
	private void feedCurrentPlayerState() {
		for (Player player : gameController.getPlayers()) {
			programIn.println(player.getMoney());
		}
		programIn.flush();
	}

	/**
	 * Feed the data for the current state of the gameWorld
	 */
	private void feedGameState() {
		programIn.println(gameWorld.getGameObjects().size());
		for (GameObject gameObject : gameWorld.getGameObjects()) {
			programIn.println(joinValues(gameObject.getPlayer().getId(), gameObject.getPositionX(),
					gameObject.getPositionY(), gameObject.getGameObjectType().getExternalId(),
					gameObject.getHp()));
		}

		programIn.flush();
	}

	/**
	 * Feed the data for all performed actions since the last round
	 */
	private void feedPerformedActions() {
		LinkedList<String> lines = new LinkedList<String>();
		for (int i = 1; i < gameController.getPlayers().size(); i++) {
			Player player = gameController.getPlayers().get((id + i) % gameController.getPlayers().size());
			if (gameWorld.getPlayerActions().containsKey(player)) {
				for (Action action : gameWorld.getPlayerActions().get(player)) {
					String actionID = null;
					switch (action.actionType) {
						case MOVE:
							actionID = Consts.MOVE_ID;
							break;
						case FIGHT:
							actionID = Consts.FIGHT_ID;
							break;
						case PRODUCE:
							actionID = Consts.PRODUCE_ID;
							break;
						case NONE:
							break;
					}
					String line = joinValues(player.getId(), actionID, action.startX, action.startY,
							action.endX, action.endY);
					if(action.actionType == Action.ActionType.PRODUCE){
						line += " " + action.produceGameObjectType.getExternalId();
					}
					lines.add(line);
				}
			}
		}

		programIn.println(lines.size());
		for(String line : lines){
			programIn.println(line);
		}

		programIn.flush();
	}

	/**
	 * Read the next line from the process and handles not responding
	 *
	 * @return the read line
	 */
	private String getNextLine() throws ProtocolViolationException {
		try {
			String nextLine = null;
			nextLine = programOutQueue.poll(Consts.EXTERNAL_SHORT_TIMEOUT, TimeUnit.MILLISECONDS);

			if (nextLine == null) {
				// no line was available within timeout
				if (programOutToQueueThread.isAlive()) {
					// process is still running, try again
					nextLine = programOutQueue.poll(Consts.EXTERNAL_LONG_TIMEOUT, TimeUnit.MILLISECONDS);
					if (nextLine == null) {
						// process is blocked or in endless loop
						throw new ProtocolViolationException("did not print a line within " +
								(Consts.EXTERNAL_SHORT_TIMEOUT + Consts.EXTERNAL_LONG_TIMEOUT) + "ms");
					} else {
						logger.progBehaviour(id + "/" + name, "used between "
								+ Consts.EXTERNAL_SHORT_TIMEOUT + " and " +
								(Consts.EXTERNAL_SHORT_TIMEOUT + Consts.EXTERNAL_LONG_TIMEOUT) + "ms");
						usedTimeouts++;
						if (usedTimeouts > Consts.EXTERNAL_MAX_TIMEOUT_COUNT) {
							throw new ProtocolViolationException("used all allowed timeouts" +
									"(e.g. it took too long to output a line too often)");
						}
					}
				} else {
					// process crashed
					throw new ProtocolViolationException("exited instead of printing a line");
				}
			}

			return nextLine;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Read the desired actions
	 */
	private void readDesiredActions() throws ProtocolViolationException {
		String nextLine = getNextLine();
		if (nextLine == null) {
			return;
		}
		int desiredActionsCount = Integer.parseInt(nextLine);

		for (int i = 0; i < desiredActionsCount; i++) {
			try {
				nextLine = getNextLine();
				if (nextLine == null) {
					throw new ProtocolViolationException("no next line provided");
				}
				String[] parts = nextLine.split(" +");

				Action desiredAction = new Action();
				if (Consts.MOVE_ID.equals(parts[0])) {
					desiredAction.actionType = Action.ActionType.MOVE;
				} else if (Consts.FIGHT_ID.equals(parts[0])) {
					desiredAction.actionType = Action.ActionType.FIGHT;
				} else if (Consts.PRODUCE_ID.equals(parts[0])) {
					desiredAction.actionType = Action.ActionType.PRODUCE;
				}

				desiredAction.startX = Integer.parseInt(parts[1]);
				desiredAction.startY = Integer.parseInt(parts[2]);
				desiredAction.endX = Integer.parseInt(parts[3]);
				desiredAction.endY = Integer.parseInt(parts[4]);

				if (desiredAction.actionType == Action.ActionType.PRODUCE) {
					int gameObjectTypeId = Integer.parseInt(parts[5]);
					for (GameObjectType gameObjectType : GameObjectType.getAllGameObjectTypes()) {
						if (gameObjectType.getExternalId() == gameObjectTypeId) {
							desiredAction.produceGameObjectType = gameObjectType;
							break;
						}
					}
				}

				boolean result = gameWorld.doAction(desiredAction);
				if (!result) {
					throw new ProtocolViolationException("invalid action (action is now allowed or possible): " + nextLine);
				}
			} catch (NumberFormatException e){
				throw new ProtocolViolationException("invalid action (part is not a number): " + nextLine);
			} catch (IndexOutOfBoundsException e){
				throw new ProtocolViolationException("not enough parameters");
			}
		}
	}

	/**
	 * Perform the moves for this round
	 */
	@Override
	protected void playAI() throws ProtocolViolationException {
		try {
			if (process == null) {
				try {
					startProcess();
					feedScenario();
				} catch (IOException e) {
					throw new ProtocolViolationException(e);
				}
			}
			feedRound();
			readDesiredActions();
			setFinishedPlaying();
		} catch (ProtocolViolationException e){
			logger.progBehaviour(id + "/" + name, e.getMessage());
			throw e;
		}
	}

	/**
	 * Thread to fill the Queue with the output of the process
	 */
	private class ProgramOutToQueueThread extends Thread {

		private ArrayBlockingQueue<String> queue;
		private BufferedReader reader;
		private volatile boolean terminating = false;

		public ProgramOutToQueueThread(ArrayBlockingQueue<String> queue, BufferedReader reader) {
			this.queue = queue;
			this.reader = reader;
		}

		@Override
		public void run() {
			try {
				while (true) {
					String line = reader.readLine();
					if (line == null) {
						// program has terminated
						break;
					} else {
						queue.put(line);
					}
				}
			} catch (IOException e) {
				if (!terminating) {
					e.printStackTrace();
				}
			} catch (InterruptedException e) {
				if (!terminating) {
					e.printStackTrace();
				}
			}
		}

		public boolean isTerminating() {
			return terminating;
		}

		public void setTerminating(boolean terminating) {
			this.terminating = terminating;
		}
	}

	/**
	 * Thread to pass on the stderr of the process
	 */
	private class StdErrHandlerThread extends Thread {
		private int id;
		private String name;
		private BufferedReader reader;
		private volatile boolean terminating = false;

		public StdErrHandlerThread(int id, String name, BufferedReader reader) {
			this.id = id;
			this.name = name;
			this.reader = reader;
		}

		@Override
		public void run() {
			try {
				for (int i = 0; i < Consts.EXTERNAL_MAX_STDERR_LINES; i++) {
					String line = reader.readLine();
					if (line == null) {
						// program has terminated
						break;
					} else {
						logger.progStdErr(id + "/" + name, line);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public boolean isTerminating() {
			return terminating;
		}

		public void setTerminating(boolean terminating) {
			this.terminating = terminating;
		}
	}
}
