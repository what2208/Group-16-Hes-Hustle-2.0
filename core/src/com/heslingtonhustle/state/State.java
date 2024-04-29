package com.heslingtonhustle.state;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.heslingtonhustle.map.MapManager;
import com.heslingtonhustle.sound.SoundController;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


/**
 * Contains all data related to the logical state of the game.
 * Acts as coordinator for the whole game, mutating the state given a player's Action.
 * This class should be queried in order to perform tasks like rendering.
 */
public class State {
    private static final int MAX_DAYS = 7;
    private boolean gameOver;
    private final Player player;
    private final Clock clock;
    private final MapManager mapManager;
    private final DialogueManager dialogueManager;
    private final SoundController soundController;
    private final HashMap<String, Activity> activities = new HashMap<>();
    private Trigger currentTrigger;
    private int score;
    private int energy;
    private int hoursSlept;


    public State(MapManager mapManager, SoundController soundController, float playerWidth, float playerHeight) {
        gameOver = false;

        player = new Player(38.25f, 57.25f, playerWidth, playerHeight);
        clock = new Clock();
        this.mapManager = mapManager;
        dialogueManager = new DialogueManager(soundController);
        this.soundController = soundController;

        score = 0;
        replenishEnergy();
        currentTrigger = null; // This stores the Tiled trigger box that we are currently stood inside of
    }


    /**
     * Updates the game's state based on the pressed and held actions
     * Handles player movement and collision, time as well as interactions
     *
     * @param heldActions A set of all held down actions
     * @param pressedActions A set of all actions called only on this frame
     * @param timeDelta Time passed since the last update
     */
    public void update(HashSet<Action> heldActions, HashSet<Action> pressedActions, float timeDelta) {
        currentTrigger = mapManager.getTrigger(player.getCollisionBox());

        // Checks for an interaction or dialogue skip
        for (Action action : pressedActions) {
            handleAction(action);
        }

        // The player only deals with actions that are held down, for its movement
        if (dialogueManager.isEmpty()) {
            player.move(heldActions);
        } else {
            // Specifically tell the player not to move anywhere this frame
            player.freeze();
        }

        // Store old pos in case player is colliding with something
        Vector2 previousPlayerPos = player.getPosition();
        // Move player
        player.update();

        // If collisions with anything, revert back to old position
        handlePlayerCollisions(previousPlayerPos, player.getCollisionBox());

        player.setInBounds(mapManager.getCurrentMapWorldDimensions());
        clock.increaseTime(timeDelta);
    }

    /**
     * Checks if the player is colliding with any objects, and restores them to their original position if they are
     * Note: Assumes the player was not colliding with anything in the position oldPos
     * @param oldPos The position of the player on the previous frame
     */
    public void handlePlayerCollisions(Vector2 oldPos, Rectangle playerBox) {
        // Get an array of objects the player is colliding with
        Array<Rectangle> colliders = mapManager.getCollisionRectangles(player.getCollisionBox());
        // Translate player's coordinates to world coordinates
        playerBox = mapManager.worldRectangleToPixelRectangle(playerBox);
        // We need a copy of the player's location not in world coordinates
        Vector2 oldPosWorld = mapManager.worldToPixelCoords(oldPos);


        // For each object
        for (Rectangle collider : colliders) {
            // If null then the player is not colliding with anything, so do nothing
            if (collider != null) {
                // Find which previous dimension was not overlapping, and then only reset that one to allow
                // sliding to happen

                // If previously not overlapping in x direction, revert them back
                if (!(oldPosWorld.x < collider.x + collider.width &&
                        oldPosWorld.x + playerBox.width > collider.x)) {
                    player.setX(oldPos.x);
                }
                // Same with y dimension
                if (!(oldPosWorld.y < collider.y + collider.height &&
                        oldPosWorld.y + playerBox.height > collider.y)) {
                    player.setY(oldPos.y);
                }
            }
        }

    }

