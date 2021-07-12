/*
 * Created on 30.7.2004
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cz.kamma.kfmanager.games.samecolors;

import java.util.Random;
import java.util.Vector;

/**
 * @author e_rdavid
 * 
 * 
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class Engine {

  public int WIDTH = 15;
  public int HEIGHT = 15;

  private static final byte[] gameItems = { 1, 2, 3 };

  private int[][] gameField = new int[WIDTH][HEIGHT];

  private int score = 0;

  private static int lastReducedStones = 0;

  public Engine() {
    gameField = new int[WIDTH][HEIGHT];
  }

  public void randomizeGameField() {
    Random rnd = new Random();
    for (int y = 0; y < HEIGHT; y++) {
      for (int x = 0; x < WIDTH; x++) {
        gameField[x][y] = gameItems[rnd.nextInt(gameItems.length)];
      }
    }
  }

  public int[][] getGameField() {
    return gameField;
  }

  public void setGameField(int[][] gameField) {
    this.gameField = gameField;
  }

  public void reduceField(int x, int y, boolean preview) {
    if (x>=WIDTH || y>=HEIGHT)
      return;
    if (gameField[x][y] == 0)
      return;
    resetPreviewFields();
    reduceNeighbor(gameField, x, y, gameField[x][y], preview);
    if (!preview) {
      int reducedStones = rearangeField();
      // System.out.println("Reduced:
      // "+(reducedStones-lastReducedStones));
      score = score + ((reducedStones - lastReducedStones) * 15);
      lastReducedStones = reducedStones;
    }
  }

  private boolean reduceNeighbor(int[][] gField, int x, int y, int field, boolean preview) {
    boolean tmpRes = false;
    if (isSameField(gField, x - 1, y, field)) {
      tmpRes = true;
      if (preview)
        gField[x - 1][y] = gField[x - 1][y] + gameItems.length;
      else
        gField[x - 1][y] = 0;
      reduceNeighbor(gField, x - 1, y, field, preview);
    }
    if (isSameField(gField, x + 1, y, field)) {
      tmpRes = true;
      if (preview)
        gField[x + 1][y] = gField[x + 1][y] + gameItems.length;
      else
        gField[x + 1][y] = 0;
      reduceNeighbor(gField, x + 1, y, field, preview);
    }
    if (isSameField(gField, x, y - 1, field)) {
      tmpRes = true;
      if (preview)
        gField[x][y - 1] = gField[x][y - 1] + gameItems.length;
      else
        gField[x][y - 1] = 0;
      reduceNeighbor(gField, x, y - 1, field, preview);
    }
    if (isSameField(gField, x, y + 1, field)) {
      tmpRes = true;
      if (preview)
        gField[x][y + 1] = gField[x][y + 1] + gameItems.length;
      else
        gField[x][y + 1] = 0;
      reduceNeighbor(gField, x, y + 1, field, preview);
    }
    return tmpRes;
  }

  private boolean isSameField(int[][] gField, int x, int y, int field) {
    if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT)
      return false;
    return gField[x][y] == field;
  }

  private int rearangeField() {
    int tmpRes = dropDown();
    dropRight();
    return tmpRes;
  }

  private int dropDown() {
    int tmpRes = 0;
    for (int y = 0; y < HEIGHT; y++) {
      Vector<String> tmp = new Vector<String>();
      for (int x = 0; x < WIDTH; x++) {
        if (gameField[x][y] != 0)
          tmp.add(gameField[x][y] + "");
      }
      for (int x = 0; x < WIDTH; x++) {
        if ((tmp.size() - 1 - x) < 0) {
          gameField[(WIDTH - 1) - x][y] = 0;
          tmpRes++;
        } else
          gameField[(WIDTH - 1) - x][y] = Byte.parseByte((String) tmp.get(tmp.size() - 1 - x));
      }
    }
    return tmpRes;
  }

  private void dropRight() {
    int colsToDrop = 0;
    for (int i = 0; i < WIDTH; i++)
      if (dropColumn(i)) {
        colsToDrop++;
        for (int j = i; j > 0; j--) {
          copyColumn(j - 1, j);
        }
      }
    for (int i = 0; i < colsToDrop; i++) {
      dropColumnValues(i);
    }
  }

  private boolean dropColumn(int col) {
    if (gameField[HEIGHT - 1][col] != 0)
      return false;
    return true;
  }

  private void dropColumnValues(int col) {
    for (int y = 0; y < HEIGHT; y++) {
      gameField[y][col] = 0;
    }
  }

  private void copyColumn(int src, int des) {
    for (int i = 0; i < HEIGHT; i++)
      gameField[i][des] = gameField[i][src];
  }

  public int getFieldCount() {
    int cnt = 0;
    for (int y = 0; y < HEIGHT; y++) {
      for (int x = 0; x < WIDTH; x++) {
        if (gameField[x][y] != 0)
          cnt++;
      }
    }
    return cnt;
  }

  public int getPossibleMoves() {
    int moves = 0;
    int[][] tmpField = new int[WIDTH][HEIGHT];
    copyArray(gameField, tmpField);
    for (int y = 0; y < HEIGHT; y++) {
      for (int x = 0; x < WIDTH; x++) {
        int tmpChr = gameField[x][y];
        if (tmpChr != 0 && reduceNeighbor(tmpField, x, y, tmpChr, false))
          moves++;
      }
    }
    return moves;
  }

  private void copyArray(int[][] src, int[][] dst) {
    for (int y = 0; y < HEIGHT; y++) {
      for (int x = 0; x < WIDTH; x++) {
        dst[x][y] = src[x][y];
      }
    }
  }

  private void resetPreviewFields() {
    for (int y = 0; y < HEIGHT; y++) {
      for (int x = 0; x < WIDTH; x++) {
        if (gameField[x][y] > gameItems.length)
          gameField[x][y] = gameField[x][y] - gameItems.length;
      }
    }
  }

  public int getScore() {
    return score;
  }

  public int getFinalScore(long gameTime) {
    score = score - (getFieldCount() * 20);
    score = score - (new Long(gameTime).intValue() * 3);
    return score;
  }

  /**
   * 
   */
  public void initialize() {
    lastReducedStones = 0;
    score = 0;
    randomizeGameField();

  }

  public int getHEIGHT() {
    return HEIGHT;
  }

  public int getWIDTH() {
    return WIDTH;
  }

}
