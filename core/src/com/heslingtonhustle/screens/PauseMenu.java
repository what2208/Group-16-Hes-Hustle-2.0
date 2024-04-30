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
import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.sound.SoundController;
import com.heslingtonhustle.sound.Sounds;
import com.heslingtonhustle.state.State;

public class PauseMenu {
    private final Screen playScreen;
    private final HeslingtonHustleGame game;

    private final Skin skin;
    private final SoundController soundController;

    private final Stage stage;
    private Table optionsTable;
    private boolean isVisible;

    public PauseMenu(Screen parentClass, HeslingtonHustleGame game) {
        playScreen = parentClass;
        this.game = game;

        this.soundController = game.soundController;
        this.skin = game.skin;

        isVisible = false;
        stage = new Stage(new FitViewport(game.width, game.height));
        Gdx.input.setInputProcessor(stage);

        createTable();
        addOptions();
    }

    private void createTable() {
        optionsTable = new Table();
        optionsTable.setFillParent(true);
        stage.addActor(optionsTable);
    }

    private void addOptions() {
        // Resume button
        TextButton resumeButton = new TextButton("Resume", skin);
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                soundController.playSound(Sounds.CONFIRM);
                playScreen.resume();
            }
        });
        optionsTable.add(resumeButton).fillX().uniformX().prefWidth(350).padBottom(10);
        optionsTable.row();

        // Options button
        TextButton optionsButton = new TextButton("Settings", skin);
        optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                soundController.playSound(Sounds.CONFIRM);
                game.switchScreen(AvailableScreens.OptionsScreen, true);
            }
        });
        optionsTable.add(optionsButton).fillX().uniformX().prefWidth(350).padBottom(10);
        optionsTable.row();


        // Menu Button
        TextButton mainMenuButton = new TextButton("Menu", skin);
        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                soundController.playSound(Sounds.CONFIRM);
                game.switchScreen(AvailableScreens.MenuScreen, false);
            }
        });
        optionsTable.add(mainMenuButton).fillX().uniformX();
        optionsTable.row();
    }

    public void showPauseMenu() {
        isVisible = true;
        optionsTable.setVisible(true);
    }

    public void hidePauseMenu() {
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
