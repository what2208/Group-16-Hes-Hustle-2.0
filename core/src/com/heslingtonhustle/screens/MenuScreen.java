package com.heslingtonhustle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.heslingtonhustle.HeslingtonHustleGame;

public class MenuScreen implements Screen {
    private final boolean DEBUG = false;
    private final HeslingtonHustleGame heslingtonHustleGame;
    private final Stage stage;
    private Table optionsTable;
    private final Texture backgroundTexture;
    private Image backgroundImage;


    public MenuScreen(HeslingtonHustleGame parentClass) {
        heslingtonHustleGame = parentClass;
        stage = new Stage(new FitViewport(parentClass.WIDTH, parentClass.HEIGHT));
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture("Graphics/UI/menu-background.jpg");
        Image backgroundImage = new Image(backgroundTexture);
        stage.addActor(backgroundImage);

        createTable();
        addOptions(parentClass);
    }

    private void createTable() {
        optionsTable = new Table();
        optionsTable.setFillParent(true);
        optionsTable.setDebug(DEBUG);
        stage.addActor(optionsTable);
    }

    private void addOptions(HeslingtonHustleGame parentClass) {
        TextButton playGameButton = new TextButton("Play game", parentClass.skin, "special");
        optionsTable.add(playGameButton).fillX().uniformX();
        optionsTable.row().pad(10, 0, 10, 0);
        playGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                heslingtonHustleGame.changeScreen(AvailableScreens.PlayScreen);
            }
        });

        optionsTable.row();

        TextButton exitButton = new TextButton("Exit", parentClass.skin, "special");
        optionsTable.add(exitButton).fillX().uniformX();
        optionsTable.row().pad(10, 0, 10, 0);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
    }
}
