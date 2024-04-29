package com.heslingtonhustle.state;

/**
 * Stores information about a type of activity completed.
 * Each activity should have a name, score and a class of score it contributes to.
 * Either 'sleep', 'study', 'recreational', or 'eating'
 * A max number of times completed in a day can also be set
 */
public class Activity {
    private String name;
    private String scoreType;
    private int score;
    private int energyUse;
    private int hours;
    private int timesCompleted = 0;
    private int timesCompletedToday = 0;
    private int hoursSpent = 0;
    private int maxPerDay;


    /**
     * Initialise a new instance of this activity to keep track of
     * how many times it has been performed.
     * @param name Name of the activity
     * @param scoreType Which type of score it should contribute to,
     *                  'sleep', 'study', 'recreational' or 'eat'
     * @param score The score this activity should give each time it is completed
     * @param energyUse The energy completing this activity uses
     * @param hours The hours this activity takes to complete
     * @param dayLimit The maximum number of times this activity can be done each day
     *                 set to -1 for none.
     */
    public Activity(String name, String scoreType, int score, int energyUse, int hours, int dayLimit) {
        this.name = name;
        this.scoreType = scoreType; // Could make this an enum
        this.energyUse = energyUse;
        this.hours = hours;
        this.score = score;
        this.maxPerDay = dayLimit;
    }


    /**
     * @return The name of the activity
     */
    public String getName() {
        return name;
    }

    /**
     * @return The type of score this activity contributes to
     * Either 'sleep', 'study', 'recreation' or 'eat'
     */
    public String getScoreType() {
        return scoreType;
    }

    /**
     * @return The score the activity should give
     */
    public int getScore() {
        return score;
    }

    /**
     * @return The energy required to complete the activity
     */
    public int getEnergyUse() {
        return energyUse;
    }

    /**
     * @return The hours passed when completing the activity
     */
    public int getHours() {
        return hours;
    }

    /**
     * @return The total number of times the activity has been done
     */
    public int getTimesCompleted() {
        return timesCompleted;
    }

    /**
     * @return The number of times this activity has been completed today
     */
    public int getTimesCompletedToday() {
        return timesCompletedToday;
    }

    /**
     * @return The total hours spent doing this particular activity
     */
    public int getHoursSpent() {
        return hoursSpent;
    }

    /**
     * Increments the value for the number of times the activity has
     * been completed. Doesn't check for any limits
     */
    public void completeActivity() {
        timesCompleted++;
        timesCompletedToday++;
        hoursSpent += hours;
    }

    /**
     * @return True if this activity will not go over its limit for the day
     */
    public boolean canDoActivity() {
        if (maxPerDay == -1 || timesCompletedToday < maxPerDay) {
            return true;
        }
        return false;
    }

    /**
     * Resets the number of times the activity has been done today
     */
    public void dayAdvanced() {
        timesCompletedToday = 0;
    }

    /**
     * Use to set a new daily limit on the activity
     * @param times The new max number of times per day
     */
    public void setMaxPerDay(int times) {
        maxPerDay = times;
    }
}
