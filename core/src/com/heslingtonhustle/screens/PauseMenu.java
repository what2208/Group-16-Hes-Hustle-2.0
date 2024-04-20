package com.heslingtonhustle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.heslingtonhustle.sound.SoundController;
import com.heslingtonhustle.sound.Sounds;
import com.heslingtonhustle.state.State;

public class PauseMenu {
    private final boolean DEBUG = false;
    private final Screen playScreen;
    private final Stage stage;
    private State gameState;
    private Table optionsTable;
    private final Skin skin;
    private boolean isVisible;
    private final SoundController soundController;

    public PauseMenu(Screen parentClass, Skin skin, State gameState, SoundController soundController, int width, int height) {
        playScreen = parentClass;
        isVisible = false;
        stage = new Stage(new FitViewport(width, height));
        Gdx.input.setInputProcessor(stage);
        this.gameState = gameState;
        this.soundController = soundController;
        this.skin = skin;

        createTable();
        addOptions();
    }

    private void createTable() {
        optionsTable = new Table();
        optionsTable.setFillParent(true);
        optionsTable.setDebug(DEBUG);
        stage.addActor(optionsTable);
    }

    private void addOptions() {
        TextButton resumeButton = new TextButton("Resume", skin
        );
        optionsTable.add(resumeButton).fillX().uniformX().prefWidth(350);
        optionsTable.row().pad(10, 0, 10, 0);
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                soundController.playSound(Sounds.CONFIRM);
                playScreen.resume();
            }
        });

        optionsTable.row();
        TextButton mainMenuButton = new TextButton("Menu", skin);
        optionsTable.add(mainMenuButton).fillX().uniformX();
        optionsTable.row().pad(10, 0, 10, 0);
        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                soundController.playSound(Sounds.CONFIRM);
                gameState.setGameOver();
            }
        });
    }

    public void ShowPauseMenu() {
        isVisible = true;
        optionsTable.setVisible(true);
    }

    public void HidePauseMenu() {
        isVisible = false;
        optionsTable.setVisible(false);
    }

    public void render() {
        if (isVisible) {
            stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
            stage.draw();
        }
    }

    public Stage GetStage() {
        return stage;
    }

    public void resize(int width, int height) {
        stage.getCamera().viewportWidth = Gdx.graphics.getWidth();
        stage.getCamera().viewportHeight = Gdx.graphics.getHeight();
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
    }
}
