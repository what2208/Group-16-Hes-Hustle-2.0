package com.heslingtonhustle.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.heslingtonhustle.state.DialogueManager;
import com.heslingtonhustle.state.State;

import java.util.List;

/**
 * A class to render static information to the screen that the player needs
 * Displays the time and date, and the current energy level
 * Also displays a label when the player can interact with an event
 */
public class HudRenderer implements Disposable {
    private final State gameState;
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


    /**
     * Initalises images and labels to display to the screen
     * Also initalises the dialogue window to display dialogue from
     * dialogueManager
     *
     * @param gameState
     * @param textureAtlas
     * @param skin The loaded UI skin
     * @param width Width of the game window
     * @param height Height of the game window
     */
    public HudRenderer(State gameState, TextureAtlas textureAtlas, Skin skin, int width, int height){
        this.gameState = gameState;
        this.skin = skin;

        // Camera and viewport
        hudCamera = new OrthographicCamera();
        viewport = new FitViewport(width, height, hudCamera);

        // Stage for UI elements
        hudStage = new Stage(new FitViewport(width, height));

        // Store a reference to the dialogueManager to fetch dialogue
        dialogueManager = gameState.getDialogueManager();

        // Display time
        timeButton = new TextButton("10:00am", skin, "informational");
        timeButton.setWidth(200);
        timeButton.setPosition(width-timeButton.getWidth()-15, height-133);
        hudStage.addActor(timeButton);

        // Display day
        dayButton = new TextButton("Day 1", skin, "informational");
        dayButton.setWidth(200);
        dayButton.setPosition(25, height-133);
        hudStage.addActor(dayButton);

        // Label to tell the user they can interact with something
        interactLabel = new Label("E - Interact", skin, "interaction");
        interactLabel.setPosition((width - interactLabel.getWidth()) / 2 + 15, 200);
        hudStage.addActor(interactLabel);

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
     * Also displays the user's energy, and a prompt if they can itneract with
     * a building.
     * Displays a dialogue window if dialogueQueue is not empty
     */
    public void render(){
        hudStage.setViewport(viewport);
        hudCamera.update();
        // Update day and time labels
        updateLabels();

        updateEnergy();

        // Show an interaction label if player is near an interactable object
        if (gameState.isInteractionPossible()) {
            interactLabel.setVisible(true);
        } else {
            interactLabel.setVisible(false);
        }

        showDialogue();

        hudStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        hudStage.draw();
    }

    /**
     * Fetches the correct values for time and date from gameState
     * and updates the labels.
     */
    private void updateLabels() {
        dayButton.setText("Day " + gameState.getDay());
        timeButton.setText(gameState.getTime());

    }

    /**
     * Scales the energy bar to the correct level
     */
    private void updateEnergy() {
        energyBar.setScaleY(gameState.getEnergy() / 100f);
    }

    /**
     * Shows/hides the dialoueBox if there is dialogue in the queue
     * Also restructures the dialoguebox table contents if there is an update
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
        if (dialogueManager.getOptions() != null) {
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
}
