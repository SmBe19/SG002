#include <iostream>
#include <vector>
#include <set>

#ifndef SG002
#define SG002

using namespace std;

/*
 * This file contains the interface to communicate with the server.
 * The UAI calls startGame() when it's ready. For each round to be
 * played play(GameWorld&) will be called. The GameWorld contains
 * the whole game state. The UAI can perform actions by calling
 * gameWorld.move(...), gameWorld.fight(...) and gameWorld.produce(...)
 */

/*
 * This class describes one type of GameObjects (e.g. TownCenter, Villager)
 */
class GameObjectType {
	public:
	// The id of this type. Corresponds to the id used in the protocol and to the index in gameWorld.getGameObjectType(int)
	int id;
	// Health points a new instance has
	int defaultHp;
	// How much an object costs when producing
	int value;
	// How much the player receives per round per object of this type
	int valuePerRound;
	// How much the player receives when he destroys an object of this type
	int valueOnDestruction;
	// The minimal radius to walk
	int radiusWalkMin;
	// The maximal radius to walk
	int radiusWalkMax;
	// The minimal radius to produce
	int radiusProduceMin;
	// The maximal radius to produce
	int radiusProduceMax;
	// The minimal radius to fight
	int radiusFightMin;
	// The maximal radius to fight
	int radiusFightMax;
	// How much hp the enemy loses when this type attacks
	int damage;
	// Whether this type is able to fight
	bool canFight;
	// Whether this type is able to produce
	bool canProduce;
	// Whether this type is able to move
	bool canMove;
	// List of GameObjectTypes this type can produce.
	vector<int> produceList;

	GameObjectType(int cId, int cDefaultHp, int cValue, int cValuePerRound, int cValueOnDestruction,
			int cRadiusWalkMin, int cRadiusWalkMax, int cRadiusProduceMin, int cRadiusProduceMax, int cRadiusFightMin, int cRadiusFightMax,
			int cDamage, vector<int> cProduceList, bool cCanFight, bool cCanProduce){
		id = cId;
		defaultHp = cDefaultHp;
		value = cValue;
		valuePerRound = cValuePerRound;
		valueOnDestruction = cValueOnDestruction;
		radiusWalkMin = cRadiusWalkMin;
		radiusWalkMax = cRadiusWalkMax;
		radiusProduceMin = cRadiusProduceMin;
		radiusProduceMax = cRadiusProduceMax;
		radiusFightMin = cRadiusFightMin;
		radiusFightMax = cRadiusFightMax;
		damage = cDamage;
		produceList = cProduceList;
		canFight = cCanFight;
		canProduce = cCanProduce;
		canMove = radiusWalkMax > 0;
	}
};

/*
 * This class describes an action the player wants to perform / has performed
 */
class Action {
	public:
	// the player that did the action
	int player;
	// type of the action (0 -> move, 1 -> fight, 2 -> produce)
	int actionType;
	// start position of this action (e.g. the object that wants to attack)
	int startX, startY;
	// end position of this action (e.g. the object that should be attacked)
	int endX, endY;
	// the type of object that was created (only when actionType == 2)
	int produceGameObjectType;
	
	Action(){}
	Action(int cPlayer, int cActionType, int cStartX, int cStartY, int cEndX, int cEndY, int cProduceGameObjectType){
		player = cPlayer;
		actionType = cActionType;
		startX = cStartX;
		startY = cStartY;
		endX = cEndX;
		endY = cEndY;
		produceGameObjectType = cProduceGameObjectType;
	}
	
	// Read the action from the server
	void readAction(){
		cin >> player >> actionType >> startX >> startY >> endX >> endY;
		if(actionType == 2){
			cin >> produceGameObjectType;
		}
	}
	
	// send the action to the server but omitting the player (as defined in the protocol)
	void writeActionWithoutPlayer(){
		cout << actionType << " " << startX << " " << startY << " " << endX << " " << endY;
		if(actionType == 2){
			cout << " " << produceGameObjectType;
		}
		cout << "\n";
	}
};

