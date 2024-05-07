package com.heslingtonhustle.state;

import com.heslingtonhustle.sound.SoundController;
import com.heslingtonhustle.sound.Sounds;

import java.util.*;

/**
 * A class to store different screens of dialogue to display to the player
 * The next dialogue can be viewed with an interact button
 * Dialogue boxes can contain only text or also contain a selection
 */
public class DialogueManager {

    /**
     * A class to store the data provided to the player in a dialogue box
     * DialogueBox is a private inner class that can only be interacted with through DialogueManager
     * Public methods within DialogueBox are only accessible to DialogueManager
     */
    private class DialogueBox {
        private final String message;
        private final List<String> options;
        private int selectedOption;
        private final DialogueCallback callback;


        /**
         * Call to set the data displayed in the dialoguebox
         * @param message The text to display
         * @param options The options to choose from, can be null
         * @param callback The lambda function to run afterwards, passed the chosen option
         */
        private DialogueBox(String message, List<String> options, DialogueCallback callback) {
            this.message = message;
            this.options = options;
            this.callback = callback;
        }

        /**
         * Moves the selection pointer down
         */
        private void decreaseSelection() {
            if (options != null) {
                selectedOption = (selectedOption+options.size()-1) % options.size();
            } else {
                selectedOption = -1;
            }
        }

        /**
         * Moves the selection pointer up
         */
        private void increaseSelection() {
            if (options != null) {
                selectedOption = (selectedOption + 1) % options.size();
            } else {
                    selectedOption = -1;
                }
        }

        /**
         * Runs the provided lambda function and passes in the selected option
         */
        private void submit() {
            if (callback != null) {
                callback.onSelected(selectedOption);
            }
        }
    }


    // ~~~ DialogueManager's properties and methods ~~~

    private final Queue<DialogueBox> dialogueQueue;
    // True if changes have been made that means the dialogue box should be re-drawn
    private boolean update;

    private final SoundController soundController;

    /**
     * Initialises a queue to store dialogueboxes, and stores the soundcontroller to play
     * dialogue sounds
     * @param soundController The sound controller used by the game
     */
    public DialogueManager(SoundController soundController) {
        this.soundController = soundController;
        dialogueQueue = new LinkedList<DialogueBox>();
    }

    /**
     * Reacts to a set of actions
     * @param pressedActions A set of pressed actions this frame
     */
    public void handleAction(HashSet<Action> pressedActions) {
        for (Action action : pressedActions)
            switch (action) {
                case MOVE_UP:
                    decreaseSelection();
                    break;
                case MOVE_DOWN:
                    increaseSelection();
                    break;
                case INTERACT:
                    submit();
            }
    }

    /**
     * Checks if the dialogue queue is empty
     * @return True if the queue is empty
     */
    public boolean isEmpty() {
        return dialogueQueue.isEmpty();
    }

    /**
     * Adds a dialogue box to the back of the queue
     * @param message The message to display
     * @param options The options the player can choose from
     * @param callback The lambda to function to run when the player confirms
     */
    public void addDialogue(String message, List<String> options, DialogueCallback callback) {
        DialogueBox dialogueBox = new DialogueBox(message, options, callback);
        dialogueQueue.add(dialogueBox);
        update = true;
        soundController.playSound(Sounds.DIALOGUEOPEN);
    }

    /**
     * Adds a dialogue box to the back of the queue
     * @param message The message to display
     * @param options The options the player can choose from
     */
    public void addDialogue(String message, List<String> options) {
        DialogueBox dialogueBox = new DialogueBox(message, options, null);
        dialogueQueue.add(dialogueBox);
        update = true;
        soundController.playSound(Sounds.DIALOGUEOPEN);
    }

    /**
     * Adds a dialogue box to the back of the queue
     * @param message The message to display
     */
    public void addDialogue(String message) {
        DialogueBox dialogueBox = new DialogueBox(message, null, null);
        dialogueQueue.add(dialogueBox);
        update = true;
        soundController.playSound(Sounds.DIALOGUEOPEN);
    }

    /**
     * Adds a dialogue box to the back of the queue
     * @param message The message to display
     * @param callback The lambda to function to run when the player confirms
     */
    public void addDialogue(String message, DialogueCallback callback) {
        DialogueBox dialogueBox = new DialogueBox(message, null, callback);
        dialogueQueue.add(dialogueBox);
        update = true;
        soundController.playSound(Sounds.DIALOGUEOPEN);
    }

    /**
     * Returns the message on the currently displayed dialoguebox
     * @return The message on the dialoguebox
     */
    public String getMessage() {
        if (dialogueQueue.isEmpty()){
            return null;
        }
        DialogueBox dialogueBox = dialogueQueue.peek();
        return dialogueBox.message;
    }

    /**
     * @return A list of options on the current dialogue box
     */
    public List<String> getOptions() {
        if (dialogueQueue.isEmpty()){
            return null;
        }
        DialogueBox dialogueBox = dialogueQueue.peek();
        return dialogueBox.options;
    }

    /**
     * @return The option that the player has currently selected
     */
    public int getSelectedOption() {
        if (dialogueQueue.isEmpty()){
            return -1;
        }
        DialogueBox dialogueBox = dialogueQueue.peek();
        return dialogueBox.selectedOption;
    }

    /**
     * Moves the player's selection down, and plays a sound
     * Also signifies to hudRenderer that the dialogue box should be redrawn
     */
    public void decreaseSelection() {
        DialogueBox dialogueBox = getDialogueBox();
        int option = getSelectedOption();
        dialogueBox.decreaseSelection();

        // Works if no option are available
        if (dialogueBox.options != null && option != getSelectedOption()) {
            update = true;
            soundController.playSound(Sounds.OPTIONSWITCH);
        }
    }

    /**
     * Moves the player's selection up, and plays a sound
     * Also signifies to hudRenderer that the dialogue box should be redrawn
     */
    public void increaseSelection() {
        DialogueBox dialogueBox = getDialogueBox();
        int option = getSelectedOption();
        dialogueBox.increaseSelection();

        if (dialogueBox.options != null && option != getSelectedOption()) {
            update = true;
            soundController.playSound(Sounds.OPTIONSWITCH);
        }
    }

    /**
     * Calls the lambda function associated with the dialogue box at the front of the queue if given one.
     * Also plays a confirmation/closing sound and advances the queue along.
     */
    public void submit() {
        if (dialogueQueue.isEmpty()) {
            throw new RuntimeException("There are no dialog boxes to submit");
        }

        DialogueBox dialogueBox = dialogueQueue.remove();
        dialogueBox.submit();
        update = true;
        // If no options are available, we just close
        if (dialogueBox.options == null) {
            soundController.playSound(Sounds.DIALOGUECLOSE);
        } else {
            // If there were options, play a confirm noise
            soundController.playSound(Sounds.CONFIRM);
        }
    }

    /**
     * @return The dialogue box at the front of the queue
     */
    private DialogueBox getDialogueBox() {
        if (dialogueQueue.isEmpty()) {
            throw new RuntimeException("There are no dialog boxes to get");
        }
        return dialogueQueue.peek();
    }

    /**
     * Call if the dialogue box has been constructed, so doesn't need to be reconstructed on the next frame
     */
    public void setUpdated() {
        update = false;
    }

    /**
     * @return True if the dialogue box should be reconstructed for the next frame
     */
    public boolean getUpdate() {
        return update;
    }

    /**
     * @return True if the dialogue queue is empty
     */
    public boolean queueEmpty() {
        return dialogueQueue.isEmpty();
    }
}
