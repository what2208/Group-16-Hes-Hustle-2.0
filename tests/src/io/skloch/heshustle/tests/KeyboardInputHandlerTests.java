package io.skloch.heshustle.tests;

import com.heslingtonhustle.input.KeyboardInputHandler;
import com.heslingtonhustle.state.Action;
import org.junit.Test;
import com.badlogic.gdx.Input.Keys;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class KeyboardInputHandlerTests {

    @Test
    public void testKeyDownValidKey() {
        KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler();
        int keycode = Keys.D;
        HashSet<Action> newHeldActions = new HashSet<>();
        HashSet<Action> newPressedActions = new HashSet<>();
        newHeldActions.add(Action.MOVE_RIGHT);
        newPressedActions.add(Action.MOVE_RIGHT);

        keyboardInputHandler.keyDown(keycode);

        assertEquals(newHeldActions, keyboardInputHandler.getHeldActions());
        assertEquals(newPressedActions, keyboardInputHandler.getPressedActions());
    }

    @Test
    public void testKeyDownInvalidKey() {
        KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler();
        int keycode = Keys.P;
        assertFalse(keyboardInputHandler.keyDown(keycode));
    }

    @Test
    public void testKeyUpValidKey() {
        KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler();
        int keycode = Keys.D;
        HashSet<Action> newHeldActions = new HashSet<>();
        HashSet<Action> newPressedActions = new HashSet<>();

        keyboardInputHandler.keyUp(keycode);

        assertEquals(newHeldActions, keyboardInputHandler.getHeldActions());
        assertEquals(newPressedActions, keyboardInputHandler.getPressedActions());
    }



    @Test
    public void testKeyUpInvalidKey() {
        KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler();
        int keycode = Keys.P;
        assertFalse(keyboardInputHandler.keyUp(keycode));
    }

    @Test
    public void testResetPressedActions() {
        KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler();
        int newPressedActionsLength = 0;

        keyboardInputHandler.resetPressedActions();

        assertEquals(newPressedActionsLength, keyboardInputHandler.getPressedActions().size());
    }
}