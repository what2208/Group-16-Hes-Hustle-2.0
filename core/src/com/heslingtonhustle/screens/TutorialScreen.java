package com.heslingtonhustle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.sound.SoundController;
import com.heslingtonhustle.sound.Sounds;

public class TutorialScreen implements Screen {
    private final HeslingtonHustleGame game;
    private final SoundController soundController;
    private final Skin skin;
    private final Stage tutStage;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Texture backgroundTexture;
    public static final String tutorialBackgroundAsset = "Graphics/UI/Backgrounds/menu_background.jpg";
    public static final String tutorialAsset1 = "Graphics/UI/Tutorial/tut2.jpg";
    public static final String tutorialAsset2 = "Graphics/UI/Tutorial/tut1.jpg";

    /**
     * A screen that shows a brief tutorial of how to play the game and
     * the setting to the player
     * @param game The main game object
     */
    public TutorialScreen(HeslingtonHustleGame game) {
        this.game = game;
        this.soundController = game.soundController;
        this.skin = game.skin;

        tutStage = new Stage(new FitViewport(game.width, game.height));
        Gdx.input.setInputProcessor(tutStage);

        // Configure orthographic camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(game.width, game.height, camera);
        camera.setToOrtho(false, game.width, game.height);

        // Background texture
        backgroundTexture = new Texture(tutorialBackgroundAsset);
        Image backgroundImage = new Image(backgroundTexture);
        tutStage.addActor(backgroundImage);

        createTutorial();

    }

    /**
     * Method to draw the options window and the volume sliders contained within.
     */
    private void createTutorial() {
        // Create the window
        Table mainTable = new Table();
        mainTable.setFillParent(true);
//        mainTable.setDebug(true);
        tutStage.addActor(mainTable);

        // Title
        Label title = new Label("How to Play", skin, "leaderboardscore");
        mainTable.add(title).top().padTop(30).colspan(3);
        mainTable.row();

        // First text
        Label info1 = new Label("Welcome to Heslington Hustle! In this game " +
                "you live out life as a student at the University of York in exam season." +
                " Except your first exam is in a week and you haven't studied at all!", skin, "minecraftia24px");
        info1.setAlignment(Align.center);
        info1.setWrap(true);
        mainTable.add(info1).padTop(20).prefWidth(900).colspan(3);
        mainTable.row();

        // First image
        Image img1 = new Image(new Texture(Gdx.files.internal(tutorialAsset1)));
        mainTable.add(img1).prefSize(938/2f, 423/2f).left();

        // Padding
        Label padding = new Label("P", skin, "minecraftia24px");
        padding.setVisible(false);
        mainTable.add(padding);

        // Second text
        Label info2 = new Label("Over the next 7 days you will need to make sure you study at least once" +
                " per day, complete enough recreational activities," +
                " eat 3 times per day and get enough sleep!", skin, "minecraftia24px");
        info2.setAlignment(Align.right);
        info2.setWrap(true);
        mainTable.add(info2).padTop(30).prefWidth(500);
        mainTable.row();

        // Third text
        Label info3 = new Label("You can walk around campus with the arrow or WASD keys." +
                " Press 'E' or the spacebar to interact with buildings to complete activities and advance" +
                " dialogue. Press 'M' to view the whole map. And make sure you sleep at the end of the" +
                " day to replenish your energy!", skin, "minecraftia24px");
        info3.setAlignment(Align.left);
        info3.setWrap(true);
        mainTable.add(info3).padTop(30).prefWidth(650).colspan(2);

        // Second image
        Image img2 = new Image(new Texture(Gdx.files.internal(tutorialAsset2)));
        mainTable.add(img2).prefSize(582/2f, 468/2f).right();
        mainTable.row();


        // Start game button
        TextButton playGameButton = new TextButton("Continue", skin);
        playGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                soundController.playSound(Sounds.CONFIRM);
                game.switchScreen(AvailableScreens.AvatarSelectScreen, false);
            }
        });

        mainTable.add(playGameButton).colspan(3).prefWidth(320);
    }


    /**
     * A method to render the options stage.
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tutStage.act(delta);
        tutStage.draw();

        camera.update();
    }

    @Override
    public void show() {

    }

    /**
     * Method to resize the stage if the viewport changes.
     * @param width The width of the game screen
     * @param height The height of the game screen
     */
    @Override
    public void resize(int width, int height){
        tutStage.getViewport().update(width, height);
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

    /**
     * Method which disposes anything no longer required when changing screens
     */
    @Override
    public void dispose(){
        tutStage.dispose();
        backgroundTexture.dispose();
    }

    /**
     * @return The viewport
     */
    public Viewport getViewport() { return viewport; }
}
