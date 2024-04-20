package com.heslingtonhustle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.input.InputHandler;
import com.heslingtonhustle.input.KeyboardInputHandler;
import com.heslingtonhustle.map.MapManager;
import com.heslingtonhustle.renderer.Renderer;
import com.heslingtonhustle.state.Action;
import com.heslingtonhustle.state.State;

import java.util.HashSet;

public class PlayScreen implements Screen {
    public HeslingtonHustleGame heslingtonHustleGame;

    InputMultiplexer inputMultiplexer;
    private final InputHandler inputHandler;
    private final State gameState;
    private final Renderer renderer;
    private final MapManager mapManager;
    private final PauseMenu pauseMenu;
    private boolean isPaused;

    public PlayScreen(HeslingtonHustleGame parentClass) {
        this.heslingtonHustleGame = parentClass;
        isPaused = false;

        // The player size is in world units
        float playerWidth = 0.6f;
        float playerHeight = 0.9f;

        mapManager = new MapManager();
        gameState = new State(mapManager, parentClass.soundController, playerWidth, playerHeight);
        pauseMenu = new PauseMenu(this, gameState, parentClass.soundController, parentClass.WIDTH, parentClass.HEIGHT);
        renderer = new Renderer(gameState, mapManager, pauseMenu, parentClass.skin, parentClass.WIDTH, parentClass.HEIGHT);

        inputHandler = new KeyboardInputHandler();
        addInputHandlers();

        gameState.pushWelcomeDialogue();
        renderer.snapCamToPlayer();
    }
    @Override
    public void render(float delta) {
        Action action = inputHandler.getAction();
        HashSet<Action> allActions = inputHandler.getAllActions();
        if (handleDebugAction(action)) {
            action = inputHandler.getAction();
        }
        if (handlePauseAction(action)) {
            action = inputHandler.getAction();
        }
        if (!isPaused) {
            gameState.update(action, allActions, delta);
        }
        renderer.update();

        if (gameState.isGameOver()) {
            heslingtonHustleGame.changeScreen(AvailableScreens.MenuScreen);
        }
    }

    private boolean handleDebugAction(Action action) {
        if (action == null) {
            return false;
        }
        // One of the debugging keys have been pressed. By default, these are ',' '.' '/' keys
        switch (action) {
            case DEBUGGING_ACTION1:
                if (gameState.noDialogueOnScreen()) {
                    gameState.printActivities();
                }
                return true;
            case DEBUGGING_ACTION2:
                Gdx.app.debug("DEBUG", "Time: "+gameState.getDebugTime());
                return true;
            case DEBUGGING_ACTION3:
                if (gameState.noDialogueOnScreen()) {
                    gameState.pushTestDialogue();
                }
                return true;
            default:
                return false;
        }
    }

    private boolean handlePauseAction(Action action) {
        if (action == Action.PAUSE && !isPaused) {
            pause();
            return true;
        } else if (action == Action.PAUSE) {
            resume();
            return true;
        }
        return false;
    }

    private void addInputHandlers() {
        // We use an input multiplexer so that we can handle multiple sources of inputs at once
        // Only used when the game is paused
        inputMultiplexer = new InputMultiplexer();

        inputMultiplexer.addProcessor(inputHandler);
        inputMultiplexer.addProcessor(pauseMenu.GetStage());

        Gdx.input.setInputProcessor(inputHandler);
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        renderer.windowResized(width, height);
        renderer.snapCamToPlayer();
    }

    @Override
    public void pause() {
        isPaused = true;
        Gdx.input.setInputProcessor(inputMultiplexer);
        renderer.ShowPauseScreen();
    }

    @Override
    public void resume() {
        isPaused = false;
        Gdx.input.setInputProcessor(inputHandler);
        renderer.HidePauseScreen();
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        mapManager.dispose();
        renderer.dispose();
        pauseMenu.dispose();
    }
}
