package com.smeanox.games.sg002.world;

import java.awt.Point;

/**
 * Describes the mapObjectType for a given position
 * @author Fabian Lyck
 */
public class MapObject {

    private final int x, y;

    private MapObjectType mapObjectType;

    /**
     * Create a new instance
     * @param type the type of this MapObject
     * @param x position
     * @param y position
     */
    public MapObject(MapObjectType type, int x, int y){

        this.mapObjectType = type;
        this.x = x;
        this.y = y;
    }

    public MapObjectType getMapObjectType(){
        return mapObjectType;
    }

    public void setMapObjectType(MapObjectType mapObjectType){
        this.mapObjectType = mapObjectType;
    }

    public Point getPosition(){
        return new Point(x,y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
