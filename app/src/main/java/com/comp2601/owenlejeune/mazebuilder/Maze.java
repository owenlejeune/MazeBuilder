package com.comp2601.owenlejeune.mazebuilder;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by owenlejeune on 2018-02-23.
 */

public class Maze {

    public static final int PADDING = 5;

    private int numRows;
    private int numCols;
    private Cell[][] cells;
    private Cell start;
    private Cell finish;
    private MazeActivity context;
    private ArrayList<Cell> route;

    private boolean placingStart = false;
    private boolean placingEnd = false;

    private HashMap<Button, Cell> buttonCellHashMap;

    private final View.OnClickListener buttonOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Button clicked = (Button)v;
            Cell cell = buttonCellHashMap.get(clicked);
            if(cell == null) return;

            switch (cell.getState()){
                case Cell.FLOOR:
                    if(placingStart){
                        cell.setStart();
                        finish.getButton().setEnabled(true);
                        placingStart = false;
                        start = cell;
                    }else if(placingEnd){
                        cell.setEnd();
                        start.getButton().setEnabled(true);
                        placingEnd = false;
                        finish = cell;
                    }else{
                        cell.setWall();
                    }
                    break;
                case Cell.WALL:
                    if(placingStart){
                        cell.setStart();
                        finish.getButton().setEnabled(true);
                        placingStart = false;
                        start = cell;
                    }else if(placingEnd){
                        cell.setEnd();
                        start.getButton().setEnabled(true);
                        placingEnd = false;
                        finish = cell;
                    }else{
                        cell.setFloor();
                    }
                    break;
                case Cell.START:
                    finish.getButton().setEnabled(false);
                    placingStart = true;
                    cell.setFloor();
                    break;
                case Cell.END:
                    start.getButton().setEnabled(false);
                    cell.setFloor();
                    placingEnd = true;
                    break;
            }
            context.drawCell(cell);
        }
    };

    public Maze(int rows, int columns, MazeActivity context){
        this.numCols = columns;
        this.numRows = rows;
        this.context = context;
        cells = new Cell[numRows][numCols];
        buttonCellHashMap = new HashMap<>();
        route = new ArrayList<>();
        initialize();
    }

    private void initialize(){
        for(byte row=0; row<numRows; row++){
            for(byte col=0; col<numCols; col++){
                Button button = new Button(context);
                button.setPadding(PADDING, PADDING, PADDING, PADDING);
                button.setOnClickListener(buttonOnClickListener);
                Cell cell = new Cell(col, row, this, button);
                cells[row][col] = cell;
                buttonCellHashMap.put(button, cell);
            }
        }
        setStart(0, 0);
        setEnd(numCols-1, numRows-1);
    }

    public void setNeighbours() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                cells[i][j].setNeighbours();
            }
        }
    }

    public void setStart(int x, int y){
        cells[y][x].setStart();
        start = cells[y][x];
    }

    public void setEnd(int x, int y){
        cells[y][x].setEnd();
        finish = cells[y][x];
    }

    public Cell getCell(int x, int y) {
        if((x < 0) || (y < 0)) return null;

        if((x >= numCols) || (y >= numRows)) return null;

        return cells[y][x];
    }

    public ArrayList<Cell> getRoute() {
        return route;
    }

    public void reset() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                cells[i][j].reset();
            }
        }
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public Cell getStart() {
        return start;
    }

    public Cell getFinish() {
        return finish;
    }

    public boolean isPlacingStart() {
        return placingStart;
    }

    public boolean isPlacingEnd() {
        return placingEnd;
    }
}
