package com.heslingtonhustle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.sound.Sounds;

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

        backgroundTexture = new Texture("Graphics/UI/Backgrounds/menu_background.jpg");
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
        Label titleText = new Label("Heslington Hustle", parentClass.skin, "title");
        TextButton playGameButton = new TextButton("Start game", parentClass.skin);
        optionsTable.add(titleText);
        optionsTable.row();
        optionsTable.add(playGameButton).prefWidth(350).padTop(100);
        optionsTable.row().pad(15, 0, 15, 0);
        playGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                parentClass.soundController.playSound(Sounds.CONFIRM);
                heslingtonHustleGame.changeScreen(AvailableScreens.PlayScreen);
            }
        });

        optionsTable.row();

        TextButton exitButton = new TextButton("Exit", parentClass.skin);
        optionsTable.add(exitButton).prefWidth(350);
        optionsTable.row().pad(10, 0, 10, 0);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        // Rotated text
        Group versionGroup = new Group();
        versionGroup.setPosition(1000, 575);
        stage.addActor(versionGroup);
        Label versionText = new Label("V2.0", parentClass.skin, "version");
        versionGroup.addActor(versionText);
        versionGroup.addAction(Actions.rotateBy(-25));

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
