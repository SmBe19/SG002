#include <iostream>
#include <vector>
#include <cstdlib>
#include <cmath>
#include "SG002.cpp"

using namespace std;

pair<int, int> getRandomPosition(GameWorld& gameWorld, GameObject gameObject, int minRadius, int maxRadius, bool free, bool needGold){
	int startX, startY;
	startX = gameObject.x;
	startY = gameObject.y;
	
	vector<pair<int, int> > positions;
	for(int x = startX - maxRadius; x <= startX + maxRadius; x++){
		for(int y = startY - maxRadius; y <= startY + maxRadius; y++){
			if(x < 0 || y < 0 || x >= gameWorld.width || y >= gameWorld.height){
				continue;
			}
			if(max(abs(startX - x), abs(startY - y)) < minRadius){
				continue;
			}
			if(gameWorld.getWorldMap(x, y).present != free
					&& (free || gameWorld.getWorldMap(x, y).player != gameWorld.myId)
					&& (!needGold || gameWorld.isGold(x, y))){
				positions.push_back(make_pair(x, y));
			}
		}
	}

	if(positions.empty()){
		return make_pair(-1, -1);
	}
	
	return positions[rand() % positions.size()];
}

void play(GameWorld& gameWorld){
	for(int i = 0; i < gameWorld.gameObjectCount; i++){
		GameObject aObject = gameWorld.gameObjects[i];
		if(aObject.player == gameWorld.myId){
			GameObjectType gameObjectType = gameWorld.getGameObjectType(aObject.gameObjectType);
			
			// Produce if we can
			if(gameObjectType.canProduce){
				vector<int> options = gameWorld.getGameObjectType(aObject.gameObjectType).produceList;
				int newGameObjectType = options[rand() % options.size()];
				pair<int, int> pos = getRandomPosition(gameWorld, aObject,
						gameObjectType.radiusProduceMin, gameObjectType.radiusProduceMax, true, newGameObjectType == 1);
				
				if(pos != make_pair(-1, -1) && gameWorld.getGameObjectType(newGameObjectType).value <= gameWorld.getPlayerMoney(gameWorld.myId)){
					gameWorld.produce(aObject.x, aObject.y, pos.first, pos.second, newGameObjectType);
				}
			}
			
			// Fight if we can
			if(gameObjectType.canFight){
				pair<int, int> pos = getRandomPosition(gameWorld, aObject,
						gameObjectType.radiusFightMin, gameObjectType.radiusFightMax, false, false);
				
				if(pos != make_pair(-1, -1)){
					gameWorld.fight(aObject.x, aObject.y, pos.first, pos.second);
				}
			}
			
			// Move if we can
			if(gameObjectType.canMove){
				pair<int, int> pos = getRandomPosition(gameWorld, aObject,
						gameObjectType.radiusWalkMin, gameObjectType.radiusWalkMax, true, false);
				
				if(pos != make_pair(-1, -1)){
					gameWorld.move(aObject.x, aObject.y, pos.first, pos.second);
				}
			}
		}
	}
}

int main(){
	srand(12345);
	
	startGame();
	
	return 0;
}