    /**
     * Passes the given action to dialogue box or interacting with a building
     * @param action The action to pass
     */
    private void handleAction(Action action) {
        if (!dialogueManager.isEmpty()) {
            // A dialogue box is currently being displayed
            handleDialogueAction(action);
        } else if (action == Action.INTERACT) {
            handleInteraction();
        }
    }

    private void handleDialogueAction(Action action) {
        switch (action) {
            case MOVE_UP:
                dialogueManager.decreaseSelection();
                break;
            case MOVE_DOWN:
                dialogueManager.increaseSelection();
                break;
            case INTERACT:
                dialogueManager.submit();
        }
    }

    private void handleInteraction() {
        if (currentTrigger == null) {
            return;
        }

        if (currentTrigger.getNewMap() != null) {
            mapManager.loadMap("Maps/" + currentTrigger.getNewMap());
            player.setPosition(currentTrigger.getNewMapCoords());
            return;
        }

        if (currentTrigger.canSleep()) {
            // If player has not slept yet
            if (!activities.containsKey("sleep")) {
                activities.put("sleep", new Activity(
                        "sleep", "sleep",
                        5, 0, 8, -1)
                );
            }

            Activity activity  = activities.get("sleep");
            List<String> options = new ArrayList<>(Arrays.asList("Yes", "No"));
            dialogueManager.addDialogue("Do you want to sleep?", options, selectedOption -> {
                    if (selectedOption == 0) {
                        addHoursSlept();
                        advanceDay();
                        activity.completeActivity();
                        dialogueManager.addDialogue("You have just slept!");
                    }
                }
            );
            return;
        }

        // Call an activity that is not sleeping
        String activityName = currentTrigger.getActivityName();
        if (activityName != null) {
            // On the first encounter with an activity, create a new Activity class
            // to store how many times this activity has been completed
            if (!activities.containsKey(activityName)) {
                activities.put(activityName, currentTrigger.toActivity());
            }

            Activity activity = activities.get(activityName);

            // Call a dialogue box to prompt the player if they want to do
            // the listed activity
            List<String> options = new ArrayList<>(Arrays.asList("Yes", "No"));
            dialogueManager.addDialogue(currentTrigger.getPromptMessage(), options, selectedOption -> {
                    if (selectedOption == 0) {
                        // If 'yes', do activity
                        doActivity(activity);
                    }
                }
            );
        }
    }


    /**
     * Checks whether a player can do an activity, and if so updates
     * appropriate values
     * Also shows a success/fail dialogue box
     * @param activity The activity to complete
     */
    private void doActivity(Activity activity) {

        // Various checks for if the player can do the activity
        if (!activity.canDoActivity()) {
            dialogueManager.addDialogue("You've done this too much today!\nGo do something else!");
        } else if (energy < activity.getEnergyUse()) {
            dialogueManager.addDialogue("You don't have enough energy to do this right now!");
        } else if (!(clock.getRawTime() > 480)) {
            dialogueManager.addDialogue("This building opens at 8am.");
        } else {
            // They can do it
            activity.completeActivity();
            exertEnergy(activity.getEnergyUse());
            clock.passHours(activity.getHours());
            dialogueManager.addDialogue(currentTrigger.getSuccessMessage());
        }
    }

    private void advanceDay() {
        if (clock.getDay() >= MAX_DAYS) {
            dialogueManager.addDialogue("Game Over. Your score was: "
                    + score, selectedOption -> gameOver = true);
            return;
        }

        // The player is only allowed to do the same activity twice once
        // in a day
        if (activities.containsKey("study")) {
            Activity study = activities.get("study");
            if (study.getTimesCompletedToday() == 2) {
                study.setMaxPerDay(1);
            }
        }

        for (Activity activity : activities.values()) {
            activity.dayAdvanced();
        }

        nextDay();
    }

