package com.smeanox.games.sg002.world;

import java.awt.Point;

/**
 * Created by User on 09.06.2015.
 */
public class MapObject {

    public final int x,y;

    private MapObjectType mapObjectType;

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
}
