package io.skloch.heshustle.tests;

import com.heslingtonhustle.sound.SoundController;
import com.heslingtonhustle.state.Action;
import com.heslingtonhustle.state.DialogueManager;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(GdxTestRunner.class)
public class DialogueManagerTests {
    @Test
    public void testHandleActionMoveUp() {
        HashSet<Action> pressedActions = new HashSet<Action>();
        pressedActions.add(Action.MOVE_UP);
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);
        List<String> options = new ArrayList<String>();
        options.add("option1");
        options.add("option2");
        options.add("option3");
        dialogueManager.addDialogue("message", options);
        int newSelectedOption = 1;

        dialogueManager.handleAction(pressedActions);
        dialogueManager.handleAction(pressedActions);

        assertEquals(newSelectedOption, dialogueManager.getSelectedOption());
    }

    @Test
    public void testHandleActionMoveDown() {
        HashSet<Action> pressedActions = new HashSet<Action>();
        pressedActions.add(Action.MOVE_DOWN);
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);
        List<String> options = new ArrayList<String>();
        options.add("option1");
        options.add("option2");
        options.add("option3");
        dialogueManager.addDialogue("message", options);
        int newSelectedOption = 2;

        dialogueManager.handleAction(pressedActions);
        dialogueManager.handleAction(pressedActions);

        assertEquals(newSelectedOption, dialogueManager.getSelectedOption());
    }

    //public boolean isEmpty() {
    //        return dialogueQueue.isEmpty();
    //    }
    @Test
    public void testIsEmptyWhenEmpty() {
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);

        assertTrue(dialogueManager.isEmpty());
    }

    @Test
    public void testIsEmptyWhenNotEmpty() {
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);
        List<String> options = new ArrayList<String>();
        options.add("option1");
        options.add("option2");
        options.add("option3");
        dialogueManager.addDialogue("message", options);

        assertFalse(dialogueManager.isEmpty());
    }

    @Test
    public void testGetMessageWhenEmptyQueue() {
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);

        assertNull(dialogueManager.getMessage());
    }

    @Test
    public void testGetMessageWhenExists() {
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);
        List<String> options = new ArrayList<String>();
        options.add("option1");
        options.add("option2");
        options.add("option3");
        String returnedMessage = "message";
        dialogueManager.addDialogue(returnedMessage, options);

        assertEquals(returnedMessage, dialogueManager.getMessage());
    }

    @Test
    public void testGetOptionsWhenEmptyQueue() {
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);

        assertNull(dialogueManager.getOptions());
    }

    @Test
    public void testGetOptionsWhenExists() {
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);
        List<String> options = new ArrayList<String>();
        options.add("option1");
        options.add("option2");
        options.add("option3");
        String returnedMessage = "message";
        dialogueManager.addDialogue(returnedMessage, options);

        assertEquals(options, dialogueManager.getOptions());
    }

    @Test
    public void testSelectedOptionWhenEmptyQueue() {
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);

        assertEquals(-1, dialogueManager.getSelectedOption());
    }


}