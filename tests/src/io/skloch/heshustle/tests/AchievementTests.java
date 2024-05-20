package io.skloch.heshustle.tests;

import org.junit.Test;
import com.heslingtonhustle.state.Achievement;

import static org.junit.Assert.*;

public class AchievementTests {

    @Test
    public void testIsPositive() {
        String title = "Achievement 1";
        String description = "The first achievement";
        int score = 200;
        Achievement achievement = new Achievement(title, description, score);

        assertTrue(achievement.isPositive());
    }

    @Test
    public void testIsPositiveWhenPenalty() {
        String title = "Achievement 1";
        String description = "The first achievement";
        int score = -200;
        Achievement achievement = new Achievement(title, description, score);

        assertFalse(achievement.isPositive());
    }

}