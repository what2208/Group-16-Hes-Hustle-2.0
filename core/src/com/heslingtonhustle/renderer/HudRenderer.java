package com.heslingtonhustle.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.heslingtonhustle.state.DialogueManager;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.List;

/**
 * A class to render static information to the screen that the player needs
 * Displays the time and date, and the current energy level
 * Also displays a label when the player can interact with an event.
 */
public class HudRenderer implements Disposable {
    private final OrthographicCamera hudCamera;
    private final FitViewport viewport;
    private final DialogueManager dialogueManager;
    private final Skin skin;
    private final Stage hudStage;
    private final Window dialogueWindow;
    private final Label dialogueText;
    private final Table optionTable;
    private final TextButton dayButton;
    private final TextButton timeButton;
    private final Label interactLabel;
    private final Image energyBar;
    private final Image blackScreen;

    /**
     * Initialises images and labels to display to the screen.
     * Also initialises the dialogue window to display dialogue from
     * dialogueManager.
     *
     * @param dialogueManager The current dialogue manager
     * @param skin The loaded UI skin
     * @param width Width of the game window
     * @param height Height of the game window
     */
    public HudRenderer(Skin skin, DialogueManager dialogueManager, int width, int height) {
        this.skin = skin;
        this.dialogueManager = dialogueManager;

        // Camera and viewport
        hudCamera = new OrthographicCamera();
        viewport = new FitViewport(width, height, hudCamera);

        // Stage for UI elements
        hudStage = new Stage(new FitViewport(width, height));

        // Black screen
        blackScreen = new Image(new Texture(Gdx.files.internal("Graphics/black.png")));
        blackScreen.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        blackScreen.addAction(Actions.alpha(0f));
        hudStage.addActor(blackScreen);

        // Display time
        timeButton = new TextButton("10:00am", skin, "informational");
        timeButton.setWidth(200);
        timeButton.setPosition(width - timeButton.getWidth() - 15, height - 133);
        hudStage.addActor(timeButton);

        // Display day
        dayButton = new TextButton("Day 1", skin, "informational");
        dayButton.setWidth(200);
        dayButton.setPosition(25, height - 133);
        hudStage.addActor(dayButton);

        // Label to tell the user they can interact with something
        Table interactionTable = new Table();
        hudStage.addActor(interactionTable);
        interactionTable.setFillParent(true);
        interactLabel = new Label("E - Interact", skin, "interaction");
        interactionTable.add(interactLabel).expand().padTop(250);

        // Energy bar
        energyBar = new Image(skin, "energy_bar");
        energyBar.setPosition(27, 27);
        hudStage.addActor(energyBar);

        // Energy Bar Outline
        Image energyBarOutline = new Image(skin, "energy_bar_outline");
        energyBarOutline.setPosition(15, 15);
        hudStage.addActor(energyBarOutline);

        // Dialogue box window
        dialogueWindow = new Window("", skin, "dialog");
        dialogueWindow.setSize(900, 200);
        dialogueWindow.setPosition((width - dialogueWindow.getWidth())/2, 20);
        hudStage.addActor(dialogueWindow);
        dialogueWindow.setVisible(false);

        // Dialogue box table
        Table dialogueTable = new Table();
        dialogueTable.setSize(860, 160);
        dialogueTable.setPosition(20, 20);
        dialogueWindow.addActor(dialogueTable);

        // Dialogue text
        dialogueText = new Label("", skin, "dialoguesmall");
        dialogueText.setWrap(true);
        dialogueTable.add(dialogueText).expandX().expandY().left().top().prefWidth(860);
        dialogueTable.row();

        // Dialogue options
        optionTable = new Table();
        dialogueTable.add(optionTable).left().bottom();
    }