/*
 * This class describes an object on the map.
 */
class GameObject {
	public:
	// health points this object has
	int hp;
	// type of the object (can be resolved using gameWorld.getGameObjectType(int))
	int gameObjectType;
	// the player to which this object belongs
	int player;
	// the position of the object
	int x, y;
	// whether this object exists (if false, the other fields have no meaning)
	bool present;
	
	GameObject(){}
	GameObject(int cHp, int cGameObjectType, int cPlayer, int cX, int cY){
		hp = cHp;
		gameObjectType = cGameObjectType;
		player = cPlayer;
		x = cX;
		y = cY;
		present = true;
	}
	
	// Read the state of the GameObject from the server
	void readGameObject(){
		cin >> player >> x >> y >> gameObjectType >> hp;
		present = true;
	}
};

/*
 * This class describes the whole state of the game
 */
class GameWorld {
	public:
	// the number of players playing
	int playerCount;
	// the amount of money each player has at the start
	int startMoney;
	// the size of the map
	int width, height;
	// the id the UAI has (between 0 and playerCount - 1)
	int myId;
	// Number of positions where GoldMines can be built
	int goldCount;
	// Number of GameObjects currently on the map
	int gameObjectCount;
	// Number of performed actions (of other players) since the last round
	int actionCount;
	
	// 2D Array of the map
	vector<vector<GameObject> > worldMap;
	// List of positions where a GoldMine can be built
	vector<pair<int, int> > gold;
	// Set of positions where a GoldMine can be built (used for faster check)
	set<pair<int, int> > goldSet;
	// available amount of money for each player
	vector<int> playerMoney;
	// List of all GameObjects
	// TODO update this list when action is performed
	vector<GameObject> gameObjects;
	// List of performed actions (of other players) since the last round
	vector<Action> actions;
	// List of GameObjectTypes
	vector<GameObjectType> gameObjectTypes;
	
	// List of actions performed this round
	vector<Action> actionQueue;
	// Whether the list was already written this round
	bool didWriteActions;
	
	// Add the given action to the list of performed actions
	void addActionToQueue(Action action){
		didWriteActions = false;
		actionQueue.push_back(action);
	}
	
	GameObject getWorldMap(int x, int y){
		return worldMap[x][y];
	}
	
	bool isGold(int x, int y){
		return goldSet.find(make_pair(x, y)) != goldSet.end();
	}
	
	int getPlayerMoney(int i){
		return playerMoney[i];
	}
	
	GameObjectType getGameObjectType(int i){
		return gameObjectTypes[i];
	}
	
	// Fill the stats of all GameObjectTypes to the list of GameObjectTypes
	void fillGameObjectTypes(){
		vector<int> prodListE, prodList0, prodList2;
		prodList0.push_back(2);
		prodList0.push_back(3);
		prodList0.push_back(4);
		prodList0.push_back(5);
		prodList2.push_back(0);
		prodList2.push_back(1);
		
		// TownCenter
		gameObjectTypes.push_back(GameObjectType(0, 250, 500, 10, 100, 0, 0, 1, 2, 0, 0, 0, prodList0, false, true));
		// GoldMine
		gameObjectTypes.push_back(GameObjectType(1, 50, 500, 150, 50, 0, 0, 0, 0, 0, 0, 0, prodListE, false, false));
		// Villager
		gameObjectTypes.push_back(GameObjectType(2, 60, 100, 1, 1, 1, 5, 1, 1, 1, 2, 5, prodList2, true, true));
		// Knight
		gameObjectTypes.push_back(GameObjectType(3, 80, 220, 5, 110, 1, 2, 0, 0, 1, 5, 26, prodListE, true, false));
		// Archer
		gameObjectTypes.push_back(GameObjectType(4, 60 ,180, 5, 90, 1, 3, 0, 0, 4, 8, 14, prodListE, true, false));
		// Infantry
		gameObjectTypes.push_back(GameObjectType(5, 40, 200, 5, 100, 1, 5, 0, 0, 1, 2, 22, prodListE, true, false));
	}
	
