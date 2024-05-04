package com.heslingtonhustle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

public class PauseMenu {
    private final Screen playScreen;
    private final HeslingtonHustleGame game;

    private final Skin skin;
    private final SoundController soundController;

    private final Stage stage;
    private Table optionsTable;
    private boolean isVisible;

    /**
     * A class to display the pause menu on screen when it is called from PlayScreen.
     * @param parentClass The parent screen.
     * @param game The main game object.
     */
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

    /**
     * A method to create a table for the pause menu options to be displayed in.
     */
    private void createTable() {
        optionsTable = new Table();
        optionsTable.setFillParent(true);
        stage.addActor(optionsTable);
    }

    /**
     * A method to add the options into the pause menu's table and perform the corresponding actions if
     * they're clicked.
     */
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

    /**
     * A method which makes the pause menu visible when needed.
     */
    public void showPauseMenu() {
        isVisible = true;
        optionsTable.setVisible(true);
    }

    /**
     * A method which hides the pause menu when no longer needed.
     */
    public void hidePauseMenu() {
        isVisible = false;
        optionsTable.setVisible(false);
    }

    /**
     * A method to render the pause stage, acting on delta.
     */
    public void render() {
        if (isVisible) {
            stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
            stage.draw();
        }
    }

    /**
     * @return The current stage
     */
    public Stage GetStage() {
        return stage;
    }

    /**
     * Correctly resizes the onscreen elements when the window is resized
     * @param width The width of the game screen.
     * @param height The height of the game screen.
     */
    public void resize(int width, int height) {
        stage.getCamera().viewportWidth = Gdx.graphics.getWidth();
        stage.getCamera().viewportHeight = Gdx.graphics.getHeight();
        stage.getViewport().update(width, height, true);
    }

    /**
     * Method which disposes anything no longer required when changing screens
     */
    public void dispose() {
        stage.dispose();
    }
}