    //Debug function
    public void printActivities() {
        StringBuilder builder = new StringBuilder();

        for (String s : activities.keySet()) {
            builder.append(s).append(" count: ").append(activities.get(s).getTimesCompleted()).append("\n");
            //builder.append(s).append(" value: ").append(activities.get(s).getValue()).append("\n");
        }

        String result = builder.toString();
        dialogueManager.addDialogue(result);
    }

    public Vector2 getPlayerPosition() {
        return player.getPosition();
    }

    public String getTime() {
        return clock.getTime();
    }
    public int getDay() {
        return clock.getDay();
    }
    public String getDebugTime() {
        return clock.getDebugString();
    }
    public Facing getPlayerFacing() {
        return player.getFacing();
    }

    public float getPlayerWidth() {
        return player.getPlayerWidth();
    }

    public float getPlayerHeight() {
        return player.getPlayerHeight();
    }

    public Action getPlayerMovement() {
        return player.getMovement().iterator().next();
    }

    public DialogueManager getDialogueManager() {
        return dialogueManager;
    }

    public boolean noDialogueOnScreen() {
        return dialogueManager.queueEmpty();
    }

    /**
     * Gives the player an intro dialogue
     */
    public void pushWelcomeDialogue() {
        dialogueManager.addDialogue("You can move around with W,A,S,D and press E to interact with buildings and complete activities.\n"
                + "You cannot do anything after midnight and must sleep by interacting with a house. Good luck!");

        dialogueManager.addDialogue("Welcome to the Heslington Hustle v2.0 game by SKLOCH and Pitstop Piazza!");
    }

    /**
     * DEBUG
     * Displays a debug panel
     * TODO: Remove
     */
    public void pushTestDialogue() {
        // This is a debugging function that creates a useful control dialog box when you press '/'
        List<String> options = new ArrayList<>(Arrays.asList("End Game", "Decrement day", "Set time speed to VERY FAST", "Set time speed to normal", "Close"));
        dialogueManager.addDialogue("This is the debugging console. Please select an option", options, selectedOption -> {
            switch (selectedOption) {
                case 0: // Option 0 selected
                    setGameOver();
                    break;
                case 1: // Option 1 selected
                    clock.decrementDay();
                    break;
                case 2:
                    clock.setSpeed(15f);
                    break;
                case 3:
                    clock.setSpeed(1.5f);
                    break;
            }
        });
    }

    /**
     * Sets the player's energy to 100
     */
    public void replenishEnergy() {
        // This is the amount of energy that the player starts with at the beginning of each day
        energy = 100;
    }

    /**
     * Adds the number of hours slept until 8 am to the total amount
     * of hours slept
     */
    private void addHoursSlept() {
        // Before midnight
        if (clock.getRawTime() <= 1440) {
            hoursSlept += (1440 - clock.getRawTime()) + 8*60;
        } else {
            // After midnight
            hoursSlept += (8*60) - clock.getRawTime();
        }
    }


    /**
     * @return The total hours slept in the game
     */
    public int getHoursSlept() {
        return hoursSlept;
    }

    /**
     * Decreases the player's energy by a set amount
     * @param energyCost The amount to decrease by
     */
    private void exertEnergy(int energyCost) {
        energy -= energyCost;
    }

    /**
     * @return True if the player is near a trigger that can be interacted with
     */
    public boolean isInteractionPossible() {
        return  currentTrigger != null;
    }

    /**
     * Advances the game to the next day and restores the player's energy
     */
    public void nextDay() {
        clock.incrementDay();
        replenishEnergy();
    }

    /**
     * @return True if the last day has finished
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Sets the game to be over
     */
    public void setGameOver() {
        gameOver = true;
    }

    /**
     * @return The player's energy level, max is 100
     */
    public int getEnergy() {
        return energy;
    }

    /**
     * @return A hashmap of activities so their data can be used for scoring
     */
    public HashMap<String, Activity> getActivities() {
        return activities;
    }

    /**
     * @return True if the player walked at least x steps on each day
     */
    public boolean getPlayerStepAchievement() {
        return player.getStepAchievement();
    }
}
