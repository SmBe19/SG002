package com.smeanox.games.sg002.player;

import com.smeanox.games.sg002.data.Point;
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
public class ExternalAIPlayer extends Player {

	protected boolean finishedPlaying;

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
		if(process != null){
			throw new IllegalStateException("the command can not be changed after the process was started");
		}
		this.command = command;
	}

	/**
	 * Start the process and set up all reader and writer, but no data is fed to the process
	 * @throws IOException
	 */
	private void startProcess() throws IOException {
		if(process != null){
			throw new IllegalStateException("the process can not be started multiple times");
		}
		if(command == null || command.isEmpty()){
			throw new IllegalStateException("the command has to be set before the process can be started");
		}

		process = Runtime.getRuntime().exec(command);
		programIn = new PrintWriter(process.getOutputStream(), false);
		programOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
		programErr = new BufferedReader(new InputStreamReader(process.getInputStream()));

		programOutQueue = new ArrayBlockingQueue<String>(10, true);
		programOutToQueueThread = new ProgramOutToQueueThread(programOutQueue, programOut);
		stdErrHandlerThread = new StdErrHandlerThread(id, name, programErr);

		terminating = false;
		usedTimeouts = 0;
	}

	/**
	 * Terminate the process and close all reader and writer
	 * @throws IOException
	 */
	public void terminate() throws IOException {
		if(process == null){
			throw new IllegalStateException("the process can not be terminated before it is started");
		}

		if(terminating){
			return;
		}
		terminating = true;

		programOutToQueueThread.setTerminating(true);
		programOutToQueueThread.interrupt();
		stdErrHandlerThread.setTerminating(true);
		// no need to interrupt stdErrHandlerThread, it will be terminated because stderr is closed

		Thread terminator = new Thread(){
			@Override
			public void run() {
				try{
					int exitStatus = process.waitFor();
					// TODO replace with real logging
					System.err.println(name + " exited with status " + exitStatus);
				} catch (InterruptedException e) {
					process.destroy();
					// TODO replace with real logging
					System.err.println(name + " did not exit properly and was therefor killed");
				}
			}
		};
		terminator.start();

		try{
			terminator.join(50);
			if(terminator.isAlive()){
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
	 * @param values the values
	 * @return the joined string
	 */
	private String joinValues(Object... values){
		LinkedList<String> vals = new LinkedList<String>();
		for(Object o : values){
			vals.add(o.toString());
		}
		return String.join(" ", vals);
	}

	/**
	 * Feed the values of the scenario to the program
	 */
	private void feedScenario(){
		Scenario scenario = gameController.getScenario();
		programIn.println(joinValues(scenario.getMaxPlayerCount(), scenario.getStartMoney(),
				scenario.getMapSizeX(), scenario.getMapSizeY(), id, scenario.getMaxGold()));

		for(Point point : scenario.getGoldPos()){
			programIn.println(joinValues(point.x, point.y));
		}

		programIn.flush();
	}

	/**
	 * Feed the data for the start of a new round
	 */
	private void feedRound(){
		feedCurrentPlayerState();
		feedGameState();
		feedPerformedActions();
	}

	/**
	 * Feed the data for the current state of all players
	 */
	private void feedCurrentPlayerState(){
		for(Player player : gameController.getPlayers()){
			programIn.println(player.getMoney());
		}
		programIn.flush();
	}

	/**
	 * Feed the data for the current state of the gameWorld
	 */
	private void feedGameState(){
		for(GameObject gameObject : gameWorld.getGameObjects()){
			programIn.println(joinValues(gameObject.getPlayer().getId(), gameObject.getPositionX(),
					gameObject.getPositionY(), gameObject.getGameObjectType().getExternalId(),
					gameObject.getHp()));
		}
	}

	/**
	 * Read the next line from the process and handles not responding
	 * @return the read line
	 */
	private String getNextLine(){
		try {
			String nextLine = null;
			nextLine = programOutQueue.poll(Consts.EXTERNAL_SHORT_TIMEOUT, TimeUnit.MILLISECONDS);

			if(nextLine == null){
				// no line was available within timeout
				if(programOutToQueueThread.isAlive()){
					// process is still running, try again
					nextLine = programOutQueue.poll(Consts.EXTERNAL_LONG_TIMEOUT, TimeUnit.MILLISECONDS);
					if(nextLine == null){
						// process is blocked or in endless loop
						// TODO report protocol violation (throw up;)
						return null;
					} else {
						// TODO log long waiting time
						usedTimeouts++;
						if(usedTimeouts > Consts.EXTERNAL_MAX_TIMEOUT_COUNT){
							// TODO report protocol violation (throw up;)
						}
					}
				} else {
					// process crashed
					// TODO report protocol violation (throw up;)
					return null;
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
	private void readDesiredActions(){
		// TODO report protocol violations and check input
		String nextLine = getNextLine();
		if(nextLine == null){
			return;
		}
		int desiredActionsCount = Integer.parseInt(nextLine);

		for (int i = 0; i < desiredActionsCount; i++) {
			nextLine = getNextLine();
			if(nextLine == null){
				return;
			}
			String[] parts = nextLine.split(" ");

			Action desiredAction = new Action();
			if("0".equals(parts[0])){
				desiredAction.actionType = Action.ActionType.MOVE;
			} else if("1".equals(parts[0])){
				desiredAction.actionType = Action.ActionType.FIGHT;
			} else if("2".equals(parts[0])){
				desiredAction.actionType = Action.ActionType.PRODUCE;
			}

			desiredAction.startX = Integer.parseInt(parts[1]);
			desiredAction.startY = Integer.parseInt(parts[2]);
			desiredAction.endX = Integer.parseInt(parts[3]);
			desiredAction.endY = Integer.parseInt(parts[4]);

			if(desiredAction.actionType == Action.ActionType.PRODUCE){
				int gameObjectTypeId = Integer.parseInt(parts[5]);
				for(GameObjectType gameObjectType : GameObjectType.getAllGameObjectTypes()){
					if(gameObjectType.getExternalId() == gameObjectTypeId){
						desiredAction.produceGameObjectType = gameObjectType;
						break;
					}
				}
			}

			gameWorld.doAction(desiredAction);
		}
	}

	/**
	 * Feed the data for all performed actions since the last round
	 */
	private void feedPerformedActions(){
		// TODO implement me
	}

	@Override
	public final void update(float delta) {
		if (finishedPlaying) {
			finishedPlaying = false;
			endPlaying();
		}
	}

	/**
	 * In the next frame the gamecontroller will be informed that the AI finished
	 */
	protected void setFinishedPlaying() {
		finishedPlaying = true;
	}

	/**
	 * Perform the moves for this round
	 */
	@Override
	protected void play() {
		if(process == null){
			try {
				startProcess();
				feedScenario();
			} catch (IOException e) {
				// TODO handle exception
			}
		}
		feedRound();
	}

	/**
	 * Thread to fill the Queue with the output of the process
	 */
	private static class ProgramOutToQueueThread extends Thread{

		private ArrayBlockingQueue<String> queue;
		private BufferedReader reader;
		private volatile boolean terminating = false;

		public ProgramOutToQueueThread(ArrayBlockingQueue<String> queue, BufferedReader reader){
			this.queue = queue;
			this.reader = reader;
		}

		@Override
		public void run() {
			try{
				while(true){
					String line = reader.readLine();
					if(line == null){
						// program has terminated
						break;
					} else {
						queue.put(line);
					}
				}
			} catch (IOException e) {
				if(!terminating) {
					e.printStackTrace();
				}
			} catch (InterruptedException e) {
				if(!terminating) {
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
	private static class StdErrHandlerThread extends Thread{
		private int id;
		private String name;
		private BufferedReader reader;
		private volatile boolean terminating = false;

		public StdErrHandlerThread(int id, String name, BufferedReader reader){
			this.id = id;
			this.name = name;
			this.reader = reader;
		}

		@Override
		public void run() {
			try{
				for (int i = 0; i < Consts.EXTERNAL_MAX_STDERR_LINES; i++) {
					String line = reader.readLine();
					if(line == null){
						// program has terminated
						break;
					} else {
						// TODO replace with real logging
						System.err.println(line);
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
