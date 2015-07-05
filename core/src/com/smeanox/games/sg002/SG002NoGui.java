package com.smeanox.games.sg002;

import com.badlogic.gdx.files.FileHandle;
import com.smeanox.games.sg002.log.TournamentLogger;
import com.smeanox.games.sg002.nogui.Evaluation;
import com.smeanox.games.sg002.nogui.InvalidConfigurationException;
import com.smeanox.games.sg002.nogui.Tournament;
import com.smeanox.games.sg002.util.ConfigFileUtil;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.util.GameObjectTypeReader;
import com.smeanox.games.sg002.util.Language;
import com.smeanox.games.sg002.util.MapObjectTypeReader;
import com.smeanox.games.sg002.util.ProgramArguments;
import com.smeanox.games.sg002.util.ScenarioReader;
import com.smeanox.games.sg002.world.Scenario;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Main Class of the game without gui
 *
 * @author Benjamin Schmid
 */
public class SG002NoGui {
	private static boolean prepared = false;

	public static void prepare(){
		if(prepared){
			return;
		}

		prepared = true;
		if(!ProgramArguments.noGUI){
			throw new IllegalStateException("SG002NoGui can only be called in noGUI mode");
		}
		Language.loadStringsForHeadless(Locale.getDefault());
		GameObjectTypeReader.readGameObjectTypes(new FileHandle("config/GameObjectTypes.xml"));
		MapObjectTypeReader.readMapObjectTypes(new FileHandle("config/MapObjectTypes.xml"));
		ScenarioReader.readScenarios(new FileHandle("config/Scenarios.xml"));

		Consts.aiSleep = 0;
	}

	public void start() throws FileNotFoundException, InvalidConfigurationException {
		prepare();

		LinkedList<String> playerNames = ConfigFileUtil.readAllLines(new File(ProgramArguments.namesFile));
		LinkedList<String> playerCommands = ConfigFileUtil.readAllLines(new File(ProgramArguments.playersFile));

		if(ProgramArguments.scenario != null){
			TournamentLogger logger = new TournamentLogger();

			logger.setLogFolder(ProgramArguments.logFolder);
			if(ProgramArguments.evaluationLog != null) {
				logger.setEventLogPath(ProgramArguments.evaluationLog);
			}
			logger.setPrintStdOut(ProgramArguments.evaluationStdOut);
			logger.setPrintStdErr(ProgramArguments.evaluationStdErr);

			new Evaluation(Scenario.getScanarioById(ProgramArguments.scenario), playerNames, playerCommands, logger).play();
		} else if(ProgramArguments.tournamentFile != null){
			TournamentLogger logger = new TournamentLogger();

			logger.setLogFolder(ProgramArguments.logFolder);
			if(ProgramArguments.tournamentLog != null) {
				logger.setEventLogPath(ProgramArguments.tournamentLog);
			}
			logger.setPrintStdOut(ProgramArguments.tournamentStdOut);
			logger.setPrintStdErr(ProgramArguments.tournamentStdErr);

			new Tournament(ProgramArguments.tournamentFile, playerNames, playerCommands, logger).play();
		} else {
			throw new InvalidConfigurationException("scenario or tournament has to be set");
		}
	}
}
