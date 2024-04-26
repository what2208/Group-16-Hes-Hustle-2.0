package com.heslingtonhustle.state;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.heslingtonhustle.map.MapManager;
import com.heslingtonhustle.sound.SoundController;
import org.w3c.dom.css.Rect;

import java.util.*;


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
    private final HashMap<String, Activity> activities;
    private Trigger currentTrigger;
    private int score;
    private int energy;

    public State(MapManager mapManager, SoundController soundController, float playerWidth, float playerHeight) {
        gameOver = false;

        player = new Player(38.25f, 57.25f, playerWidth, playerHeight);
        clock = new Clock();
        this.mapManager = mapManager;
        dialogueManager = new DialogueManager(soundController);
        this.soundController = soundController;

        activities = new HashMap<>();
        activities.put("eat", new Activity(2));
        activities.put("recreation", new Activity(3));
        activities.put("study", new Activity(2));
        activities.put("sleep", new Activity());

        score = 0;
        replenishEnergy();
        currentTrigger = null; // This stores the Tiled trigger box that we are currently stood inside of
    }

    /**
     * Given an Action, apply that action to the state.
     *
     * @param action The most recent action
     * @param actions The actions corresponding to all keypresses
     * @param timeDelta Delta time
     * */
    public void update(Action action, HashSet<Action> actions, float timeDelta) {
        currentTrigger = mapManager.getTrigger(player.getCollisionBox());
        if (action != null) {
            handleAction(action);
        }
        // Give the player the list of movements it needs to make
        // Player can only move if dialogue box is not shown
        if (dialogueManager.isEmpty()) {
            player.move(actions);
        } else {
            player.freeze();
        }
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
     * @param action
     */
    private void handleAction(Action action) {
        if (!dialogueManager.isEmpty()) {
            // A dialogue box is currently being displayed
            handleDialogueAction(action);
        } else if (action == Action.INTERACT) {
            handleInteraction();
        } else {
            // We have a normal action
//            player.move(action);
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
        }

        score += currentTrigger.changeScore();

        if (currentTrigger.canSleep()) {
            advanceDay();
            Activity activity  = activities.get("sleep");
            if (activity != null) {
                activity.increaseValue(1);
                dialogueManager.addDialogue("You have just slept!");
            }
        }
        
        String activityID = currentTrigger.getActivity();
        if (activityID != null) {
            int i = currentTrigger.getValue();
            if (!activities.containsKey(activityID)) {
                activities.put(activityID, new Activity()); // Useful feature?
            }
            Activity activity = activities.get(activityID);
            if (canDoActivity(activity)) {
                activity.increaseValue(i);
                exertEnergy(currentTrigger.getEnergyCost());
                dialogueManager.addDialogue(currentTrigger.getSuccessMessage());
            } else {
                dialogueManager.addDialogue(currentTrigger.getFailedMessage());
            }
        }
    }

    private boolean canDoActivity(Activity activity) {
        // Check if the player is allowed to do the activity that is assigned to the current
        // trigger that the player is stood in
        if (!activity.canIncreaseValue()) {
            return false;
        }
        if (!hasEnoughEnergy(currentTrigger.getEnergyCost())) {
            return false;
        }
        if (clock.getRawTime() > 750) {
            return false;
        }
        return true;
    }

    private void advanceDay() {
        if (clock.getDay() >= MAX_DAYS) {
            printActivities();
            dialogueManager.addDialogue("Game Over. Your score was: "+score, selectedOption -> {
                gameOver = true;
            });
            return;
        }

        // The player is only allowed to do the same activity twice once in a day
        Activity study = activities.get("study");
        if (study.getTimesPerformedToday() == 2) {
            study.changeMaxTimesPerDay(1);
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
            builder.append(s).append(" count: ").append(activities.get(s).getCount()).append("\n");
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

    public void pushWelcomeDialogue() {
        dialogueManager.addDialogue("Welcome to the Heslington Hustle v2.0 game by SKLOCH! and Pitstop Piazza\n" +
                "You can move around the map with W,A,S,D and interact with buildings with SPACE to do activities.\n" +
                "You cannot do anything at night time and must sleep by interacting with a house. Good luck!");
    }

    public void pushTestDialogue() {
        // This is a debugging function that creates a useful control dialog box when you press '/'
        List<String> options = new ArrayList<>(Arrays.asList("Increment day", "Decrement day", "Set time speed to VERY FAST", "Set time speed to normal", "Close"));
        dialogueManager.addDialogue("This is the debugging console. Please select an option", options, selectedOption -> {
            switch (selectedOption) {
                case 0: // Option 0 selected
                    advanceDay();
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

    public void replenishEnergy() {
        // This is the amount of energy that the player starts with at the beginning of each day
        energy = 15;
    }
    
    private void exertEnergy(int energyCost) {
        energy -= energyCost;
    }

    private boolean hasEnoughEnergy(int energyCost) {
        return energy - energyCost >= 0;
    }

    public boolean isInteractionPossible() {
        return  currentTrigger != null;
    }

    public void nextDay() {
        clock.incrementDay();
        replenishEnergy();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver() {
        gameOver = true;
    }

    public int getEnergy() {
        return energy;
    }
}
