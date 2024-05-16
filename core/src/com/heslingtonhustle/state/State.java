package com.heslingtonhustle.state;

import com.badlogic.gdx.maps.MapProperties;
import com.heslingtonhustle.sound.SoundController;
import java.util.HashMap;
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
    private final Clock clock;
    private final SoundController soundController;
    private final DialogueManager dialogueManager;
    private final HashMap<String, Activity> activities = new HashMap<>();
    // A reference to the current closest trigger the player can interact with
    private MapProperties currentTrigger;
    private int energy;
    private int hoursSlept;


    public State(SoundController soundController, DialogueManager dialogueManager) {
        gameOver = false;

        clock = new Clock();
        this.soundController = soundController;
        this.dialogueManager = dialogueManager;

        replenishEnergy();
        currentTrigger = null; // This stores the Tiled trigger box that we are currently stood inside of
    }


    /**
     * Adds the amount of time passed to the in game clock
     * @param delta The time to add in seconds
     */
    public void passTime(float delta) {
        clock.increaseTime(delta);
    }


    /**
     * Tells the game which trigger the player is nearest in
     * case they want to interact with it.
     * @param trigger The closest trigger object
     */
    public void setNearestTrigger(MapProperties trigger) {
        currentTrigger = trigger;
    }


    public MapProperties getNearestTrigger() {
        return currentTrigger;
    }


    public void handleInteraction() {
        if (currentTrigger == null) {
            return;
        }

        // Read sign
        if (currentTrigger.containsKey("sign")) {
            dialogueManager.addDialogue((String) currentTrigger.get("sign"));
            return;
        }

        // Talk to NPC
        if (currentTrigger.containsKey("dialogue")) {
            // Show dialogue
            dialogueManager.addDialogue((String) currentTrigger.get("dialogue"));
            return;
        }

        if (currentTrigger.containsKey("new_map")) {
            return;
        }

        // Sleep at a house
        if (currentTrigger.containsKey("sleep")) {
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
        if (currentTrigger.containsKey("activity")) {
            String activityName = (String) currentTrigger.get("activity");

            // On the first encounter with an activity, create a new Activity class
            // to store how many times this activity has been completed
            if (!activities.containsKey(activityName)) {
                // Creates an activity class from MapProperties and stores it
                // with the activity name
                activities.put(activityName, Activity.toActivity(currentTrigger));
            }

            Activity activity = activities.get(activityName);

            // Call a dialogue box to prompt the player if they want to do
            // the listed activity
            List<String> options = new ArrayList<>(Arrays.asList("Yes", "No"));
            dialogueManager.addDialogue((String) currentTrigger.get("prompt_message"), options, selectedOption -> {
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
            // Gets the success message from the most recent trigger
            dialogueManager.addDialogue((String) currentTrigger.get("success_message"));
        }
    }

    private void advanceDay() {
        if (clock.getDay() >= MAX_DAYS) {
            dialogueManager.addDialogue("Today is the day of your exam!" +
                    "\nI hope you studied well!", selectedOption -> gameOver = true);
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

    public String getTime() {
        return clock.getTime();
    }
    public int getDay() {
        return clock.getDay();
    }
    public String getDebugTime() {
        return clock.getDebugString();
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
    public void pushStartDayDialogue() {
        int day = clock.getDay();
        if (day != MAX_DAYS) {
            dialogueManager.addDialogue(
                    String.format("You have %s days left until your exam!\nMake sure you study, eat and have fun!", MAX_DAYS-day+1));
        } else {
            dialogueManager.addDialogue("Your exam is tomorrow\nI hope you've been ");
        }

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
        return currentTrigger != null;
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
     * @return The instance of the clock
     */
    public Clock getClock() { return clock; }

    /**
     * @return The current trigger
     */
    public MapProperties getCurrentTrigger() { return currentTrigger; }
}
