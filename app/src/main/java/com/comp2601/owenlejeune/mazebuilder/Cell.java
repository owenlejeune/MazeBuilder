package com.comp2601.owenlejeune.mazebuilder;

import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by owenlejeune on 2018-02-23.
 */

public class Cell {

    public static final byte FLOOR = 0;
    public static final byte WALL = 1;
    public static final byte START = 3;
    public static final byte END = 4;

    private final int x;
    private final int y;


    private ArrayList<Cell> neighbours = new ArrayList<>();

    private Maze parent;

    private byte state;
    private boolean visited = false;
    private boolean inRoute = false;

    private Button button;

    public Cell(int x, int y, Maze parent, Button button){
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.button = button;
        this.state = FLOOR;
    }

    public void setNeighbours() {
        neighbours = new ArrayList<>();

        if(x != 0) addNeighbour(x-1, y);

        if(y != 0) addNeighbour(x, y-1);

        if(x < parent.getNumCols()-1) addNeighbour(x+1, y);

        if(y < parent.getNumRows()-1) addNeighbour(x, y+1);
    }

    private void addNeighbour (int x, int y) {
        Cell neighbour = parent.getCell(x, y);
        if (!(neighbour.getState() == WALL)) neighbours.add(neighbour);
    }

    public void reset () {
        neighbours = new ArrayList<>();
        visited = false;
        inRoute = false;
    }

    public byte getState() {
        return state;
    }

    public int getX () {
        return x;
    }

    public int getY () {
        return y;
    }

    public ArrayList<Cell> getNeighbours() {
        return neighbours;
    }

    public Maze getParent() {
        return parent;
    }

    public Button getButton() {
        return button;
    }

    public Cell getAnUnvisitedNeighbour() {
        if (neighbours == null) return null;

        if (neighbours.size() == 0) return null;

        for (int i = 0; i < neighbours.size (); i++) {
            Cell next = neighbours.get(i);
            if (!next.isVisited()){
                return next;
            }
        }

        return null;
    }

    public boolean inRoute() {
        return inRoute;
    }

    public void setInRoute() {
        inRoute = true;
    }

    public void setFloor(){
        state = FLOOR;
    }

    public void setWall(){
        state = WALL;
    }

    public void setStart(){
        state = START;
    }

    public void setEnd(){
        state = END;
    }

    public boolean isVisited () {
        return visited;
    }

    public void visited() {
        visited = true;
    }
}
