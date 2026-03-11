package com.habil.game.logic;

import com.badlogic.gdx.Gdx;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import com.habil.game.engine.Stockfish;

public class GameState {
  private Board board;
  private Stockfish engine;
  boolean isPlayerWhite = true;
  int depth = 7;

  public GameState(boolean isPlayerWhite, int depth) {
    this.board = new Board();
    engine = new Stockfish();
    try {
      engine.start();
      System.out.println("engine success run");
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }

    stockFishCommand("uci");
    stockFishCommand("isready");

    this.depth = depth;
    this.isPlayerWhite = isPlayerWhite;
    if (!isPlayerWhite) {
      stockFishCommand("position fen " + board.getFen());
      stockFishCommand("go depth " + depth);
      getBestMoved();
    }
  }

  public void move(String UCI) {
    Move move = new Move(UCI, board.getSideToMove());
    if (!board.legalMoves().contains(move)) {
      System.out.println("move illegal");
      return;
    }
    board.doMove(move);
    new Thread(() -> {
      stockFishCommand("position fen " + board.getFen());
      stockFishCommand("go depth " + depth);
      getBestMoved();
    }).start();
  }

  public void move(Move move) {
    if (!board.legalMoves().contains(move)) {
      System.out.println("move illegal");
      return;
    }
    board.doMove(move);
    new Thread(() -> {
      stockFishCommand("position fen " + board.getFen());
      stockFishCommand("go depth " + depth);
      getBestMoved();
    }).start();
  }

  private void stockFishCommand(String command) {
    try {
      engine.sendCommand(command);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
  }

  public void getBestMoved() {
    try {
      String bestMove = engine.getOutput();
      bestMove = bestMove.split(" ")[1];
      Move best = new Move(bestMove, board.getSideToMove());
      Gdx.app.postRunnable(() -> {
        LogMove(best);
        board.doMove(best);
      });
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
  }

  public void LogMove(Move move) {
    System.out.println(move.getFrom().toString() + move.getTo().toString());
  }

  public Board getBoard() {
    return board;
  }
}
