package io.skloch.heshustle.tests;

import com.heslingtonhustle.state.Activity;
import com.heslingtonhustle.state.LeaderboardManager;
import org.junit.Test;

import static org.junit.Assert.*;

public class LeaderboardManagerTests {


    /**
     * Tests whether the leaderboard will accept valid player names
     */
    @Test
    public void testValidNames() {
        assertTrue(LeaderboardManager.isValidName("Joe"));
        assertTrue(LeaderboardManager.isValidName("Joe777"));
        assertTrue(LeaderboardManager.isValidName("Joe 777"));
    }

    /**
     * Tests whether the leaderboard will accept valid player names
     */
    @Test
    public void testInvalidNames() {
        // Reject blank names
        assertFalse(LeaderboardManager.isValidName(""));
        // Reject names with special symbols
        assertFalse(LeaderboardManager.isValidName("Joe-777"));
        // Reject names that are just spaces
        assertFalse(LeaderboardManager.isValidName("           "));
        assertFalse(LeaderboardManager.isValidName(null));
    }
}