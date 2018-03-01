package com.comp2601.owenlejeune.mazebuilder;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MazeActivity extends AppCompatActivity {

    private static final int MAZE_ROWS = 13;
    private static final int MAZE_COLS = 10;
    public static final int PADDING = 5;
    private static final long WAIT_TIME_MILLIS = 400;
    private static final String MISSING_START_END_ERROR = "Cannot build maze without start and finish!";

    private Maze maze;
    private int numInRoute;

    private class MazeBuilder extends AsyncTask <Void, Cell, Integer>{
        @Override
        protected Integer doInBackground(Void... arg0){
            numInRoute = 0;
            maze.reset();
            drawMaze();
            maze.setNeighbours();
            setCellButtonsEnabled(false);
            this.findPathFrom(maze.getStart());
            return numInRoute;
        }

        @Override
        protected void onPostExecute(Integer result){
            //re-colour all cells not in the solution route back to floor colour
            for(Cell cell : maze.getRoute()){
                if(!cell.inRoute()){
                    int colour = getResources().getColor(R.color.floor);
                    cell.getButton().setBackgroundColor(colour);
                }
            }
            String message = (result > 0) ? "Success!" : "No path found!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            setCellButtonsEnabled(true);
        }

        @Override
        protected void onProgressUpdate(Cell... values){
            //draw cells in route as the maze finds a path
            Cell cell = values[0];
            maze.getRoute().add(cell);
            int colour = getResources().getColor(R.color.path);
            cell.getButton().setBackgroundColor(colour);
        }

        private boolean findPathFrom(Cell cell){
            cell.visited(); //mark cell as being visited (so the problem will get smaller)

            if (cell == maze.getFinish()) {
                cell.setInRoute();
                numInRoute++;
                return true;
            }

            Cell next = cell.getAnUnvisitedNeighbour(); //get an unvisited neighbour of cell

            while(next != null) {
                if(next.getState() != Cell.END) publishProgress(next);
                try {
                    Thread.sleep(WAIT_TIME_MILLIS); //put the thread to sleep so the path animates nicely
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                if(findPathFrom(next)){
                    cell.setInRoute();
                    numInRoute++;
                    return true;
                }
                next = cell.getAnUnvisitedNeighbour();
            }
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        maze = new Maze(MAZE_ROWS, MAZE_COLS, this);
        buildMaze(maze);
    }

    private void buildMaze(Maze maze){
        //generate a maze layout
        LinearLayout mazeLayout = new LinearLayout(this);
        mazeLayout.setOrientation(LinearLayout.VERTICAL);
        mazeLayout.setPadding(PADDING, PADDING, PADDING,PADDING);
        mazeLayout.setGravity(Gravity.CENTER);

        for(byte row=0; row<MAZE_ROWS; row++){
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setPadding(PADDING, PADDING,PADDING, PADDING);
            for(byte col=0; col<MAZE_COLS; col++){
                Cell cell = maze.getCell(col, row);
                Button button = colourCell(cell);
                button.setHeight(button.getMeasuredWidth());

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.weight=.1f;
                params.setMargins(PADDING, PADDING, PADDING, PADDING);

                rowLayout.addView(button, params);
            }
            mazeLayout.addView(rowLayout);
        }
        setContentView(mazeLayout);
    }

    public void drawCell(final Cell cell){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                colourCell(cell);
            }
        });
    }

    public Button colourCell(Cell cell){
        Button button = cell.getButton();
        switch (cell.getState()) {
            case Cell.FLOOR:
                button.setBackgroundColor(getResources().getColor(R.color.floor));
                break;
            case Cell.WALL:
                button.setBackgroundColor(getResources().getColor(R.color.wall));
                break;
            case Cell.START:
                button.setBackgroundColor(getResources().getColor(R.color.path_start));
                break;
            case Cell.END:
                button.setBackgroundColor(getResources().getColor(R.color.path_end));
                break;
        }
        return button;
    }

    public void drawMaze(){
        for(Cell[] row : maze.getCells()){
            for(Cell col : row){
                drawCell(col);
            }
        }
    }

    public void setCellButtonsEnabled(final boolean enabled){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(Cell[] row : maze.getCells()){
                    for(Cell col : row){
                        col.getButton().setEnabled(enabled);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.build_maze:
                if(maze.isPlacingEnd() || maze.isPlacingStart()){
                    Toast.makeText(getApplicationContext(), MISSING_START_END_ERROR,
                            Toast.LENGTH_LONG).show();
                    break;
                }
                MazeBuilder builder = new MazeBuilder();
                builder.execute();
                break;
            case R.id.reset:
                maze = new Maze(MAZE_ROWS, MAZE_COLS, this);
                buildMaze(maze);

        }
        return true;
    }
}
