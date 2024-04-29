package com.heslingtonhustle.state;

/**
 * A class to manage the day and time
 * Provides methods to get a formatted time and day
 */
public class Clock {
    private float speed;
    private float timeUnits;
    private int day;

    /**
     * Initialises default clock values
     */
    public Clock() {
        timeUnits = 480;
        day = 1;
        speed = 1.5f; // Probably want this to be less
    }


    /**
     * @return A 12 hour representation of the current in game time as a string
     */
    public String getTime() {
        int hour = Math.floorDiv((int) timeUnits, 60);
        String minutes = String.format("%02d", ((int) timeUnits - hour * 60));

        // Make 12 hour
        if (hour == 24 || hour == 0) {
            return String.format("12:%sam", minutes);
        } else if (hour == 12) {
            return String.format("12:%spm", minutes);
        } else if (hour > 12) {
            return String.format("%d:%spm", hour-12, minutes);
        } else {
            return String.format("%d:%sam", hour, minutes);
        }
    }

    /**
     * @return The raw amount of time units elapsed in the day
     */
    public float getRawTime() {
        return timeUnits;
    }

    /**
     * Set the time to a certain value in 'seconds'
     * @param timeUnits The value to set the time to in 'seconds'
     */
    public void setTime(float timeUnits) {
        this.timeUnits = timeUnits;
    }

    /**
     * @return The current day number
     */
    public int getDay() {
        return day;
    }

    /**
     * Increases time by a certain amount, increments the day if necessary
     * @param delta The real time passed in seconds
     */
    public void increaseTime(float delta) {
        timeUnits += delta * speed;
        if (timeUnits >= 1440) {
            timeUnits -= 1440;
            day += 1;
        }
    }

    /**
     * Passes a number of hours of in game time
     * @param hours The hours to pass
     */
    public void passHours(float hours) {
        timeUnits += hours * 60;
        if (timeUnits >= 1440) {
            timeUnits -= 1440;
            day += 1;
        }
    }

    /**
     * Increments the day by 1
     */
    public void incrementDay() {
        // 7 am
        timeUnits = 480;
        day += 1;
    }

    // Debug methods

    /**
     * Decrements the day by 1, used for debugging
     */
    public void decrementDay() {
        timeUnits = 0;
        day -= 1;
    }

    /**
     * Changes the speed at which time elapses
     * @param speed The speed multiplier to set
     */
    public void setSpeed(float speed) {
        // This is mainly used for debugging
        this.speed = speed;
    }

    /**
     * @return Debug information about the current time
     */
    public String getDebugString() {
        return timeUnits +" "+getTime() + " Day: "+getDay() + " Speed: "+speed;
    }
}
