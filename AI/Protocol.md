# Protocol for user AI
This document explains the protocoll used to communicate between the engine and a User AI (UAI)

## Miscellanious
User AIs are started in a seperate process. Std I/O is used to communicate. Thus they can be written in any language.

Each player is assigned an unique number, ranging from `0` to `playerCount - 1`. The players will take turns according to their number (e.g. after player 1 plays player 2).

## GameObjectType
Each GameObjectType is assigned an id:

* `0`: `townCenter`
* `1`: `goldMine`
* `2`: `villager`
* `3`: `knight`
* `4`: `archer`
* `5`: `infantry`

The stats for each type will be available in a seperate document, atm it can be viewed at /android/assets/config/GameObjectTypes.xml.

## Startup
The UAS receives the values of the used scenario on one line, values seperated by one space:

`playerCount startMoney mapWidth mapHeight numOfAI numOfGold`

* `playerCount`: the number of players participating in the game
* `startMoney`: the amount of money each player receives at the start of the game
* `mapWidth`: the width of the map
* `mapHeight`: the height of the map
* `numOfAI`: the number the UAI is assigned to
* `numOfGold`: the number of places where a goldMine can be built

For each place where a goldMine can be built, one line follows:

`goldX goldY`

* `goldX`: the x position
* `goldY`: the y position

## Round
At the start of each round the UAI will receive the state of the other players, the current game state and all actions the other players performed. Afterwards the UAI should output the desired actions.

### Current player state
For each player the amount of money he owns is listed, one per line. The first value corresponds to player 0, the second to player 1, ...

### Current game state
For the current game state each GameObject currently on the map is listed, one GameObject per line, it's values separated by one space:

`playerNum posX posY gameObjectType hp`

* `playerNum`: the number of the player that owns the GameObject
* `posX`: the x position
* `posY`: the y position
* `gameObjectType`: the id of the type of the GameObject

The list is preceded by one integer on a separate line, the number of GameObjects.

### Performed actions
Every event that occured since the last time the UAI player is listed, one action per line, values separated by one space, in the same format as the expected output preceded by the number of the player that performed the acttion:

`playerNum actionType startX startY endX endY [gameObjectType]`

See desired action section for explanation. The list is preceded by one integer on a separate line, the number of actions performed

### Desired actions
After reading the input the UAI should output all desired actions in the order they should occur, one per line, values separated by one space:

`actionType startX startY endX endY [gameObjectType]`

* `actionType`: the type of action to perform (`0`: move, `1`: fight, `2`: produce)
* `startX`: the x position of the GameObject that should perform the action
* `startY`: the y position of the GameObject that should perform the action
* `endX`: the x position of the Field to which the action should be performed to (e.g. the field to walk to, the field to attack or the field to build the new GameObject on)
* `endY`: the y position of the Field to which the action should be performed to
* `[gameObjectType]`: if the actionType is `2` (produce), the id of the type that should be produced.

If the desired action is not valid the AI is terminated and the game ends. The list has to be preceded by one integer on a separate line, the number of desired actions.

## Example
Lines preceded with `<` are outputet by the engine, lines preceded with `>` are outputet by the UAI.

### Startup

    < 3 1000 10 10 1

### Round

    < 900
    < 1000
    < 1000
    < 4
    < 0 2 3 0 500
    < 0 3 2 2 40
    < 1 7 6 0 500
    < 2 2 8 0 500
    < 0 2 2 3 3 2 2
    > 1
    > 2 7 6 7 7 3