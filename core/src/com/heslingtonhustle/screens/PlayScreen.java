package com.heslingtonhustle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.input.InputHandler;
import com.heslingtonhustle.input.KeyboardInputHandler;
import com.heslingtonhustle.map.MapManager;
import com.heslingtonhustle.renderer.CharacterRenderer;
import com.heslingtonhustle.renderer.HudRenderer;
import com.heslingtonhustle.state.Action;
import com.heslingtonhustle.state.DialogueManager;
import com.heslingtonhustle.state.Player;
import com.heslingtonhustle.state.State;
import com.badlogic.gdx.math.Interpolation;

import java.util.HashSet;


/**
 * The main screen controlling most of the game logic and loop
 */
public class PlayScreen implements Screen {
    private final HeslingtonHustleGame game;
    InputMultiplexer inputMultiplexer;
    private final InputHandler inputHandler;
    private final State gameState;
    private final MapManager mapManager;
    private final PauseMenu pauseMenu;
    private boolean isPaused;
    private final Player player;
    private final DialogueManager dialogueManager;
    private final FitViewport viewport;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final HudRenderer hudRenderer;
    private final CharacterRenderer playerRenderer;
    private float zoomLevel = 7f;
    private float zoomTarget = 1f;
    private float zoomProgress = 1f;
    private Vector2 zoomCoordinates = null;
    MapProperties changeMapTrigger = null;
    public static final String campusEastMapPath = "Maps/campusEast.tmx";

    /**
     * A screen to display the main game when the user is playing; importantly rendering the map,
     * player sprite and UI.
     * Also calls the actions necessary for the game logic to complete
     * @param game The main game object
     */
    public PlayScreen(HeslingtonHustleGame game, String playerString) {
        this.game = game;
        float zoom = 4.5f;

        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        viewport = new FitViewport(game.width/zoom, game.height/zoom, camera);
        camera.setToOrtho(false, game.width, game.height);

        isPaused = false;

        // The player size is in world units
        float playerWidth = 0.9f;
        float playerHeight = 0.9f;

        // Classes needed for the game
        player = new Player(playerWidth, playerHeight);

        dialogueManager = new DialogueManager(game.soundController);

        mapManager = new MapManager();
        mapManager.loadMap(campusEastMapPath);

        gameState = new State(dialogueManager);
        hudRenderer = new HudRenderer(game.skin, dialogueManager, game.width, game.height);
        pauseMenu = new PauseMenu(this, game);


        // Configure the input handler
        inputHandler = new KeyboardInputHandler();
        addInputHandlers();

        gameState.pushStartDayDialogue();
        camera.position.set(mapManager.worldToPixelCoords(player.getPosition()), 0);

        float playerWidthInPixels = mapManager.worldToPixelValue(player.getPlayerWidth());
        float playerHeightInPixels = mapManager.worldToPixelValue(player.getPlayerHeight());

        TextureAtlas textureAtlas = new TextureAtlas("Players/players.atlas");
        playerRenderer = new CharacterRenderer(playerWidthInPixels, playerHeightInPixels, textureAtlas, playerString, false);

        player.setPosition(mapManager.getSpawnPoint());
        Vector2 playerPixelPosition = mapManager.worldToPixelCoords(player.getPosition());
        camera.position.set(new Vector3(
                playerPixelPosition.x + (mapManager.worldToPixelValue(player.getPlayerWidth())/2),
                playerPixelPosition.y + (mapManager.worldToPixelValue(player.getPlayerHeight())/2),
                0
        ));
    }