    /**
     * Updates and renders the time and date to the screen
     * Also displays the user's energy, and a prompt if they can interact with
     * a building.
     * Displays a dialogue window if dialogueQueue is not empty
     */
    public void render(MapProperties nearestTrigger){
        hudStage.setViewport(viewport);
        hudCamera.update();

        // Show an interaction label if player is near an interactable object
        if (nearestTrigger == null) {
            interactLabel.setVisible(false);
        } else if (nearestTrigger.containsKey("sign")) {
            interactLabel.setVisible(true);
            interactLabel.setText("E - Read");
        } else if (nearestTrigger.containsKey("sleep")) {
            interactLabel.setVisible(true);
            interactLabel.setText("E - Sleep");
        } else if (nearestTrigger.containsKey("dialogue")) {
            interactLabel.setVisible(true);
            interactLabel.setText("E - Talk");
        } else {
            interactLabel.setVisible(true);
            interactLabel.setText("E - Interact");
        }

        showDialogue();

        hudStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        hudStage.draw();
    }


    /**
     * Updates hud's time, date and energy labels
     * @param time The time represented as a string
     * @param day The day number to display
     * @param energy The amount of energy the player has left, between 0 and 100
     */
    public void updateValues(String time, int day, int energy) {
        timeButton.setText(time);
        dayButton.setText("Day " + day);

        energyBar.setScaleY(energy / 100f);
    }


    /**
     * Fades the black screen in
     */
    public void fadeIn(float duration) {
        blackScreen.addAction(Actions.fadeIn(duration));
    }

    /**
     * Fades the screen in from black
     */
    public void fadeOut(float duration) {
        blackScreen.addAction(Actions.fadeOut(duration));
    }

    /**
     * @return Returns true if the screen is black
     */
    public Boolean screenIsBlack() {
        Color colour = blackScreen.getColor();
        return colour.a == 1f;
    }

    /**
     * @return True if a black screen is not being faded in or out
     */
    public Boolean screenClear() {
        Color colour = blackScreen.getColor();
        return colour.a == 0f;
    }



    /**
     * Shows/hides the dialogueBox if there is dialogue in the queue
     * Also restructures the dialogueBox table contents if there is an update
     */
    private void showDialogue() {
        if (dialogueManager.isEmpty()) {
            // No dialogue box to show
            dialogueWindow.setVisible(false);
            return;
        } else {
            dialogueWindow.setVisible(true);
        }

        if (dialogueManager.getUpdate()) {
            reconstructDialogueBox();
            dialogueManager.setUpdated();
        }
    }

    /**
     * Builds a dialogue box using scene2d elements
     * Displays the main dialogue text, a list of options and a pointer if
     * necessary.
     * Called when text is changed or an option pointer is moved.
     */
    public void reconstructDialogueBox() {
        // Check dialogue details
        String message = dialogueManager.getMessage();
        List<String> options = dialogueManager.getOptions();
        int selectedOption = dialogueManager.getSelectedOption();

        dialogueText.setText(message);
        optionTable.clearChildren();

        // Only draw if the player has options to choose from
        if (options != null) {
            // Add player's options to the options table
            for (int i = 0; i < options.size(); i++) {
                Label pointer = new Label(">>", skin, "dialoguesmall");
                // Hide pointer if not selected
                if (selectedOption != i) pointer.setVisible(false);
                Label option = new Label(options.get(i), skin, "dialoguesmall");

                optionTable.add(pointer).left().padRight(10);
                optionTable.add(option).left();
                optionTable.row();
            }
        }


    }

    /**
     * Corrects resizes the screen elements
     * @param width New width of the game window
     * @param height New height of the game window
     */
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudStage.getViewport().update(width, height);
    }

    /**
     * Correctly disposes of any elements
     */
    @Override
    public void dispose() {

    }

    /**
     * @return The day button (for testing)
     */
    public TextButton getDayButton() { return dayButton; }

    /**
     * @return The viewport (for testing)
     */
    public FitViewport getViewport() { return viewport; }

    /**
     * @return The time button (for testing)
     */
    public TextButton getTimeButton() { return timeButton; }

    /**
     * @return The energy bar (for testing)
     */
    public Image getEnergyBar() { return energyBar; }
}
