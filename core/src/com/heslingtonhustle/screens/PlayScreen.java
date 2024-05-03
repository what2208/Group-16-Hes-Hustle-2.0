package com.heslingtonhustle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.input.InputHandler;
import com.heslingtonhustle.input.KeyboardInputHandler;
import com.heslingtonhustle.map.MapManager;
import com.heslingtonhustle.renderer.Renderer;
import com.heslingtonhustle.state.Action;
import com.heslingtonhustle.state.Player;
import com.heslingtonhustle.state.State;

import java.util.HashSet;

public class PlayScreen implements Screen {
    private final HeslingtonHustleGame game;

    InputMultiplexer inputMultiplexer;
    private final InputHandler inputHandler;
    private final State gameState;
    private final Renderer renderer;
    private final MapManager mapManager;
    private final PauseMenu pauseMenu;
    private boolean isPaused;
    private float playerWidth, playerHeight;
    private Player player;

    /**
     * A screen to display the main game when the user is playing; importantly showing the map,
     * player sprite and UI.
     * @param game The main game object
     */
    public PlayScreen(HeslingtonHustleGame game) {
        this.game = game;

        isPaused = false;

        // The player size is in world units
        playerWidth = 0.6f;
        playerHeight = 0.9f;

        // The player
        player = new Player();

        // Configure the renderer
        mapManager = new MapManager();
        gameState = new State(mapManager, game.soundController, playerWidth, playerHeight);
        pauseMenu = new PauseMenu(this, game);
        renderer = new Renderer(gameState, mapManager, pauseMenu, game.skin, game.width, game.height);

        // Configure the input handler
        inputHandler = new KeyboardInputHandler();
        addInputHandlers();

        gameState.pushWelcomeDialogue();
        renderer.snapCamToPlayer();
    }

    /**
     * A method to render the game.
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {

//        // Get the actions just called in this frame and the actions currently held down
//        HashSet<Action> heldActions = inputHandler.getHeldActions();
//        HashSet<Action> pressedActions = inputHandler.getPressedActions();
//        // Game screen doesn't deal with any held actions
//        handleActions(pressedActions);

        // Structure
        // Get actions
        // Move player
        // Map returns list of objects player is inside, move player back
        // Map also finds if the player is near a trigger, and which is the nearest
        // If E pressed, pass the MapProperties to gamestate
        // Also pass to dialoguemanager

        // Draw everything
        // Check for gameover



        HashSet<Action> heldActions = inputHandler.getHeldActions();
        HashSet<Action> pressedActions = inputHandler.getPressedActions();

        player.move(heldActions);
        // The player reacts to any objects it is inside
        player.collide(mapManager.getObjectsInside(player.getHitbox()));

        MapProperties nearestTrigger = mapManager.getNearestTrigger(player.triggerHitbox());

        if (nearestTrigger != null) {
            if (dialogueManager.dialogue()) {
                dialogueManager.react(nearestTrigger);
            } else {
                gameState.react(nearestTrigger);
            }
        }

        mapManager.render();
        // Draw player
        hudRenderer.render();

        // Check for gameover
        if (gameState.isGameOver()) {
            {}
        }

//        if (!isPaused) {
//            gameState.update(heldActions, pressedActions, delta);
//        }
//        renderer.update(playerWidth, playerHeight);
//        inputHandler.resetPressedActions();
//
//        // Checks if the game has concluded
//        if (gameState.isGameOver()) {
//            game.gameOver(
//                    gameState.getActivities(),
//                    gameState.getPlayerStepAchievement());
//        }
    }

    /**
     * Calls events related to the actions called by the player on the current frame
     * @param pressedActions All actions related to key presses this frame
     */
    private void handleActions(HashSet<Action> pressedActions) {

        if (pressedActions.contains(Action.PAUSE)) {
            // If pause or unpause, ignore everything else
            handlePauseAction(Action.PAUSE);
            return;
        }

        // For all other actions
        for (Action action : pressedActions) {
            handleDebugAction(action);
        }

    }

    /**
     * Checks if a debug key has been pressed and performs the corresponding actions if one has.
     * @param action An action related to a key press.
     * @return Boolean
     */
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

    /**
     * Checks if a key press corresponds to a pause action. If so and the game is playing,
     * the game pauses. If so and the game is paused, the game resumes.
     * @param action An action related to a key press.
     * @return Boolean
     */
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

    /**
     * Method to implement multiplexers to ensure multiple inputs can be handled concurrently whilst the
     * game is paused.
     */
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

    /**
     * Method to resize the stage if the viewport changes.
     * @param width The width of the game screen.
     * @param height The height of the game screen.
     */
    @Override
    public void resize(int width, int height) {
        renderer.windowResized(width, height);
        renderer.snapCamToPlayer();
    }

    /**
     * Method to pause the game, start the input multiplexer and display the pause menu.
     */
    @Override
    public void pause() {
        isPaused = true;
        Gdx.input.setInputProcessor(inputMultiplexer);
        renderer.showPauseScreen();
    }

    /**
     * Method to resume the game, revert to the default input handler and hide the pause
     * menu.
     */
    @Override
    public void resume() {
        isPaused = false;
        Gdx.input.setInputProcessor(inputHandler);
        renderer.hidePauseScreen();
    }

    @Override
    public void hide() {
    }

    /**
     * Method which disposes anything no longer required when changing screens
     */
    @Override
    public void dispose() {
        mapManager.dispose();
        renderer.dispose();
        pauseMenu.dispose();
    }
}