    /**
     * A method to render the game.
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();

        delta = 0.01667f;

        // <--- LOGIC ---> //

        // Get inputs
        HashSet<Action> heldActions = inputHandler.getHeldActions();
        HashSet<Action> pressedActions = inputHandler.getPressedActions();

        // Check if the player has paused the game
        handleActions(pressedActions);

        // Check for pause
        if (!isPaused) {
            // Let the player move if there is no dialogue on screen
            if (dialogueManager.isEmpty() && camera.zoom == 1f && hudRenderer.screenClear()) {
                player.move(heldActions, delta);
            } else {
                player.dontMove();
            }

            // Player's can either interact with a trigger or with on-screen dialogue
            // but no both at once
            if (!dialogueManager.isEmpty()) {
                dialogueManager.handleAction(pressedActions);
            } else if (camera.zoom == 1f) {
                // Only interact if button pressed and screen clear
                if (pressedActions.contains(Action.INTERACT) && hudRenderer.screenClear()) {
                    gameState.handleInteraction();

                    MapProperties currentTrigger = gameState.getNearestTrigger();

                    // Check for NPC to rotate and sleep fade ins
                    if (currentTrigger != null) {
                        if (currentTrigger.containsKey("dialogue")) {
                            mapManager.rotateNPC(currentTrigger, mapManager.worldToPixelCoords(player.getCentre()));
                        }
                    }
                }
            }

            // Check for a zoom in
            if (dialogueManager.isEmpty()) {
                // Allow a zoom
                if (pressedActions.contains(Action.MAP) && zoomProgress == 1) {
                    zoom();
                }
            }
            gameState.passTime(delta);

        } else {
            player.dontMove();
        }

        // Start fading the screen in to black if needed
        if (gameState.getFading() && hudRenderer.screenClear()) {
            hudRenderer.fadeIn(2f);
            gameState.setFading(false);
        }

        // Zoom camera
        if (zoomProgress < 0.98) {
            camera.zoom = Interpolation.exp10Out.apply(zoomLevel, zoomTarget, zoomProgress);
            zoomProgress += delta;

        } else {
            zoomProgress = 1;
            camera.zoom = zoomTarget;
        }

        // The player needs to move out of any objects it is inside
        player.collide(mapManager.getOverlappingRectangles(player.getCollisionBox()));
        // Also stay inside map
        player.setInBounds(mapManager.getCurrentMapWorldDimensions());

        // Find the nearest interactable object
        MapProperties nearestTrigger = mapManager.getNearestTrigger(player.getTriggerBox());
        gameState.setNearestTrigger(nearestTrigger);

        hudRenderer.updateValues(gameState.getTime(), gameState.getDay(), gameState.getEnergy());

        // <--- RENDERING ---> //
        MapRenderer mapRenderer = mapManager.getCurrentMapRenderer(batch); // Maybe change how this works
        // Set this before slerping the camera to remove odd black bar errors
        if (!(zoomTarget == 1 && zoomProgress < 0.6)) mapRenderer.setView(camera);

        // Centre and zoom camera
        Vector2 playerPixelPosition = mapManager.worldToPixelCoords(player.getPosition());
        // Centre camera on the player
        if (zoomCoordinates == null) {
            camera.position.slerp(
                    new Vector3(
                            playerPixelPosition.x + (mapManager.worldToPixelValue(player.getPlayerWidth())/2),
                            playerPixelPosition.y + (mapManager.worldToPixelValue(player.getPlayerHeight())/2),
                            0
                    ),
                    delta*5);
        } else {
            camera.position.slerp(
                new Vector3(
                        zoomCoordinates.x,
                        zoomCoordinates.y,
                        0
                ),
                delta*5);
        }


        // Draw map
        mapRenderer.render(mapManager.getBackgroundLayers());

        // Draw player and NPCs
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        mapManager.renderNPCs(batch);
        // Add 1 to stop player's feet clipping into things
        playerRenderer.render(batch, playerPixelPosition.x, playerPixelPosition.y+1, player.getFacing(), player.getMoving());
        batch.end();

        // Render layers in front of player
        mapRenderer.render(mapManager.getForegroundLayers());

        // Render map labels if zoomed out or zooming in
        if (zoomTarget != 1f || zoomProgress < 0.2f) {
            batch.begin();
            mapManager.renderLabels(batch);
            batch.end();
        }

        // Draw HUD if not zoomed out
        if (!(zoomTarget != 1f || zoomProgress < 0.2f)) {
            if (isPaused) {
                hudRenderer.render(null);
            } else {
                // Pass the nearest trigger for the interaction label
                hudRenderer.render(nearestTrigger);
            }
        }
        pauseMenu.render();

        // <--- FINAL CHECKS AND RESETS ---> //
        doBlackScreenActions();

        inputHandler.resetPressedActions();
        camera.update();

        if (gameState.isGameOver()) {
            game.gameOver(
                    gameState.getActivities(),
                    player.getStepAchievement());

        }
    }

    /**
     * DEBUG
     * Draws the player's two hit boxes for debugging purposes
     */
    private void drawPlayerDebug() {
        ShapeRenderer collisionRenderer = mapManager.getCollisionRenderer();
        collisionRenderer.setProjectionMatrix(camera.combined);

        collisionRenderer.begin(ShapeRenderer.ShapeType.Line);
        // Collision hitbox
        collisionRenderer.setColor(0, 0, 1, 1);
        Rectangle collision = mapManager.worldRectangleToPixelRectangle(player.getCollisionBox());
        collisionRenderer.rect(collision.x, collision.y, collision.width, collision.height);
        // Trigger hitbox
        collisionRenderer.setColor(0, 1, 0, 1);
        Rectangle trigger = mapManager.worldRectangleToPixelRectangle(player.getTriggerBox());
        collisionRenderer.rect(trigger.x, trigger.y, trigger.width, trigger.height);
        collisionRenderer.end();
    }

