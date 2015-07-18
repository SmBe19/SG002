#Project structure
The soucre code is spread across a few folders. The main part is found in the folder `core` which contains platform independent code. The folders `android`, `desktop` and `html` contain platform specific implementations to start the program. If you want to modify (or only look at) the code you should only need the `core` folder.

The code contains detailed javadoc for each package, class & method. In this document I'll give a short overview.

## Packages
### data
This package contains classes that are not available on android but only on desktop. They are copied from the original source code.

### debug
This package contains utilities that help debuging the program (e.g. a timer to measure how long a certain code segment ran). In the final version those classes shouldn't be used anymore.

### log
This package contains the loggers that log a game or tournament to a file or stdout / stderr

### nogui
This package contains the implementations of a tournament / evaluation / game that runs without the gui. This is used to evaluate different AI's against each other.

### player
This package contains different implementations for a player. The most important are:

* AIPlayer: Base class for computer controlled players
* ExternalAIPlayer: calls an external program and communicates with stdin / stdout
* LocalPlayer: Allows a human player to play the game using the GUI
* ReplayPlayer: Reads a gamelog and repeats the saved actions

### screen
This package contains the different screens that form the gui. The most important one is `GameScreen` which displays a game and allows a human player to play.

### util
This package contains different utilities (e.g. to read config, i18n data).

### view
This package contains views that display the current game state.

### world
This package contains the whole model of the game and contains classes that represent the current world state, allow to manipulate it and control the game flow.

The class `GameWorld` represents the current world state and allows to modify it using defined actions (e.g. move a GameObject). Each unit on the field is represented by a `GameObject`. The class `GameObjectType` stores information about a certain type of GameObjects (e.g. for a villager, a knight). The class `GameController` controls the game flow (e.g. notifies a player that it should play) and knows each player. This is the "main" class for the simulation of a game. It is either used by the GUI (`GameScreen`) or a Tournament / Evaluation.

## Important classes
Depending on what you would like to change / analyze, different classes are important.

### Game rules
The classes that handle the rules of the game are mostly in the package `world`. The most important classes are `GameWorld` and `GameObject`.

### Visualization
Tha classes that generate the visualization are located in the packages `screen` and `view`. The class `GameView` is resposible for how to display the current game state and `GameScreen` displays the GUI around it.

### Communication with external AI
The class `ExternalAI` in the package `player` handles the whole communication with external AIs.

### Tournament / Evaluation
The responsible classes are located in the package `nogui`.

An Evaluation takes a list of players and plays every possible match up between those players using a given number of players per game. A Tournament consists of several Evaluations.