package com.habil.game.screen;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.habil.game.logic.GameState;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class ChessScreen implements Screen {
  Map<Piece, Texture> texturePiece;
  GameState game;

  SpriteBatch batch;
  ShapeRenderer renderer;
  private int tileSize = 80;
  OrthographicCamera camera;
  Viewport view;
  final float WORLD_SIZE = 640;

  Board board;
  String selectedSquare = null;

  public ChessScreen(boolean iswhite, int depth){
    game = new GameState(iswhite, depth);
  }

  @Override
  public void show() {
    // Prepare your screen here.
    texturePiece = new HashMap<>();
    initTexturePieceLibrary();

    batch = new SpriteBatch();
    renderer = new ShapeRenderer();
    camera = new OrthographicCamera();
    view = new FitViewport(WORLD_SIZE, WORLD_SIZE, camera);


    camera.position.set(WORLD_SIZE / 2f, WORLD_SIZE / 2f, 0);
    camera.update();
  }

  private void initTexturePieceLibrary() {
    texturePiece.put(Piece.WHITE_PAWN, new Texture("chess/Chess_plt45.svg.png"));
    texturePiece.put(Piece.WHITE_KNIGHT, new Texture("chess/Chess_nlt45.svg.png"));
    texturePiece.put(Piece.WHITE_BISHOP, new Texture("chess/Chess_blt45.svg.png"));
    texturePiece.put(Piece.WHITE_ROOK, new Texture("chess/Chess_rlt45.svg.png"));
    texturePiece.put(Piece.WHITE_QUEEN, new Texture("chess/Chess_qlt45.svg.png"));
    texturePiece.put(Piece.WHITE_KING, new Texture("chess/Chess_klt45.svg.png"));

    texturePiece.put(Piece.BLACK_PAWN, new Texture("chess/Chess_pdt45.svg.png"));
    texturePiece.put(Piece.BLACK_KNIGHT, new Texture("chess/Chess_ndt45.svg.png"));
    texturePiece.put(Piece.BLACK_BISHOP, new Texture("chess/Chess_bdt45.svg.png"));
    texturePiece.put(Piece.BLACK_ROOK, new Texture("chess/Chess_rdt45.svg.png"));
    texturePiece.put(Piece.BLACK_QUEEN, new Texture("chess/Chess_qdt45.svg.png"));
    texturePiece.put(Piece.BLACK_KING, new Texture("chess/Chess_kdt45.svg.png"));
  }

  @Override
  public void render(float delta) {
    board = game.getBoard();
    view.apply();
    // camera.update();

    Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    boardRender(camera);

    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    pieceRender();
    batch.end();

    handleInput();
  }

  @Override
  public void resize(int width, int height) {
    // If the window is minimized on a desktop (LWJGL3) platform, width and height
    // are 0, which causes problems.
    // In that case, we don't resize anything, and wait for the window to be a
    // normal size before updating.
    view.update(width, height, true);
    // Resize your screen here. The parameters represent the new window size.
  }

  @Override
  public void pause() {
    // Invoked when your application is paused.
  }

  @Override
  public void resume() {
    // Invoked when your application is resumed after pause.
  }

  @Override
  public void hide() {
    // This method is called when another screen replaces this one.
  }

  @Override
  public void dispose() {
    // Destroy screen's assets here.
    renderer.dispose();
    batch.dispose();
  }

  private void boardRender(OrthographicCamera camera) {
    renderer.setProjectionMatrix(camera.combined);

    renderer.begin(ShapeRenderer.ShapeType.Filled);

    for (int y = 0; y < 8; y++) {
      for (int x = 0; x < 8; x++) {
        if ((x + y) % 2 == 0) {
          renderer.setColor(Color.BROWN);
        } else {
          renderer.setColor(Color.LIGHT_GRAY);
        }
        renderer.rect(x * tileSize, y * tileSize, tileSize, tileSize);
      }
    }
    renderer.end();
  }

  private void pieceRender() {

    for (int rank = 0; rank < 8; rank++) {
      for (int file = 0; file < 8; file++) {
        // Square sq = Square.squareAt(rank * 8 + file);
        char fileChar = (char) ('a' + file);
        int rankNum = rank + 1;

        String squareStr = ("" + fileChar + rankNum).toUpperCase();
        Square sq = Square.fromValue(squareStr);

        Piece piece = board.getPiece(sq);
        if (piece == Piece.NONE)
          continue;

        drawPiece(piece, file, rank);
      }
    }
  }

  /**
   * file == colomn
   * rank == row
   * 
   * @param piece
   * @param file
   * @param rank
   */
  private void drawPiece(Piece piece, int file, int rank) {

    Texture tex = texturePiece.get(piece);

    batch.draw(tex, file * tileSize, rank * tileSize, tileSize, tileSize);

  }

  private void handleInput() {
    if (Gdx.input.justTouched()) {
      Vector3 touchPos = new Vector3();
      touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
      view.unproject(touchPos);
      moveCall(touchPos.x, touchPos.y);
    }
  }

  private void moveCall(float worldX, float worldY) {
    int file = (int) worldX / tileSize;
    int rank = (int) worldY / tileSize;

    char fileChar = (char) ('a' + file);
    int rankNum = rank + 1;
    String square = "" + fileChar + rankNum;

    if (selectedSquare == null) {
      selectedSquare = square;
      return;
    }

    String uci = selectedSquare + square;

    game.move(uci);
    selectedSquare = null;
  }
}