    /**
     * Calls events related to the actions called by the player on the current frame
     * @param pressedActions All actions related to key presses this frame
     */
    private void handleActions(HashSet<Action> pressedActions) {
        // For each action...
        for (Action action : pressedActions) {
            // Check whether anything needs to be done
            handlePauseAction(action);
            handleDebugAction(action);
        }

    }

    /**
     * Actions that need to be performed when the game screen
     * is fully black, includes sleeping and changing maps
     */
    private void doBlackScreenActions() {
        if (hudRenderer.screenIsBlack()) {
            // Always fade out if the screen is fully black
            hudRenderer.fadeOut(3f);
            gameState.setFading(false);

            // Change map
            if (gameState.getNewMapTrigger() != null) {
                changeMap(gameState.getNewMapTrigger());
                gameState.resetNewMapTrigger();
            }

            // Sleep
            if (gameState.getSleeping()) {
                gameState.sleep();
                gameState.setSleeping(false);
            }

        }
    }

    /**
     * Changes the map to the map defined by the current Trigger
     * Also sets the player to the new position defined in the trigger
     * and moves the camera
     * @param currentTrigger The trigger containing information about
     *                       the new map to switch to
     */
    public void changeMap(MapProperties currentTrigger) {
        mapManager.loadMap("Maps/" + currentTrigger.get("new_map"));
        player.setPosition(new Vector2(
                (float) currentTrigger.get("new_map_x"),
                (float) currentTrigger.get("new_map_y")));
        Vector2 playerPixelPosition = mapManager.worldToPixelCoords(player.getPosition());
        camera.position.set(
            camera.position.set(
                new Vector3(
                        playerPixelPosition.x + (mapManager.worldToPixelValue(player.getPlayerWidth())/2),
                        playerPixelPosition.y + (mapManager.worldToPixelValue(player.getPlayerHeight())/2),
                        0
                )
            )
        );
    }

    /**
     * Zooms the camera in/out
     */
    private void zoom() {
        float temp = zoomLevel;
        zoomLevel = zoomTarget;
        zoomTarget = temp;
        zoomProgress = 0f;
        if (zoomCoordinates == null) {
            zoomCoordinates = mapManager.getCentre();
        } else {
            zoomCoordinates = null;
        }
    }

    /**
     * Checks if a debug key has been pressed and performs the corresponding actions if one has.
     * @param action An action related to a key press.
     */
    private void handleDebugAction(Action action) {
        if (action == null) {
            return;
        }
        // One of the debugging keys have been pressed. By default, these are ',' '.' '/' keys
        switch (action) {
            case DEBUGGING_ACTION1:
                if (gameState.noDialogueOnScreen()) {
                    gameState.printActivities();
                }
                return;
            case DEBUGGING_ACTION2:
                Gdx.app.debug("DEBUG", "Time: "+gameState.getDebugTime());
                return;
            case DEBUGGING_ACTION3:
                if (gameState.noDialogueOnScreen()) {
                    gameState.pushTestDialogue();
                }
        }
    }

    /**
     * Checks if a key press corresponds to a pause action. If so and the game is playing,
     * the game pauses. If so and the game is paused, the game resumes.
     * @param action An action related to a key press.
     */
    private void handlePauseAction(Action action) {
        if (action == Action.PAUSE && !isPaused) {
            pause();
        } else if (action == Action.PAUSE) {
            unPause();
        }
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
        viewport.update(width, height);
        hudRenderer.resize(width, height);
    }

    /**
     * Method to pause the game, start the input multiplexer and display the pause menu.
     */
    @Override
    public void pause() {
        isPaused = true;
        Gdx.input.setInputProcessor(inputMultiplexer);
        pauseMenu.showPauseMenu();
    }

    /**
     * Specifically unpauses the game
     */
    public void unPause() {
        isPaused = false;
        Gdx.input.setInputProcessor(inputHandler);
        pauseMenu.hidePauseMenu();
    }

    /**
     * Method to resume the game, revert to the default input handler and hide the pause
     * menu.
     */
    @Override
    public void resume() {
        // Ensures all variables are set correctly
        if (pauseMenu.isVisible()) {
            pause();
        } else {
            unPause();
        }
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
//        renderer.dispose();
        pauseMenu.dispose();
    }

    /**
     * @return whether the screen is paused or not
     */
    public boolean isPaused() { return isPaused; }

    public PauseMenu getPauseMenu() { return pauseMenu; }

    public HudRenderer getHudRenderer() { return hudRenderer; }

    public MapManager getMapManager() { return mapManager; }

    public State getState() { return gameState; }
}
