package minesweeper;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;


import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;

public class App extends PApplet {

    public static final int WIDTH = 864;//600
    public static final int HEIGHT = 640;//360
    public static final int NUM_ROWS = 27;//30
    public static final int NUM_COLUMNS = 18;//16
    public static final int CELLSIZE = 32;//20
    public static final int TOPBAR = 64;//40

    public static final int FPS = 30;

    public String configPath;

    public static Random random = new Random();
	
	public static int[][] mineCountColour = new int[][] {
            {0,0,0}, // 0 is not shown
            {0,0,255},
            {0,133,0},
            {255,0,0},
            {0,0,132},
            {132,0,0},
            {0,132,132},
            {132,0,132},
            {32,32,32}
    };

//    public static Tile[][] board = new Tile[NUM_ROWS][NUM_COLUMNS];
    public static int[][] board = new int[NUM_ROWS][NUM_COLUMNS];

    private int totalMines;
//    private boolean firstPlay;
    private boolean gameWin;
    private int lx, ly;
    private int startTime;
    private boolean timerRunning  = true;

    private PImage mine;
    private PImage tile;
    private PImage tile1;
    private PImage tile2;
    private PImage flag;

	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {
        frameRate(FPS);
		//See PApplet javadoc:
		//loadJSONObject(configPath)

        gameWin = false;
        startTime = millis();

        rectMode(CORNER);
        textSize(14);

        totalMines = 0;
        lx = -1;
        ly = -1;
        gameWin = false;

        tile = loadImage("src/main/resources/minesweeper/tile.png");
        tile1 = loadImage("src/main/resources/minesweeper/tile1.png");
        tile2 = loadImage("src/main/resources/minesweeper/tile2.png");
        mine = loadImage("src/main/resources/minesweeper/mine0.png");
        flag = loadImage("src/main/resources/minesweeper/flag.png");

        for (int i = 0; i < NUM_ROWS; i++)
        {
            for (int j = 0; j < NUM_COLUMNS; j++)
            {
                if (random(0,1) < 0.2)
                {
                    board[i][j] = 9;
                    totalMines++;
                }
                else
                {
                    board[i][j] = -1;
                }
            }
        }
    }



    void openSpace(int x, int y)
    {
        // Yo, you hit a mine.
        if (board[x][y] == 9)
        {
            gameLoss(x,y);
        }

        board[x][y] = countNearbyMines(x,y);

        if (countNearbyMines(x,y) == 0)
        {
            // quick algorithm for examining all surrounding cells, as before.
            for (int i = 0; i < 3; i++)
            {
                for (int j = 0; j < 3; j++)
                {
                    // Check if out of bounds of game board, as before.
                    if ((x+i-1) < 0 || (y+j-1) < 0 || (x+i-1) >= 30 || (y+j-1) >= 16)
                    {
                        continue;
                    }
                    // If a surrounding nearby cell that could be dug into doesn't have a mine and
                    // there's no mines around it, dig into that one too automatically through recursion.
                    else if (board[x+i-1][y+j-1] == -1 && countNearbyMines(x+i-1,y+j-1) == 0)
                    {
                        board[x+i-1][y+j-1] = countNearbyMines(x+i-1,y+j-1);
                        openSpace(x+i-1,y+j-1);
                    }
                    // This bit ends this leg of recursion when it finds a cell that has nearby mines.
                    else
                    {
                        board[x+i-1][y+j-1] = countNearbyMines(x+i-1,y+j-1);
                    }
                }
            }
        }
    }

