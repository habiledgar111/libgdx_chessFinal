package com.habil.game;

import com.badlogic.gdx.Game;
import com.habil.game.screen.MenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {
        setScreen(new MenuScreen(this));
    }
}