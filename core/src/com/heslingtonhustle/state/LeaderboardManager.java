package com.heslingtonhustle.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.sun.tools.javac.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

/**
 * A class to manage reading from and writing to the leaderboard
 * Can also handle inserting a new element into the leaderbaord
 */
public class LeaderboardManager {
    // Regex to check names against
    private static final Pattern namePattern = Pattern.compile("[^a-zA-Z0-9 ]");

    /**
     * A small class to store information about a name and a score
     */
    private static class Score {
        public String name;
        public String score;
        private Score(String name, String score) {
            this.name = name;
            this.score = score;
        }
    }

    /**
     * Checks whether a name is valid to add to the leaderboard
     * @param name The name to add
     * @return True if the name is valid
     */
    public static boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        name = name.trim();
        // Check for blank name
        if (name.replace(" ", "").isEmpty()) {
            return false;
        }

        // A name only contains letters and numbers
        return !namePattern.matcher(name).find();
    }

    /**
     * Loads data from the leaderboard file
     * @return A list of strings, with each string representing
     * a line on the leaderboard
     */
    public static Array<String> getScores() {
        Array<String> lines = new Array<>();

        // Read leaderboard data
        Array<Score> scores = readValues();

        for (Score score : scores) {
            lines.add(score.name + " - " + score.score);
        }

        return lines;
    }

    /**
     * Reads the two raw strings from each line in the leaderboard file
     * Returns values as a linked hash map to preserve order and pairings
     * Should be formatted further to Strings or String int pairs
     * @return A LinkedHashMap containing names and string scores
     */
    private static Array<Score> readValues() {
        Array<Score> scores = new Array<>();
        FileHandle file = Gdx.files.local("leaderboard.txt");

        // If there isn't a leaderboard file, just return the empty hashmap
        if (!file.exists()) {
            return scores;
        }

        String data = file.readString();
        int count = 0;
        // Split into lines
        for (String line : data.split("\n")) {
            // Ignores blank lines and lines without a comma
            if (line.contains(",")) {
                scores.add(
                        new Score(
                                line.split(",")[0],
                                line.split(",")[1].replaceAll("\r", "")
                        )
                );
                count += 1;
            }
            // Only return the top 10 values
            if (count == 10) {
                break;
            }
        }

        return scores;
    }


    /**
     * Writes a player's score to the leaderboard at the correct position
     * @param newName The name of the player
     * @param newScore The score the player got
     * @return True if the score was added to the leaderboard
     */
    public static boolean writeScore(String newName, int newScore) {
        Array<Score> scores = readValues();
        FileHandle file = Gdx.files.local("leaderboard.txt");

        String stringToWrite = "";
        boolean scoreWritten = false;
        int totalScoresWritten = 0;

        // Write values back to leaderboard, inserting the new score if
        // the new score is greater than the value to be written

        for (Score score : scores) {
            // New score is greater
            if (newScore > Integer.parseInt(score.score) && !scoreWritten) {
                stringToWrite += newName + "," + newScore + "\n";
                scoreWritten = true;
                totalScoresWritten++;
            }
            // Write old score
            stringToWrite += score.name + "," + score.score + "\n";
            totalScoresWritten++;

            if (totalScoresWritten == 10) {
                break;
            }

        }

        // Append to the end of the leaderboard
        if (totalScoresWritten < 10 && !scoreWritten) {
            stringToWrite += newName + "," + newScore;
            scoreWritten = true;
        }

        file.writeString(stringToWrite, false);

        return scoreWritten;
    }
}
