package com.habil.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.habil.game.Main;

public class MenuScreen implements Screen {

  Stage stage;
  Skin skin;
  Main main;
  Table rootTable;

  private boolean iswhite = true;
  private int stockfishDepth = 10;

  public MenuScreen(Main main) {
    this.main = main;
    stage = new Stage(new ScreenViewport());
    skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
    rootTable = new Table();
    rootTable.setFillParent(true);
    stage.addActor(rootTable);
  }

  @Override
  public void show() {
    Gdx.input.setInputProcessor(stage);
    Table table = new Table();
    table.setFillParent(true);
    stage.addActor(table);

    showMainMenu();
  }

  private void showMainMenu() {
    rootTable.clear();

    TextButton playButton = new TextButton("Start", skin);
    TextButton exitButton = new TextButton("Exit", skin);

    playButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        // main.setScreen(new ChessScreen());
        showMatchMaking();
      }
    });

    exitButton.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        Gdx.app.exit();
      }
    });

    rootTable.add(playButton).fillX().uniformX().pad(10);
    rootTable.row();
    rootTable.add(exitButton).fillX().uniformX().pad(10);
  }

  private void showMatchMaking() {
    rootTable.clear();

    Label colorLabel = new Label("Choose your side", skin);
    final TextButton whiteBtn = new TextButton("White", skin, "toggle");
    final TextButton blackBtn = new TextButton("Black", skin, "toggle");

    ButtonGroup<TextButton> colorGroup = new ButtonGroup<>();
    colorGroup.setMaxCheckCount(1);
    colorGroup.setMinCheckCount(1);
    whiteBtn.setChecked(true);

    final Label depthLabel = new Label(("Stockfish depth : " + stockfishDepth), skin);
    final Label depthValLabel = new Label(String.valueOf(stockfishDepth), skin);

    TextButton btnMinus = new TextButton("-",skin);
    TextButton btnPlus = new TextButton("+",skin);
  
    btnMinus.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent envent, float x, float y){
        if(stockfishDepth > 1){
          stockfishDepth--;
          depthValLabel.setText(String.valueOf(stockfishDepth));
        }
      }
    });

    btnPlus.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y){
        if(stockfishDepth < 20){
          stockfishDepth++;
          depthValLabel.setText(String.valueOf(stockfishDepth));
        }
      }
    });
    // final Slider depthSlider = new Slider(1, 20, 1, false, skin);
    // depthSlider.setValue(stockfishDepth);
    // depthSlider.addListener(new ChangeListener() {
    //   @Override
    //   public void changed(ChangeEvent event, Actor actor){
    //     depthLabel.setText("Stockfish Depth: " + (int)depthSlider.getValue());
    //     stockfishDepth = (int)depthSlider.getValue();
    //   }
    // });


    TextButton playButton = new TextButton("Play now",skin);
    playButton.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event,float x, float y){
        iswhite  = whiteBtn.isChecked();
        main.setScreen(new ChessScreen(iswhite, stockfishDepth));
      }
    });

    rootTable.add(colorLabel).colspan(2).padBottom(10).row();
    rootTable.add(whiteBtn).width(100).pad(5);
    rootTable.add(blackBtn).width(100).pad(5);

    rootTable.add(depthLabel).colspan(2).padTop(30).row();

    Table stepperTable = new Table();
    stepperTable.add(btnMinus).width(50).height(50);
    stepperTable.add(depthValLabel).width(60).center();
    stepperTable.add(btnPlus).width(50).height(50);

    rootTable.add(stepperTable).colspan(2).pad(30).row();
    // rootTable.add(depthSlider).colspan(2).width(200).row();

    rootTable.add(playButton).colspan(2).width(210).height(50);

  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void hide() {
  }

  @Override
  public void dispose() {
    stage.dispose();
    skin.dispose();
  }

}
