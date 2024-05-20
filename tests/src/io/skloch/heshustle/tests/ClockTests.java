package io.skloch.heshustle.tests;

import com.heslingtonhustle.state.Clock;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

public class ClockTests {
    @Test
    public void testIncrementDay() {
        Clock clock = new Clock();
        int newDay = 2;
        int timeUnits = 480;

        clock.incrementDay();

        assertEquals(newDay, clock.getDay());
        assertEquals(timeUnits, clock.getRawTime(), 0);
    }

    @Test
    public void testDecrementDay() {
        Clock clock = new Clock();
        int newDay = 0;

        clock.decrementDay();

        assertEquals(newDay, clock.getDay());
    }

    @Test
    public void testIncreaseTime() {
        Clock clock = new Clock();
        float speed = 1.5f;
        float delta = 2f;
        int newTimeUnits = 483;
        int newDay = 1;
        clock.setSpeed(speed);

        clock.increaseTime(delta);

        assertEquals(newDay, clock.getDay());
        assertEquals(newTimeUnits, clock.getRawTime(), 0);
    }

    @Test
    public void testIncreaseTimeAtMidnight() {
        Clock clock = new Clock();
        float speed = 1.5f;
        float delta = 640f;
        int newTimeUnits = 0;
        int newDay = 2;
        clock.setSpeed(speed);

        clock.increaseTime(delta);

        assertEquals(newDay, clock.getDay());
        assertEquals(newTimeUnits, clock.getRawTime(), 0);
    }

    @Test
    public void testGetTimeHourLessThanTwelve() {
        Clock clock = new Clock();
        int hour = 8;
        String minutes = "00";

        assertEquals(String.format("%d:%sam", hour, minutes), clock.getTime());
    }

    @Test
    public void testGetTimeHourTwentyFour() {
        Clock clock = new Clock();
        float newTimeUnits = 1440f;
        clock.setTime(newTimeUnits);
        String minutes = "00";

        assertEquals(String.format("12:%sam", minutes), clock.getTime());
    }

    @Test
    public void testGetTimeHourZero() {
        Clock clock = new Clock();
        float newTimeUnits = 0f;
        clock.setTime(newTimeUnits);
        String minutes = "00";

        assertEquals(String.format("12:%sam", minutes), clock.getTime());
    }

    @Test
    public void testGetTimeHourTwelve() {
        Clock clock = new Clock();
        float newTimeUnits = 722f;
        clock.setTime(newTimeUnits);
        String minutes = "02";

        assertEquals(String.format("12:%spm", minutes), clock.getTime());
    }

    @Test
    public void testGetTimeHourMoreThanTwelve() {
        Clock clock = new Clock();
        float newTimeUnits = 932f;
        int hour = 15;
        clock.setTime(newTimeUnits);
        String minutes = "32";

        assertEquals(String.format("%d:%spm", hour-12, minutes), clock.getTime());
    }

    @Test
    public void testPassHoursSameDay() {
        Clock clock = new Clock();
        float hoursToPass = 2.5f;
        float newUnits = 630f;
        int newDay = 1;

        clock.passHours(hoursToPass);

        assertEquals(newDay, clock.getDay());
        assertEquals(newUnits, clock.getRawTime(), 0.0f);
    }

    @Test
    public void testPassHoursNewDay() {
        Clock clock = new Clock();
        float hoursToPass = 2.5f;
        float setTimeUnits = 1390f;
        float newTimeUnits = 100f;
        int newDay = 2;

        clock.setTime(setTimeUnits);
        clock.passHours(hoursToPass);

        assertEquals(newDay, clock.getDay());
        assertEquals(newTimeUnits, clock.getRawTime(), 0.0f);
    }
}
// public void passHours(float hours) {
//        timeUnits += hours * 60;
//        if (timeUnits >= 1440) {
//            timeUnits -= 1440;
//            day += 1;
//        }
//    }