	// read the configuration from the server (to be called once at the beginning)
	void readStartup(){
		cin >> playerCount >> startMoney >> width >> height >> myId >> goldCount;
		for(int i = 0; i < goldCount; i++){
			int x, y;
			cin >> x >> y;
			gold.push_back(make_pair(x, y));
			goldSet.insert(make_pair(x, y));
		}
		for(int i = 0; i < playerCount; i++){
			playerMoney.push_back(startMoney);
		}
		
		worldMap = vector<vector<GameObject> > (width, vector<GameObject>(height));
	}
	
	// read the state of the game and the performed actions
	void readRound(){
		for(int i = 0; i < playerCount; i++){
			cin >> playerMoney[i];
		}
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				worldMap[x][y].present = false;
			}
		}
		
		gameObjects.clear();
		cin >> gameObjectCount;
		
		for(int i = 0; i < gameObjectCount; i++){
			GameObject gameObject;
			gameObject.readGameObject();
			
			gameObjects.push_back(gameObject);
			worldMap[gameObject.x][gameObject.y] = gameObject;
		}
		
		actions.clear();
		cin >> actionCount;
		
		for(int i = 0; i < actionCount; i++){
			Action action;
			action.readAction();
			
			actions.push_back(action);
		}
		
		didWriteActions = false;
	}
	
	// Send the actions performed this round to the server
	void writeActions(){
		if(didWriteActions){
			return;
		}
		didWriteActions = true;
		
		cout << actionQueue.size() << "\n";
		
		for(int i = 0; i < actionQueue.size(); i++){
			actionQueue[i].writeActionWithoutPlayer();
		}
		
		cout << flush;
		
		actionQueue.clear();
	}
	
	// Move the object found at (startX/startY) to (endX/endY)
	void move(int startX, int startY, int endX, int endY){
		addActionToQueue(Action(myId, 0, startX, startY, endX, endY, 0));
		
		worldMap[endX][endY] = worldMap[startX][startY];
		worldMap[endX][endY].x = endX;
		worldMap[endX][endY].y = endY;
		worldMap[startX][startY].present = false;
		
		// TODO change x & y on the object in gameObjects
	}
	
	// The object at (startX/startY) attacks the object at (endX/endY)
	void fight(int startX, int startY, int endX, int endY){
		addActionToQueue(Action(myId, 1, startX, startY, endX, endY, 0));
		
		worldMap[endX][endY].hp -= gameObjectTypes[worldMap[startX][startY].gameObjectType].damage;
		if(worldMap[endX][endY].hp <= 0){
			worldMap[endX][endY].present = false;
		}
		
		// TODO change hp on the object in gameObjects
	}
	
	// The object at (startX/startY) produces a GameObject of the type produceGameObjectType at (endX/endY)
	void produce(int startX, int startY, int endX, int endY, int produceGameObjectType){
		addActionToQueue(Action(myId, 2, startX, startY, endX, endY, produceGameObjectType));
		
		worldMap[endX][endY] = GameObject(gameObjectTypes[produceGameObjectType].defaultHp,
				produceGameObjectType, myId, endX, endY);
				
		playerMoney[myId] -= gameObjectTypes[produceGameObjectType].value;
		
		// TODO add object to gameObjects but mark somehow that is was already used
	}
};

// This method is called for each round (to be implemented by the UAI)
void play(GameWorld&);

// Main Method for the interface. Start the connection to the server and handle the rounds
void startGame(){
	GameWorld gameWorld;
	gameWorld.fillGameObjectTypes();
	gameWorld.readStartup();
	
	while(true){
		if(cin.eof()){
			break;
		}
		gameWorld.readRound();
		play(gameWorld);
		gameWorld.writeActions();
	}
}

#endif