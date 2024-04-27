package com.heslingtonhustle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.sound.Sounds;
import com.heslingtonhustle.state.Activity;

import java.util.HashMap;

/**
 * A screen to display the user's score, along with any secret achievements
 * or debuffs they encountered.
 */
public class GameOverScreen implements Screen {
    private final HeslingtonHustleGame heslingtonHustleGame;
    private final Stage stage;
    private Table optionsTable;
    private final Texture backgroundTexture;
    private Texture pageTexture;
    private HashMap<String, Activity> activities;
    private int[] stats;


    /**
     * Creates the screen and fills in all the user's score information
     * @param parentClass
     */
    public GameOverScreen(HeslingtonHustleGame parentClass, HashMap<String, Activity> activities, int[] stats) {
        heslingtonHustleGame = parentClass;
        this.activities = activities;
        this.stats = stats;

        // UI Stage
        stage = new Stage(new FitViewport(parentClass.WIDTH, parentClass.HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Background table image
        backgroundTexture = new Texture("Graphics/UI/Backgrounds/table_background.jpg");
        Image backgroundImage = new Image(backgroundTexture);
        stage.addActor(backgroundImage);

        setupGUI(parentClass);

    }


    private void setupGUI(HeslingtonHustleGame parentClass) {

        // Page texture
        pageTexture = new Texture("Graphics/UI/Gameover/page.png");
        Image pageImage = new Image(pageTexture);
        pageImage.setPosition(
                (parentClass.WIDTH - pageImage.getWidth()) / 2,
                (parentClass.HEIGHT - pageImage.getHeight()) / 2);
        stage.addActor(pageImage);

        // Game over text
        Label gameOver = new Label("Game Over!", parentClass.skin, "handwriting80px");
        gameOver.setPosition((parentClass.WIDTH - gameOver.getWidth()) / 2 - 20, parentClass.HEIGHT-150);
        stage.addActor(gameOver);

        // Get game stats
        int[] gameInfo = new int[]{
                stats[0],
                activities.get("study").getCount(),
                activities.get("recreation").getCount(),
                activities.get("eat").getCount(),
                stats[1],
                stats[2]
        };

        // Lines
        String[] lines = new String[]{
                "Hours slept: ",
                "Hours studied: ",
                "Hours of fun: ",
                "Times ate: ",
                "Achievements: ",
                "Penalties: "
        };

        // Draw lines of text
        for (int i = 0; i < gameInfo.length; i++) {
            // After the 4th line add a break
            int j = (i >= 4) ? 1 : 0;
            Label line = new Label(lines[i] + gameInfo[i], parentClass.skin, "handwriting48px");

            line.setPosition(parentClass.WIDTH / 2 - 183, parentClass.HEIGHT - ((i+j)*48 + 239));
            stage.addActor(line);
        }

        // Score text
        Label scoreText = new Label("Score: ", parentClass.skin, "handwriting64px");
        scoreText.setPosition((parentClass.WIDTH - gameOver.getWidth()) / 2 - 20, 93);
        stage.addActor(scoreText);


        // Continue Button
        TextButton continueButton = new TextButton("Continue", parentClass.skin);
        continueButton.setPosition(parentClass.WIDTH - 315, 15);
        continueButton.setWidth(300);
        stage.addActor(continueButton);
        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                parentClass.soundController.playSound(Sounds.CONFIRM);
                heslingtonHustleGame.changeScreen(AvailableScreens.MenuScreen);
            }
        });



    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
    }
}
