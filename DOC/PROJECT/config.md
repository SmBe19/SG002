# Config files
All config files are located in `android/assets/config`.

# GameObjectTypes
This file describes the available GameObjectTypes and describes the stats for each. The available parameters are:

* `externalId`: the id used when communicating to external AIs
* `id`: id used within the program and the config files
* `name`: key in the i18n resource bundle containing the localized name
* `texture`: the name of the texture used to display this type
* `start`: whether this is the type that each player receives at the start. Only one type can be marked as start
* `defaultHP`: health points of an object when it is first created
* `radius...`: the radius for different actions. An action can be performed iff the max of x and y distance to the requested end field is within the specified min (inclusive) and max (inclusive). If the given type can not perform the given action, min and max should be set to `0`
* `canFight`: whether this type can fight against other objects
* `Damage`: how much damage can be dealt against the some other type
* `canProduce`: whether this type can produce new objects
* `Produce`: id of a type that can be produced

# Scenarios
This file describes the available scenarios. The available parameters are:

* `name`: key in the i18n resourc bundle containing the localized name
* `id`: the id of the scenario
* `mapSizeX`: number of fields on the map in x direction
* `mapSizeY`: number of fields on the map in y direction
* `maxGold`: number of locations gold can be placed
* `mapPlayerCount`: maximal number of players that can play
* `multipleActionsPerObject`: whether an Object can perform one action per round or one action per type per round. This should always be true
* `seed`: seed used to place the start objects and gold
* `startGameObjectminDistance`: minimal distance between the start objects of two players
* `startMoney`: how much money each player starts with
* `walkDiagonal`: whether to use Manhattan distance or max(distX, distY) as distance. This should always be true
* `StartPos`: an optional list of start positions. If not specified they will be placed at random
* `GoldPos`: an optional list of positions for gold. If not specified they will be placed at random