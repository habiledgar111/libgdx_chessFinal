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
    
    api = new Stockfish_Api();

    this.depth = depth;
    this.isPlayerWhite = isPlayerWhite;
  }

  public void startGame(){
    if(!isPlayerWhite){
      triggerEngineMove();
    }
  }

  private void triggerEngineMove(){
    isWaitingApi = true;
    String fanSnapshot = board.getFen();

    getBestMoved(fanSnapshot).thenAccept(uci -> {
      if( uci == null){
        isWaitingApi = false;
        return;
      }

      var side = board.getSideToMove();
      Move best = new Move(uci, side);
      Gdx.app.postRunnable(() -> {
        if(!board.getFen().equals(fanSnapshot)){
          isWaitingApi = false;
          return;
        }
        if(board.legalMoves().contains(best)){
          board.doMove(best);
        }
        isWaitingApi = false;
      });
    });
  }

  public void move(String UCI) {

    if (isWaitingApi)
      return;

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

          if (uci == null){
            isWaitingApi = false;
            return;
          }

          var side = board.getSideToMove();
          Move best = new Move(uci, side);

          Gdx.app.postRunnable(() -> {
            if (!board.getFen().equals(fenSnapshot)) {
              System.out.println("board changed, ignoring engine move");
              isWaitingApi = false;
              return;
            }
            System.out.println("Engine mencoba langkah: " + uci);
            try {
              if (board.legalMoves().contains(best)) {
                board.doMove(best);
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

  public boolean isWaiting() {
    return isWaitingApi;
  }
}
