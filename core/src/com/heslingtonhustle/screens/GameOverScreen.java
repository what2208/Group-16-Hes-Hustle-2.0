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
import com.heslingtonhustle.state.Achievement;
import com.heslingtonhustle.state.Activity;

import java.util.HashMap;
import java.util.HashSet;

/**
 * A screen to display the user's score, along with any secret achievements
 * or debuffs they encountered.
 */
public class GameOverScreen implements Screen {
    private final Stage stage;
    private Table optionsTable;
    private final Texture backgroundTexture;
    private Texture pageTexture;
    private HashMap<String, Activity> activities;
    private int[] stats;


    private HashSet<Achievement> achievements = new HashSet<>();
    private int numAchievements = 0;
    private final HeslingtonHustleGame parentClass;


    /**
     * Creates the screen and fills in all the user's score information
     * @param parentClass
     */
    public GameOverScreen(HeslingtonHustleGame parentClass, HashMap<String, Activity> activities, int[] stats) {
        this.parentClass = parentClass;
        this.activities = activities;
        this.stats = stats;

        // UI Stage
        stage = new Stage(new FitViewport(parentClass.WIDTH, parentClass.HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Background table image
        backgroundTexture = new Texture("Graphics/UI/Backgrounds/table_background.jpg");
        Image backgroundImage = new Image(backgroundTexture);
        stage.addActor(backgroundImage);

        // Figure out which achievements/penalties the player has got
        getAchievements();
        getPenalties();

        // Show the player's score breakdown
        drawScorePaper();
        // Draw banners for penalties and achievements
        drawBanners();

    }

    /**
     * Calculates the final score the player got
     * @return The player's score
     * TODO: Change
     */
    private int calcScore() {
        return 1400;
    }


    private void drawScorePaper() {

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
                numAchievements,
                achievements.size() - numAchievements
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
        Label scoreText = new Label("Score: " + calcScore(), parentClass.skin, "handwriting64px");
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
                parentClass.changeScreen(AvailableScreens.MenuScreen);
            }
        });
    }

    /**
     * Draws banners and explanations for the secret achievements the player
     * reached
     */
    private void drawBanners() {
        Table achievementTable = new Table();
        Table penaltyTable = new Table();

        for (Achievement achievement : achievements) {
            TextButton banner = new TextButton(achievement.getTitle(), parentClass.skin, "banner");
            Label description = new Label(achievement.getDescription(), parentClass.skin, "achievementlabel");

            if (achievement.isPositive()) {
                // Achievement
                achievementTable.add(banner).padBottom(20).prefWidth(300);
                achievementTable.row();
                achievementTable.add(description).padBottom(20);
                achievementTable.row();
            } else {
                // Penalty
                banner.setDisabled(true);
                penaltyTable.add(banner).padBottom(20).prefWidth(300);
                penaltyTable.row();
                penaltyTable.add(description).padBottom(20);
                penaltyTable.row();
            }

        }

        // Add both tables to the stage
        achievementTable.setWidth(300);
        achievementTable.setPosition(parentClass.WIDTH-350, (parentClass.HEIGHT - achievementTable.getHeight()) / 2 + 30);
        stage.addActor(achievementTable);

        penaltyTable.setWidth(300);
        penaltyTable.setPosition(60, (parentClass.HEIGHT - achievementTable.getHeight()) / 2 + 30);
        stage.addActor(penaltyTable);


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


    /**
     * Determines the secret achievements the player has achieved
     * based on the activities completed.
     * Adds these to the set of achievements
     * TODO: Make these conditional
     */
    private void getAchievements() {
        achievements.add(new Achievement(
                "Walker",
                "Walk 200 steps each day",
                200
        ));

        achievements.add(new Achievement(
                "Clubber",
                "Go clubbing at least once",
                500
        ));

        achievements.add(new Achievement(
                "Duck duck go",
                "Feed the ducks 6 times",
                300
        ));

        numAchievements += 3;

    }

    /**
     * Adds negative achievements, or 'penalties' if the player has not eaten
     * or studied enough etc.
     * TODO: Make these conditional
     */
    private void getPenalties() {
        achievements.add(new Achievement(
                "Zombie",
                "You didn't get enough sleep!",
                -500
        ));

        achievements.add(new Achievement(
                "Clueless",
                "You didn't study enough!",
                -1000
        ));

        achievements.add(new Achievement(
                "Hungry",
                "You didn't eat well enough!",
                -500
        ));

        achievements.add(new Achievement(
                "Party Pooper",
                "You didn't have enough fun!",
                -200
        ));
    }



}
