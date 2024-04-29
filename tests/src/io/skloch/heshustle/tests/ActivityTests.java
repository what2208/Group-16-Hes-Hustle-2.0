package io.skloch.heshustle.tests;

import com.heslingtonhustle.state.Activity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActivityTests {
    @Test
    public void testIncreaseValue() {
        Activity activity = new Activity();
        int increaseValue = 1;
        int newCounter = 1;
        int newTimesPerformedToday = 1;

        activity.increaseValue(increaseValue);

        assertEquals(increaseValue, activity.getValue());
        assertEquals(newCounter, activity.getCount());
        assertEquals(newTimesPerformedToday, activity.getTimesPerformedToday());
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
