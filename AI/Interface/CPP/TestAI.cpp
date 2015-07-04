#include <iostream>
#include <vector>
#include <cstdlib>
#include <cmath>
#include "SG002.cpp"

using namespace std;

pair<int, int> getRandomPosition(GameWorld& gameWorld, GameObject gameObject, int minRadius, int maxRadius, bool free, bool needGold){
	int startX, startY;
	startX = gameObject.getX();
	startY = gameObject.getY();
	
	vector<pair<int, int> > positions;
	for(int x = startX - maxRadius; x <= startX + maxRadius; x++){
		for(int y = startY - maxRadius; y <= startY + maxRadius; y++){
			if(x < 0 || y < 0 || x >= gameWorld.getWidth() || y >= gameWorld.getHeight()){
				continue;
			}
			if(max(abs(startX - x), abs(startY - y)) < minRadius){
				continue;
			}
			if(gameWorld.getWorldMap(x, y).isPresent() != free
					&& (free || gameWorld.getWorldMap(x, y).getPlayer() != gameWorld.getMyId())
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
	for(int i = 0; i < gameWorld.getGameObjectCount(); i++){
		GameObject aObject = gameWorld.getGameObject(i);
		if(aObject.getPlayer() == gameWorld.getMyId()){
			GameObjectType gameObjectType = gameWorld.getGameObjectType(aObject.getGameObjectType());
			
			// Produce if we can
			if(gameObjectType.isCanProduce()){
				vector<int> options = gameWorld.getGameObjectType(aObject.getGameObjectType()).getProduceList();
				int newGameObjectType = options[rand() % options.size()];
				pair<int, int> pos = getRandomPosition(gameWorld, aObject,
						gameObjectType.getRadiusProduceMin(), gameObjectType.getRadiusProduceMax(), true, newGameObjectType == 1);
				
				if(pos != make_pair(-1, -1) && gameWorld.getGameObjectType(newGameObjectType).getValue() <= gameWorld.getPlayerMoney(gameWorld.getMyId())){
					gameWorld.produce(aObject.getX(), aObject.getY(), pos.first, pos.second, newGameObjectType);
				}
			}
			
			// Fight if we can
			if(gameObjectType.isCanFight()){
				pair<int, int> pos = getRandomPosition(gameWorld, aObject,
						gameObjectType.getRadiusFightMin(), gameObjectType.getRadiusFightMax(), false, false);
				
				if(pos != make_pair(-1, -1)){
					gameWorld.fight(aObject.getX(), aObject.getY(), pos.first, pos.second);
				}
			}
			
			// Move if we can
			if(gameObjectType.isCanMove()){
				pair<int, int> pos = getRandomPosition(gameWorld, aObject,
						gameObjectType.getRadiusWalkMin(), gameObjectType.getRadiusWalkMax(), true, false);
				
				if(pos != make_pair(-1, -1)){
					gameWorld.move(aObject.getX(), aObject.getY(), pos.first, pos.second);
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