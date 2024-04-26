package com.heslingtonhustle.input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.heslingtonhustle.state.Action;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Handles the user's inputs and translates these into Actions {@link Action} that the game can understand
 * The set heldActions contains Actions called by keys that are being held down
 * The set pressedActions only contains Actions called on the pressed frame, and should be wiped afterwards
 */
public class KeyboardInputHandler extends InputAdapter implements InputHandler {
    private final HashMap<Integer, Action> inputMap;
    private final HashSet<Action> heldActions;
    private final HashSet<Action> pressedActions;

    /**
     * Assigns default keys to Actions, initialises empty sets heldActions and pressedActions to grab actions from
     */
    public KeyboardInputHandler() {

        // Maps keys to actions
        inputMap = new HashMap<>();

        // Sets for held and pressed Actions
        heldActions = new HashSet<>();
        pressedActions = new HashSet<>();

        // Player movement keys
        inputMap.put(Keys.D, Action.MOVE_RIGHT);
        inputMap.put(Keys.A, Action.MOVE_LEFT);
        inputMap.put(Keys.W, Action.MOVE_UP);
        inputMap.put(Keys.S, Action.MOVE_DOWN);

        inputMap.put(Keys.RIGHT, Action.MOVE_RIGHT);
        inputMap.put(Keys.LEFT, Action.MOVE_LEFT);
        inputMap.put(Keys.UP, Action.MOVE_UP);
        inputMap.put(Keys.DOWN, Action.MOVE_DOWN);

        // Other keys
        inputMap.put(Keys.SPACE, Action.INTERACT);
        inputMap.put(Keys.ENTER, Action.INTERACT);
        inputMap.put(Keys.I, Action.INTERACT);
        inputMap.put(Keys.E, Action.INTERACT);

        inputMap.put(Keys.ESCAPE, Action.PAUSE);

        inputMap.put(Keys.COMMA, Action.DEBUGGING_ACTION1);
        inputMap.put(Keys.PERIOD, Action.DEBUGGING_ACTION2);
        inputMap.put(Keys.SLASH, Action.DEBUGGING_ACTION3);
    }

    /**
     * Removes the corresponding action of the key to the sets heldActions and pressedActions (if not cleared already)
     *
     * @param keycode one of the constants in {@link com.badlogic.gdx.Input.Keys}
     * @return True if key corresponded to an actions, false otherwise
     */
    @Override
    public boolean keyDown(int keycode) {
        if (!inputMap.containsKey(keycode)) return false;

        heldActions.add(inputMap.get(keycode));
        pressedActions.add(inputMap.get(keycode));

        return true;
    }

    /**
     * Adds the corresponding action of the key to the sets heldActions and pressedActions
     *
     * @param keycode one of the constants in {@link com.badlogic.gdx.Input.Keys}
     * @return True if key corresponded to an actions, false otherwise
     */
    @Override
    public boolean keyUp(int keycode) {
        if (!inputMap.containsKey(keycode)) return false;

        heldActions.remove(inputMap.get(keycode));
        pressedActions.remove(inputMap.get(keycode));

        return true;
    }

    /**
     * @return Returns a set of the actions corresponding to the currently pressed keys
     */
    public HashSet<Action> getHeldActions() {
        return heldActions;
    }

    /**
     * @return The actions corresponding to the keys pressed on this frame only
     */
    public HashSet<Action> getPressedActions() {
        return pressedActions;
    }

    /**
     * Resets the pressed actions for the next frame
     */
    public void resetPressedActions() {
        pressedActions.clear();
    }
}
