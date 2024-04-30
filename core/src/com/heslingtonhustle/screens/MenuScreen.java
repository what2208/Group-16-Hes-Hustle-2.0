package com.heslingtonhustle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.sound.SoundController;
import com.heslingtonhustle.sound.Sounds;

public class MenuScreen implements Screen {
    private final HeslingtonHustleGame game;
    private final Skin skin;
    private final SoundController soundController;
    private final Stage stage;
    private Table optionsTable;
    private final Texture backgroundTexture;


    public MenuScreen(HeslingtonHustleGame game) {
        this.game = game;
        this.skin = game.skin;
        this.soundController = game.soundController;

        stage = new Stage(new FitViewport(game.width, game.height));
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture("Graphics/UI/Backgrounds/menu_background.jpg");
        Image backgroundImage = new Image(backgroundTexture);
        stage.addActor(backgroundImage);

        createTable();
        addOptions();
    }

    private void createTable() {
        optionsTable = new Table();
        optionsTable.setFillParent(true);
        stage.addActor(optionsTable);
    }

    private void addOptions() {
        // Title text
        Label titleText = new Label("Heslington Hustle", skin, "title");
        optionsTable.add(titleText).padBottom(30).padTop(30);
        optionsTable.row();

        // Start game button
        TextButton playGameButton = new TextButton("Start game", skin);
        playGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                soundController.playSound(Sounds.CONFIRM);
                game.switchScreen(AvailableScreens.PlayScreen, false);
            }
        });

        optionsTable.add(playGameButton).prefWidth(350);
        optionsTable.row().pad(10, 0, 5, 0);

        // Leaderboard button
        TextButton leaderboardButton = new TextButton("Leaderboard", skin);
        leaderboardButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                soundController.playSound(Sounds.CONFIRM);
                game.switchScreen(AvailableScreens.LeaderboardScreen, false);
            }
        });

        optionsTable.add(leaderboardButton).prefWidth(350);
        optionsTable.row().pad(5, 0, 5, 0);

        // Options button
        TextButton optionsButton = new TextButton("Settings", skin);
        optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                soundController.playSound(Sounds.CONFIRM);
                game.switchScreen(AvailableScreens.OptionsScreen, false);
            }
        });

        optionsTable.add(optionsButton).prefWidth(350);
        optionsTable.row().pad(5, 0, 5, 0);

        // Credits button
        TextButton creditsButton = new TextButton("Credits", skin);
        creditsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                soundController.playSound(Sounds.CONFIRM);
                // game.switchScreen(AvailableScreens.OptionsScreen, false);
            }
        });

        optionsTable.add(creditsButton).prefWidth(350);
        optionsTable.row().pad(5, 0, 5, 0);

        // Exit button
        TextButton exitButton = new TextButton("Exit", skin);
        optionsTable.add(exitButton).prefWidth(350);
        optionsTable.row().pad(5, 0, 5, 0);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        // Rotated version number text
        Group versionGroup = new Group();
        versionGroup.setPosition(1000, 650);
        stage.addActor(versionGroup);
        Label versionText = new Label("V2.0", skin, "version");
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
