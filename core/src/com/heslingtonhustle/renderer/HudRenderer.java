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

public class HudRenderer implements Disposable {
    private final State gameState;

    private final OrthographicCamera hudCamera;
    private final FitViewport viewport;

    private final DialogueManager dialogueManager;

    private final TextureAtlas textureAtlas;
    private final SpriteBatch batch;
    private final Sprite interactSprite;
    private final TextureManager animationManager;


    private final Skin skin;
    private final Stage hudStage;
    private final Window dialogueWindow;
    private final Label dialogueText;
    private final Table optionTable;
    private final TextButton dayButton;
    private final TextButton timeButton;


    public HudRenderer(State gameState, TextureAtlas textureAtlas, Skin skin, int width, int height){
        this.gameState = gameState;
        this.skin = skin;

        // Camera and viewport
        hudCamera = new OrthographicCamera();
        viewport = new FitViewport(width, height, hudCamera);

        hudStage = new Stage(new FitViewport(width, height));

        dialogueManager = gameState.getDialogueManager();

        this.textureAtlas = textureAtlas;
        batch = new SpriteBatch();

        animationManager = new TextureManager();
        addAnimations();

        // Show time and day in top left
        Group infoGroup = new Group();
        infoGroup.setPosition(15, height-133);
        hudStage.addActor(infoGroup);

        // Display time
        timeButton = new TextButton("10:00am", skin, "informational");
        timeButton.setWidth(200);
        timeButton.setPosition(width-timeButton.getWidth()-30, 0);
        infoGroup.addActor(timeButton);

        // Display day
        dayButton = new TextButton("Day 1", skin, "informational");
        dayButton.setWidth(200);
        dayButton.setPosition(10, 0);
        infoGroup.addActor(dayButton);

        interactSprite = new Sprite();
        interactSprite.setSize(128, 34);
        interactSprite.setRegion(animationManager.retrieveTexture("interact"));

        // Dialogue box window
        dialogueWindow = new Window("", skin, "dialog");
        dialogueWindow.setSize(900, 200);
        dialogueWindow.setPosition((width - dialogueWindow.getWidth())/2, 20);

        Table dialogueTable = new Table();
        dialogueTable.setSize(860, 160);
        dialogueTable.setPosition(20, 20);
        dialogueWindow.addActor(dialogueTable);

        // Text
        dialogueText = new Label("", skin, "dialoguesmall");
        dialogueText.setWrap(true);
        dialogueTable.add(dialogueText).expandX().expandY().left().top().prefWidth(860);
        dialogueTable.row();

        // Options
        optionTable = new Table();
        dialogueTable.add(optionTable).left().bottom();

        hudStage.addActor(dialogueWindow);

        dialogueWindow.setVisible(false);





    }

    public void render(){
        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);

        setInteractTexture();
        updateLabels();

        // Instead of using a spritebatch, just use a stage
        hudStage.setViewport(viewport);
        hudStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        hudStage.draw();

        batch.begin();
        if (gameState.isInteractionPossible()) {
            interactSprite.draw(batch);
        }
        showDialogue();
        batch.end();
    }

    private void updateLabels() {
        dayButton.setText("Day " + gameState.getDay());
        timeButton.setText(gameState.getTime());

    }

    private void setInteractTexture() {
        interactSprite.setRegion(animationManager.retrieveTexture("interact"));
    }

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

    private void addAnimations() {
        TextureRegion[] clockAnimationFrames = new TextureRegion[2];
//        clockAnimationFrames[0] = clockTexture = textureAtlas.findRegion("clock-night");
//        clockAnimationFrames[1] = clockTexture = textureAtlas.findRegion("clock-red");
//        animationManager.addAnimation("clock-night", clockAnimationFrames, 0.4f);

        Array<TextureAtlas.AtlasRegion> interact = textureAtlas.findRegions("interact");
        animationManager.addAnimation("interact", interact, 0.4f);
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudStage.getViewport().update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
