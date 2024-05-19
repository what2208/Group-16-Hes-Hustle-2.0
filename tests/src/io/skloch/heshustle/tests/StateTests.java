package io.skloch.heshustle.tests;

import com.badlogic.gdx.maps.MapProperties;
import com.heslingtonhustle.sound.SoundController;
import com.heslingtonhustle.state.Activity;
import com.heslingtonhustle.state.DialogueManager;
import org.junit.Test;

import com.heslingtonhustle.state.State;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static org.junit.Assert.*;

@RunWith(GdxTestRunner.class)
public class StateTests {
    @Test
    public void testPassTime() {
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);
        State state = new State(soundController, dialogueManager);
        float delta = 0.5f;
        float newTimeUnits = 480 + delta * 1.5f;

        state.passTime(delta);

        assertEquals(newTimeUnits, state.getClock().getRawTime(), 0f);
    }

    @Test
    public void testSetNearestTrigger() {
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);
        State state = new State(soundController, dialogueManager);
        MapProperties newTrigger = new MapProperties();

        state.setNearestTrigger(newTrigger);

        assertEquals(newTrigger, state.getCurrentTrigger());
    }

    @Test
    public void testReplenishEnergy() {
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);
        State state = new State(soundController, dialogueManager);
        int newEnergy = 100;

        state.replenishEnergy();

        assertEquals(newEnergy, state.getEnergy());
    }

    @Test
    public void testSignHandleInteraction() {
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);
        State state = new State(soundController, dialogueManager);
        MapProperties trigger = new MapProperties();
        String key = "sign";
        String text = "Piazza Building";
        trigger.put(key, text);
        state.setNearestTrigger(trigger);

        state.handleInteraction();

        assertFalse(state.getDialogueManager().isEmpty());
        assertTrue(state.getDialogueManager().getUpdate());
    }

    @Test
    public void testSleepHandleInteraction() {
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);
        State state = new State(soundController, dialogueManager);
        MapProperties trigger = new MapProperties();
        String key = "sleep";
        String text = "Your House";
        trigger.put(key, text);
        state.setNearestTrigger(trigger);
        HashMap<String, Activity> newActivities = new HashMap<>();
        Activity sleepActivity = new Activity(
                "sleep",
                "sleep",
                5,
                0,
                8,
                -1);
        newActivities.put("sleep", sleepActivity);

        state.handleInteraction();

        assertFalse(state.getActivities().isEmpty());
    }

    @Test
    public void testNextDay() {
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);
        State state = new State(soundController, dialogueManager);
        int newDay = 2;
        int newEnergy = 100;

        state.nextDay();

        assertEquals(newDay, state.getClock().getDay());
        assertEquals(newEnergy, state.getEnergy());
    }

    @Test
    public void testIsInteractionPossibleWhenCurrentTriggerNull() {
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);
        State state = new State(soundController, dialogueManager);

        assertFalse(state.isInteractionPossible());
    }

    @Test
    public void testIsInteractionPossibleWhenCurrentTriggerNotNull() {
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);
        State state = new State(soundController, dialogueManager);
        MapProperties newTrigger = new MapProperties();

        state.setNearestTrigger(newTrigger);

        assertTrue(state.isInteractionPossible());
    }
}
