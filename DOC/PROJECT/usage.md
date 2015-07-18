# Usage
You can run the pragram in different configurations. Which one to use is specified with program arguments.

If you start if without any parameters the visualization will start and show the menu to choose the settings for the game. Not all features are available here.

## Arguments
The following arguments are available (all are optional):

* `-nogui 0/1`: whether to start with (`0`) or without a gui (`1`). Default is `0`.
* `-fullscreen 0/1`: whether to use full screen or not. Default is `0`. Only available with `-nogui 0`.
* `-logfolder folder`: the folder in which the log files will be saved. Default is `.`.
* `-gamelog file`: the name (suffix) of the gamelog. If not specified, no file will be written.
* `-behaviourlog file`: the name (suffix) of the behaviourlog which contains information about external AIs. If not specified, no file will be written.
* `-stdout 0/1`: whether the gamelog should be written to stdout. Default is `0`.
* `-stderr 0/1`: whether the behaviourlog should be written to stderr. Default is `0`.
* `-tournamentlog file`: the name of the log file for a tournament. If not specified, no file will be written.
* `-evaluationlog file`: the name (suffix) of the log file for an evaluation. If not specified, no file will be written.
* `-tournamentstdout 0/1`: whether the log for a tournament should be written to stdout. Default is `1`.
* `-tournamentstderr 0/1`: whether additional information (e.g. progress) of a tournament should be written to stderr. Default is `1`.
* `-evaluationstdout 0/1`: whether the log for an evaluation should be written to stdout. Default is `1`.
* `-evaluationstderr 0/1`: whether additional information (e.g. progress) of an evaluation should be written to stderr. Default is `1`.
* `-players file`: path to a file which contains the commands for the players. Required with `-nogui 1` or `-autostart 1`.
* `-names file`: path to a file which contains the names of the players, one name per line. Required with `-nogui 1` or `-autostart 1`.
* `-playersoverride 0/1`: whether to allow the user to change the player commands. Default is `0`. Only available with `-nogui 0`.
* `-namesoverride 0/1`: whether to allow the user to change the player names. Default is `0`. Only available with `-nogui 0`.
* `-scenario id`: the id of the scenario to use. This or `-tournament` required with `-nogui 1` or `-autostart 1`.
* `-playercount count`: the number of players that play in one game. Required with `-nogui 1` or `-autostart 1`.
* `-autostart 0/1`: whether to start the game without showing the menu. Only available with `-nogui 0`.
* `-replay file`: the file containing the game log that should be replayed. If specified, most other arguments are ignored.
* `-evaluation 0/1`: whether to perform an evaluation (i.e. play all possible match ups with the given players / playercount) (`1`) or only play one game (using the first n players) (`0`). Default is `1` Only available with `-nogui 1`.
* `-tournament file`: file containing the scenarios to play, one scenario id per line. Only available with `-nogui 1`.
* `-printfps 0/1`: whether to print the current frames per second to stdout. Default is `0`. Only available with `-nogui 0`.

## Usecases
Some argument combinations for important usecases using example values.

### Play a game with predefined players
`-gamelog gamelog.txt -players players.txt -names names.txt`

`-gamelog gamelog.txt -players players.txt -names names.txt -playersoverride 1 -namesoverride 1`

### Play a game with predefined players using a certain scenario
`-gamelog gamelog.txt -players players.txt -names names.txt -scenario small -playercount 3 -autostart 1`

### Play a game without a GUI
`-nogui 1 -gamelog gamelog.txt -players players.txt -names names.txt -scenario small -playercount 3 -evaluation 0`

### Replay a logged game
`-replay gamelog.txt`

### Play all match ups using one scenario
`-nogui 1 -logfolder logs.txt -gamelog gamelog.txt -behaviourlog behaviourlog.txt -stderr 1 -evaluationlog evaluation.txt -players players.txt -names names.txt -playercount 2 -scenario small`

### Play all match ups using different scenarios
`-nogui 1 -logfolder logs.txt -gamelog gamelog.txt -behaviourlog behaviourlog.txt -stderr 1 -tournamentlog tournament.txt -evaluationlog evaluation.txt -players players.txt -names names.txt -playercount 2  -tournament scenariosToPlay.txt`

## Scenarios
How the scenarios are configured is described [here](config.md). The available ids are:

* `small`
* `big`
* `bigPlenty`
* `bigLittle`
* `vast`
* `giant`
* `tourney`

## Player commands
The command for a player can be a path to an executable that obeys the protocol for external AIs or a special string:

* `:local`: a human player that plays the game using the GUI
* `:BenNo1`: a sample AI implemented within the program