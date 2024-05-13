package com.heslingtonhustle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.input.InputHandler;
import com.heslingtonhustle.input.KeyboardInputHandler;
import com.heslingtonhustle.sound.SoundController;
import com.heslingtonhustle.sound.Sounds;
import com.heslingtonhustle.state.Action;

import java.util.HashSet;

public class AvatarSelectScreen implements Screen {
    private final HeslingtonHustleGame game;
    private final SoundController soundController;
    private final Skin skin;
    private final Stage tutStage;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Texture backgroundTexture;
    private final InputHandler inputHandler;
    private ImageButton avatar0, avatar1;

    /**
     * A screen to allow the player to select their avatar
     * @param game The main game object
     */
    public AvatarSelectScreen(HeslingtonHustleGame game) {
        this.game = game;
        this.soundController = game.soundController;
        this.skin = game.skin;

        // An option screen to let the player adjust the volume of music and sound effects
        tutStage = new Stage(new FitViewport(game.width, game.height));
        Gdx.input.setInputProcessor(tutStage);

        // Configure orthographic camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(game.width, game.height, camera);
        camera.setToOrtho(false, game.width, game.height);

        // Background texture
        backgroundTexture = new Texture("Graphics/UI/Backgrounds/menu_background.jpg");
        Image backgroundImage = new Image(backgroundTexture);
        tutStage.addActor(backgroundImage);

        // Inputs
        inputHandler = new KeyboardInputHandler();
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(inputHandler);
        inputMultiplexer.addProcessor(tutStage);
        Gdx.input.setInputProcessor(inputMultiplexer);

        createAvatarSelect();

    }

    /**
     * Method to draw the options window and the volume sliders contained within.
     */
    private void createAvatarSelect() {
        // Create the window
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        tutStage.addActor(mainTable);

        // Title
        Label title = new Label("Choose Your Avatar", skin, "leaderboardscore");
        mainTable.add(title).top().padTop(30).colspan(3).padBottom(10);
        mainTable.row();

        // Left button
        avatar0 = new ImageButton(skin, "avatar0");
        mainTable.add(avatar0).left().padRight(60);
        avatar0.setChecked(true);

        // Right button
        avatar1 = new ImageButton(skin, "avatar1");
        mainTable.add(avatar1).right().padLeft(60);
        mainTable.row();

        // Button group
        ButtonGroup<Button> buttonGroup = new ButtonGroup<Button>(avatar0, avatar1);
        buttonGroup.setMaxCheckCount(1);

        ChangeListener press = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundController.playSound(Sounds.OPTIONSWITCH);
            }
        };

        avatar0.addListener(press);
        avatar1.addListener(press);



        // Start game button
        TextButton playGameButton = new TextButton("Start game", skin);
        playGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundController.playSound(Sounds.CONFIRM);
                if (avatar0.isChecked()) {
                    game.startGame("player-0");
                } else {
                    game.startGame("player-1");
                }
            }
        });

        mainTable.add(playGameButton).colspan(3).prefWidth(320).padTop(15);
    }


    /**
     * A method to render the options stage.
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleActions();
        inputHandler.resetPressedActions();

        tutStage.act(delta);
        tutStage.draw();

        camera.update();
    }


    private void handleActions() {
        // Get pressed actions
        HashSet<Action> actions = inputHandler.getPressedActions();

        if (actions.contains(Action.INTERACT)) {
            // Start
            soundController.playSound(Sounds.CONFIRM);
            if (avatar0.isChecked()) {
                game.startGame("player-0");
            } else {
                game.startGame("player-1");
            }
        } else if (actions.contains(Action.MOVE_LEFT)) {
            avatar0.setChecked(true);
            avatar1.setChecked(false);
            soundController.playSound(Sounds.OPTIONSWITCH);
        } else if (actions.contains(Action.MOVE_RIGHT)) {
            avatar0.setChecked(false);
            avatar1.setChecked(true);
            soundController.playSound(Sounds.OPTIONSWITCH);
        }
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
}
