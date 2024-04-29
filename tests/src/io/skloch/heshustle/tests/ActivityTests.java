package io.skloch.heshustle.tests;

import com.heslingtonhustle.state.Activity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActivityTests {
    /**
     * Tests whether an Activity correctly returns information about
     * itself
     */
    @Test
    public void getActivityAttributes() {
        Activity activity = new Activity(
                "fishing",
                "recreational",
                100,
                15,
                2,
                3
        );


        // Test attributes
        assertEquals("fishing", activity.getName());
        assertEquals("recreational", activity.getScoreType());
        assertEquals(100, activity.getScore());
        assertEquals(15, activity.getEnergyUse());
        assertEquals(2, activity.getHours());

        // Test other baseline attributes
        assertEquals(0, activity.getTimesCompleted());
        assertEquals(0, activity.getTimesCompletedToday());
        assertEquals(0, activity.getHoursSpent());

    }


    /**
     * A test to ensure an Activity's attributes update correctly when
     * the activity is completed.
     */
    @Test
    public void testCompleteActivity() {
        Activity activity = new Activity(
                "fishing",
                "recreational",
                100,
                15,
                2,
                3
        );


        // Should increase the number of times it has been completed,
        // and the hours spent doing it
        activity.completeActivity();

        assertEquals(1, activity.getTimesCompleted());
        assertEquals(1, activity.getTimesCompletedToday());
        assertEquals(2, activity.getHoursSpent());
    }

//counter++;
//        timesPerformedToday+
/** World world = new World();
 Castle castle = new Castle(world);
 int healthPoints = castle.getHealthPoints();

 castle.damage();
 assertEquals(healthPoints - 1, castle.getHealthPoints());*/

/**public Activity() {
 counter = 0;
 value = 0; // Represents different things depending on the type of activity
 // The value can be specified in the Tiled trigger (e.g. eat: 2)
 timesPerformedToday = 0;
 maxTimesPerDay = 1;
 }

 public void increaseValue(int value) {
 this.value += value;
 incrementCounter();
 }*/
}
