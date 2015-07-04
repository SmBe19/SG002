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
	private:
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
	// List of GameObjectTypes this type can produce.
	vector<int> produceList;
	
	public:
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
	}
	
	int getId(){
		return id;
	}
	
	int getDefaultHp(){
		return defaultHp;
	}
	
	int getValue(){
		return value;
	}
	
	int getValuePerRound(){
		return valuePerRound;
	}
	
	int getValueOnDestruction(){
		return valueOnDestruction;
	}
	
	int getRadiusWalkMin(){
		return radiusWalkMin;
	}
	
	int getRadiusWalkMax(){
		return radiusWalkMax;
	}
	
	int getRadiusProduceMin(){
		return radiusProduceMin;
	}
	
	int getRadiusProduceMax(){
		return radiusProduceMax;
	}
	
	int getRadiusFightMin(){
		return radiusFightMin;
	}
	
	int getRadiusFightMax(){
		return radiusFightMax;
	}
	
	int getDamage(){
		return damage;
	}
	
	vector<int> getProduceList(){
		return produceList;
	}
	
	bool isCanMove(){
		return radiusWalkMax > 0;
	}
	
	bool isCanFight(){
		return canFight;
	}
	
	bool isCanProduce(){
		return canProduce;
	}
};

/*
 * This class describes an action the player wants to perform / has performed
 */
class Action {
	private:
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
	
	public:
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
	
	int getActionType(){
		return actionType;
	}
	
	int getStartX(){
		return startX;
	}
	
	int getStartY(){
		return startY;
	}
	
	int getEndX(){
		return endX;
	}
	
	int getEndY(){
		return endY;
	}
	
	int getProduceGameObjectType(){
		return produceGameObjectType;
	}
	
	// Read the action from the server
	void readAction(){
		cin >> player >> actionType >> startX >> startY >> endX >> endY;
		if(actionType == 2){
			cin >> produceGameObjectType;
		}
	}
	
	// Send the action to the server
	void writeAction(){
		cout << player << " " << actionType << " " << startX << " " << startY << " " << endX << " " << endY;
		if(actionType == 2){
			cout << " " << produceGameObjectType;
		}
		cout << "\n";
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
	private:
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
	
	public:
	GameObject(){}
	GameObject(int cHp, int cGameObjectType, int cPlayer, int cX, int cY){
		hp = cHp;
		gameObjectType = cGameObjectType;
		player = cPlayer;
		x = cX;
		y = cY;
		present = true;
	}
	
	int getHp(){
		return hp;
	}
	
	void setHp(int value){
		hp = value;
		if(hp <= 0){
			present = false;
		}
	}
	
	void addHp(int value){
		setHp(getHp() + value);
	}
	
	int getGameObjectType(){
		return gameObjectType;
	}
	
	int getPlayer(){
		return player;
	}
	
	int getX(){
		return x;
	}
	
	void setX(int value){
		x = value;
	}
	
	int getY(){
		return y;
	}
	
	void setY(int value){
		y = value;
	}
	
	bool isPresent(){
		return present;
	}
	
	void setPresent(bool value){
		present = value;
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
	private:
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
	
	public:
	int getPlayerCount(){
		return playerCount;
	}
	
	int getStartMoney(){
		return startMoney;
	}
	
	int getWidth(){
		return width;
	}
	
	int getHeight(){
		return height;
	}
	
	int getMyId(){
		return myId;
	}

	int getGoldCount(){
		return goldCount;
	}
	
	int getGameObjectCount(){
		return gameObjectCount;
	}
	
	int getActionCount(){
		return actionCount;
	}
	
	vector<vector<GameObject> > getWorldMap(){
		return worldMap;
	}
	
	GameObject getWorldMap(int x, int y){
		return worldMap[x][y];
	}
	
	vector<pair<int, int> > getGold(){
		return gold;
	}
	
	pair<int, int> getGold(int i){
		return gold[i];
	}
	
	set<pair<int, int> > getGoldSet(){
		return goldSet;
	}
	
	bool isGold(int x, int y){
		return goldSet.find(make_pair(x, y)) != goldSet.end();
	}
	
	vector<int> getPlayerMoney(){
		return playerMoney;
	}
	
	int getPlayerMoney(int i){
		return playerMoney[i];
	}
	
	vector<GameObject> getGameObjects(){
		return gameObjects;
	}
	
	GameObject getGameObject(int i){
		return gameObjects[i];
	}
	
	vector<Action> getActions(){
		return actions;
	}
	
	Action getAction(int i){
		return actions[i];
	}
	
	vector<GameObjectType> getGameObjectTypes(){
		return gameObjectTypes;
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
				worldMap[x][y].setPresent(false);
			}
		}
		
		gameObjects.clear();
		cin >> gameObjectCount;
		
		for(int i = 0; i < gameObjectCount; i++){
			GameObject gameObject;
			gameObject.readGameObject();
			
			gameObjects.push_back(gameObject);
			worldMap[gameObject.getX()][gameObject.getY()] = gameObject;
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
		worldMap[endX][endY].setX(endX);
		worldMap[endX][endY].setY(endY);
		worldMap[startX][startY].setPresent(false);
		
		// TODO change x & y on the object in gameObjects
	}
	
	// The object at (startX/startY) attacks the object at (endX/endY)
	void fight(int startX, int startY, int endX, int endY){
		addActionToQueue(Action(myId, 1, startX, startY, endX, endY, 0));
		
		worldMap[endX][endY].addHp(-gameObjectTypes[worldMap[startX][startY].getGameObjectType()].getDamage());
		
		// TODO change hp on the object in gameObjects
	}
	
	// The object at (startX/startY) produces a GameObject of the type produceGameObjectType at (endX/endY)
	void produce(int startX, int startY, int endX, int endY, int produceGameObjectType){
		addActionToQueue(Action(myId, 2, startX, startY, endX, endY, produceGameObjectType));
		
		worldMap[endX][endY] = GameObject(gameObjectTypes[produceGameObjectType].getDefaultHp(),
				produceGameObjectType, myId, endX, endY);
				
		playerMoney[myId] -= gameObjectTypes[produceGameObjectType].getValue();
		
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