    int countNearbyMines(int x, int y)
    {
        int mineCount = 0;
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                if ((x+i-1) < 0 || (y+j-1) < 0 || (x+i-1) >= 30 || (y+j-1) >= 16)
                {
                    continue;
                }
                else
                {
                    if (board[x+i-1][y+j-1] == 9 || board[x+i-1][y+j-1] == 10)
                    {
                        mineCount++;
                    }
                }
            }
        }
        return mineCount;
    }

    int countNearbyFlags(int x, int y)
    {
        int flagCount = 0;
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                if ((x+i-1) < 0 || (y+j-1) < 0 || (x+i-1) >= 30 || (y+j-1) >= 16)
                {
                    continue;
                }
                else
                {
                    if (board[x+i-1][y+j-1] == 10 || board[x+i-1][y+j-1] == 11)
                    {
                        flagCount++;
                    }
                }
            }
        }
        return flagCount;
    }

    void openNearbySafeSpaces(int x, int y)
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                if ((x+i-1) < 0 || (y+j-1) < 0 || (x+i-1) >= 30 || (y+j-1) >= 16)
                {
                    continue;
                }
                else
                {
                    // This is what happens when the automatic move is successul.
                    if (board[x+i-1][y+j-1] == -1)
                    {
                        openSpace(x+i-1,y+j-1);
                    }
                    // But sometimes it makes you lose because it assumes your incorrectly placed flag
                    // was a correctly placed flag.
                    else if (board[x+i-1][y+j-1] == 9)
                    {
                        gameLoss(x+i-1,y+j-1);
                    }
                }
            }
        }
    }

    void gameLoss(int x, int y)
    {
        lx = x;
        ly = y;
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        // 'ENTER' restarts the game only if you've won or lost the game.
        if (keyCode == 'R' && (lx != -1 || gameWin))
        {
            timerRunning = true;
            setup();
        }
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // This if statement on the outside prevents mouse input if the game is won or lost.
        if (lx == -1 && !gameWin)
        {

            int mx = floor(mouseX / CELLSIZE);
            int my = floor((mouseY-TOPBAR) / CELLSIZE);

            if (mouseButton == LEFT)
            {
                if (board[mx][my] == 9 || board[mx][my] == 10)
                {
                    gameLoss(mx,my);
                }
                else
                {
                    openSpace(mx,my);
                }
            }

            else if(mouseButton == RIGHT)
            {
                // If you flag a mine, that cell becomes a correctly flagged cell.
                if (board[mx][my] == 9)
                {
                    board[mx][my] = 10;
                    totalMines--;
                }
                // If you flag a cell that is not a mine, it becomes an incorrectly flagged cell.
                else if (board[mx][my] == -1)
                {
                    board[mx][my] = 11;
                    totalMines--;
                }
                // If you unflag a correctly flagged cell, remove the flag and put a mine back inside.
                else if (board[mx][my] == 10)
                {
                    board[mx][my] = 9;
                    totalMines++;
                }
                // If you unflag an incorrectly flagged cell, remove the flag and put a -1 back inside it,
                // which represents that the cell is back to being open and available to dig into.
                else if (board[mx][my] == 11)
                {
                    board[mx][my] = -1;
                    totalMines++;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {


        // Display the remaining mines at the top of the screen
        background(200,200,200);
        fill(0,0,0);
        text("Remaining Mines: "+totalMines,15,25);

        if (timerRunning) {
            int elapsedTime = millis() - startTime; // Calculate elapsed time in milliseconds
            int seconds = (elapsedTime / 1000) % 60; // Convert milliseconds to seconds

            // Display the seconds as SS
            String timeText = nf(seconds, 2);
            fill(0); // Black text color
            text("Times:" + timeText + "s", 500, 25);
        }

        // Draw the Board
        stroke(0);
        for (int i = 0; i < NUM_ROWS; i++)
        {
            for (int j = 0; j < NUM_COLUMNS; j++)
            {
                // If it's not a mine, draw the number with the corresponding color
                if (board[i][j] >= 0 && board[i][j] <= 8)
                {
                    image(tile,i*CELLSIZE,TOPBAR+(j*CELLSIZE),CELLSIZE,CELLSIZE);

                    switch (board[i][j])
                    {
                        case 0: fill(0,0,0); break;
                        case 1: fill(0,0,255); break;
                        case 2: fill(0,133,0); break;
                        case 3: fill(255,0,0); break;
                        case 4: fill(0,0,132); break;
                        case 5: fill(132,0,0); break;
                        case 6: fill(0,132,132); break;
                        case 7: fill(132,0,132); break;
                        case 8: fill(32,32,32); break;
                        default: fill(255); break;
                    }


                    if (board[i][j] != 0)
                    {
//                        text(board[i][j],6+(i*CELLSIZE),55+(j*CELLSIZE));
                        text(board[i][j],10+(i*CELLSIZE),85+(j*CELLSIZE));
                        if (countNearbyFlags(i,j) == board[i][j])
                        {
                            openNearbySafeSpaces(i,j);
                        }
                    }
                }
                // This draws the flags as red circles, whether they're correctly placed or not
                else if (board[i][j] == 10 || board[i][j] == 11)
                {
                    image(flag,i*CELLSIZE,TOPBAR+(j*CELLSIZE),CELLSIZE,CELLSIZE);
                }
                // This part draws the spaces you haven't dug into yet as darker spaces.
                else
                {
                    image(tile1,i*CELLSIZE,TOPBAR+(j*CELLSIZE),CELLSIZE,CELLSIZE);
                }
            }
        }

        // If you've lost the game
        if (lx != -1)
        {
            // This shows all of the mines.
            timerRunning = false;
            for (int i = 0; i < NUM_ROWS; i++)
            {
                for (int j = 0; j < NUM_COLUMNS; j++)
                {
                    if (board[i][j] == 9)
                    {
                        image(mine,i*CELLSIZE,TOPBAR+(j*CELLSIZE),CELLSIZE,CELLSIZE);
                    }
                }
            }

            // Displays the game losing move in purple and also the "Try again" text at the top
            fill(190,0,190);
            text("Press R to try again!",200,25);
        }

        // Test to see if you've won the game.
        if (totalMines == 0)
        {
            // If you have any false flags, you haven't won yet.
            int falseFlags = 0;
            for (int i = 0; i < NUM_ROWS; i++)
            {
                for (int j = 0; j < NUM_COLUMNS; j++)
                {
                    if (board[i][j] == 11)
                    {
                        falseFlags++;
                    }
                }
            }

            // If none of the flags are falsely placed and no mines are left, you win.
            if (falseFlags == 0)
            {
                fill(190,0,190);
                text("YOU WIN! Press Enter to play again!",200,25);
                gameWin = true;
            }
        }
    }


    public static void main(String[] args) {
        PApplet.main("minesweeper.App");
    }

}
