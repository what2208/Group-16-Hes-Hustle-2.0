package com.heslingtonhustle.state;

/**
 * A class to store information about an achievement the player
 * may or may not have obtained.
 * Stores a title, description and a score.
 * Achievements can be negative as 'penalties'
 */
public class Achievement {
    private String title;
    private String description;
    private int score;

    /**
     * Defines an achievement with a title, description and score
     * @param title The name to display in the banner
     * @param description Information about why the player got this achievement
     * @param score The score to give, can be negative
     */
    public Achievement(String title, String description, int score) {
        this.title = title;
        this.description = description;
        this.score = score;
    }

    /**
     * @return The title of the achievement
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return The description of the achievement
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The score the achievement gives, can be negative
     */
    public int getScore() {
        return score;
    }

    /**
     * Checks whether the achievement is a penalty or not
     * @return True if the achievement is not a penalty
     */
    public boolean isPositive() {
        return score > 0;
    }
}
