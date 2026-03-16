package com.habil.game.logic;

import java.util.concurrent.CompletableFuture;

import com.badlogic.gdx.Gdx;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import com.habil.game.engine.Stockfish_Api;

public class GameState {
  private Board board;
  // private Stockfish engine;
  private Stockfish_Api api;
  boolean isPlayerWhite = true;
  int depth = 7;

  private boolean isWaitingApi = false;

  public GameState(boolean isPlayerWhite, int depth) {
    this.board = new Board();
    // engine = new Stockfish();
    api = new Stockfish_Api();
    // try {
    // engine.start();
    // System.out.println("engine success run");
    // } catch (Exception e) {
    // e.printStackTrace();
    // System.out.println(e.getMessage());
    // }

    // stockFishCommand("uci");
    // stockFishCommand("isready");

    this.depth = depth;
    this.isPlayerWhite = isPlayerWhite;
    if (!isPlayerWhite) {
      // stockFishCommand("position fen " + board.getFen());
      // stockFishCommand("go depth " + depth);

      String fanSnapShot = board.getFen();
      getBestMoved(fanSnapShot).thenAccept(uci -> {
        if (uci == null)
          return;
        Move best = new Move(uci, board.getSideToMove());

        Gdx.app.postRunnable(() -> {
          if (!board.getFen().startsWith(fanSnapShot.split(" ")[0])) {
            System.out.println("board changed, ignoring engine move");
            return;
          }
          if (!board.legalMoves().contains(best)) {
            System.out.println("engine move illegal: " + best);
            return;
          }

          LogMove(best);
          board.doMove(best);

        });
      });
      // new Thread(() -> {
      // getBestMoved(fanSnapShot);
      // }).start();
    }
  }

  public void move(String UCI) {

    if(isWaitingApi)return;

    Move move = new Move(UCI, board.getSideToMove());

    if (!board.legalMoves().contains(move)) {
      System.out.println("move illegal");
      return;
    }

    board.doMove(move);
    isWaitingApi = true;

    String fenSnapshot = board.getFen();

    getBestMoved(fenSnapshot)
        .thenAccept(uci -> {

          if (uci == null)
            return;

          Move best = new Move(uci, board.getSideToMove());

          Gdx.app.postRunnable(() -> {
            System.out.println("Engine mencoba langkah: " + uci);
            try {
              Move bestMove = new Move(uci, board.getSideToMove());
              if (board.legalMoves().contains(bestMove)) {
                board.doMove(bestMove);
                System.out.println("FEN Sekarang: " + board.getFen());
              } else {
                System.err.println("Langkah Engine Ilegal menurut library!");
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
            isWaitingApi = false;
          });

        });

  }

  // private void stockFishCommand(String command) {
  // try {
  // engine.sendCommand(command);
  // } catch (Exception e) {
  // e.printStackTrace();
  // System.out.println(e.getMessage());
  // }
  // }

  public CompletableFuture<String> getBestMoved(String fenSnapshot) {

    return api.fetchBestMove(fenSnapshot, depth)
        .thenApply(dto -> {

          if (dto == null || dto.getMove() == null) {
            System.out.println("engine return null move");
            return null;
          }

          return dto.getMove(); // UCI string
        });

  }

  public void LogMove(Move move) {
    System.out.println(move.getFrom().toString() + move.getTo().toString());
  }

  public Board getBoard() {
    return board;
  }

  public boolean isWaiting(){
    return isWaitingApi;
  }
}
