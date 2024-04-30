package com.heslingtonhustle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.sound.SoundController;
import com.heslingtonhustle.sound.Sounds;

public class OptionsScreen implements Screen {
    private final HeslingtonHustleGame game;
    private final SoundController soundController;
    private final Skin skin;

    private final Stage optionStage;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private Slider musicSlider;
    private Slider sfxSlider;
    private final Texture backgroundTexture;


    public OptionsScreen(HeslingtonHustleGame game) {
        this.game = game;
        this.soundController = game.soundController;
        this.skin = game.skin;

        // An option screen to let the player adjust the volume of music and sound effects
        optionStage = new Stage(new FitViewport(game.width, game.height));
        Gdx.input.setInputProcessor(optionStage);

        camera = new OrthographicCamera();
        viewport = new FitViewport(game.width, game.height, camera);
        camera.setToOrtho(false, game.width, game.height);

        // Background texture
        backgroundTexture = new Texture("Graphics/UI/Backgrounds/menu_background.jpg");
        Image backgroundImage = new Image(backgroundTexture);
        optionStage.addActor(backgroundImage);

        createOptionWindow();

    }


    private void createOptionWindow() {
        // Create the window
        Window optionMenu = new Window("", skin);
        optionStage.addActor(optionMenu);
        optionMenu.setModal(true);

        // Table for UI elements
        Table optionTable = new Table();
        optionMenu.add(optionTable).prefHeight(600);

        // Create all the UI elements
        // musicSlider and sfxSlider need to be accessible in render so they are already declared
        TextButton exitButton = new TextButton("Exit", skin);
        Label title = new Label("Settings", skin, "button");
        Label musicTitle = new Label("Music Volume", skin, "default");
        musicSlider = new Slider(0, 100, 1, false, skin, "default-horizontal");
        Label sfxTitle = new Label("SFX Volume", skin, "default");
        sfxSlider = new Slider(0, 100, 1, false, skin, "default-horizontal");
        Table sliderTable = new Table();
        // optionTable.setDebug(true);
        // sliderTable.setDebug(true);

        // Set to correct values
        musicSlider.setValue(soundController.getMusicVolume()*100);
        sfxSlider.setValue(soundController.getSfxVolume()*100);

        // Add to a smaller table to centre the labels and slider bars
        sliderTable.add(musicTitle).padRight(20);
        sliderTable.add(musicSlider).prefWidth(250);
        sliderTable.row().padTop(20);
        sliderTable.add(sfxTitle).padRight(20).right();
        sliderTable.add(sfxSlider).prefWidth(250);

        // Add all the UI elements to the table in the window
        optionTable.add(title).top().padTop(40).padBottom(50);
        optionTable.row();
        optionTable.add(sliderTable).fillX();
        optionTable.row();
        optionTable.add(exitButton).pad(40, 50, 60, 50).width(300).bottom().expandY();

        optionMenu.pack();

        optionMenu.setSize(600, 600);

        // Centre the window
        optionMenu.setX((viewport.getWorldWidth() / 2f) - (optionMenu.getWidth() / 2f));
        optionMenu.setY((viewport.getWorldHeight() / 2f) - (optionMenu.getHeight() / 2f));

        // Create exit button listener
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundController.playSound(Sounds.CONFIRM);
                game.switchToPreviousScreen(AvailableScreens.MenuScreen);
            }
        });
    }



    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        optionStage.act(delta);
        optionStage.draw();

        // Volumes should be between 0 and 1
        soundController.setMusicVolume(musicSlider.getValue() / 100);
        soundController.setSfxVolume(sfxSlider.getValue() / 100);

        camera.update();
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height){
        optionStage.getViewport().update(width, height);
        viewport.update(width, height);
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
    public void dispose(){
        optionStage.dispose();
        backgroundTexture.dispose();
    }
}
