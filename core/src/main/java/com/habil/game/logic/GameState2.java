package com.habil.game.logic;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.badlogic.gdx.Gdx;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.habil.game.engine.Stockfish_Api;

public class GameState2 {

  public enum TurnState {
    PLAYER_TURN,
    ENGINE_TURN,
    GAME_OVER
  }

  private Board board;
  private Stockfish_Api api;

  private boolean isPlayerWhite;
  private int depth;

  private TurnState state;

  public GameState2(boolean isPlayerWhite, int depth) {
    this.board = new Board();
    this.api = new Stockfish_Api();
    this.isPlayerWhite = isPlayerWhite;
    this.depth = depth;
    initGame();
  }

  private void initGame() {
    if (isPlayerWhite) {
      state = TurnState.PLAYER_TURN;
    } else {
      state = TurnState.ENGINE_TURN;
      triggerEngineMove();
    }
  }

  public void playerMove(String uci) {
    if (state != TurnState.PLAYER_TURN) {
      return;
    }

    Move move = new Move(uci, board.getSideToMove());

    if (!board.legalMoves().contains(move)) {
      System.out.println("Illegal Move");
      return;
    }

    board.doMove(move);

    if (isGameOver())
      return;

    state = TurnState.ENGINE_TURN;
    triggerEngineMove();
  }

  private void triggerEngineMove() {
    String fenSnapshot = board.getFen();

    Side sideSnapshot = board.getSideToMove();

    CompletableFuture<String> future = api.fetchBestMove(fenSnapshot, depth).thenApply(dto -> {
      if (dto == null || dto.getMove() == null)
        return null;
      return dto.getMove();
    });

    future.thenAccept(uci -> {
      if (uci == null) {
        state = TurnState.PLAYER_TURN;
        return;
      }
      Move bestMove = new Move(uci, sideSnapshot);
      Gdx.app.postRunnable(() -> {
        if (!board.getFen().equals(fenSnapshot)) {
          System.out.println("skip engine move (state changed)");
          return;
        }

        if (state != TurnState.ENGINE_TURN) {
          System.out.println("skip engine move (wring state)");
          return;
        }

        if (!board.legalMoves().contains(bestMove)) {
          System.out.println("engince move illegal");
          return;
        }

        board.doMove(bestMove);

        if (isGameOver())
          return;
        state = TurnState.PLAYER_TURN;
      });
    });
  }

  private boolean isGameOver() {
    if (board.isMated() || board.isDraw()) {
      state = TurnState.GAME_OVER;
      System.out.println("game over");
      return true;
    }
    return false;
  }

  public boolean isPlayerTurn() {
    return state == TurnState.PLAYER_TURN;
  }

  public boolean isWaiting() {
    return state == TurnState.ENGINE_TURN;
  }

  public TurnState getState() {
    return state;
  }

  public Piece getPiece(Square piece){
    return board.getPiece(piece);
  }

  public List<Move> getLegalMove(){
    return MoveGenerator.generateLegalMoves(board);
  }

}
