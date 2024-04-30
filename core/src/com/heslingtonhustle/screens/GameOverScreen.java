package com.heslingtonhustle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.sound.Sounds;
import com.heslingtonhustle.state.Achievement;
import com.heslingtonhustle.state.Activity;
import com.heslingtonhustle.state.LeaderboardManager;
import sun.awt.image.ImageWatched;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 * A screen to display the user's score, along with any secret achievements
 * or debuffs they encountered.
 */
public class GameOverScreen implements Screen {
    private final HeslingtonHustleGame parentClass;
    // UI Elements
    private final Stage stage;
    private Table optionsTable;
    private final Texture backgroundTexture;
    private Texture pageTexture;
    private Window queryWindow, nameEntryWindow;

    // Player game data
    private HashMap<String, Activity> activities;
    private boolean stepAchievement;
    private HashSet<Achievement> achievements = new HashSet<>();
    private int numAchievements = 0;

    // The scores for each category
    private HashMap<String, Integer> categoryScores = new HashMap<String, Integer>();
    // The number of times an event in each category was completed
    private HashMap<String, Integer> categoryHours = new HashMap<String, Integer>();


    /**
     * Creates the screen and fills in all the user's score information
     * @param parentClass
     */
    public GameOverScreen(HeslingtonHustleGame parentClass, HashMap<String, Activity> activities, boolean stepAchievement) {
        this.parentClass = parentClass;
        this.activities = activities;
        this.stepAchievement = stepAchievement;

        // UI Stage
        stage = new Stage(new FitViewport(parentClass.WIDTH, parentClass.HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Background table image
        backgroundTexture = new Texture("Graphics/UI/Backgrounds/table_background.jpg");
        Image backgroundImage = new Image(backgroundTexture);
        stage.addActor(backgroundImage);

        // Figure out which achievements/penalties the player has got
        populateCategoryInformation();
        getAchievements();
        getPenalties();

        // Show the player's score breakdown
        drawScorePaper();
        // Draw banners for penalties and achievements
        drawBanners();

        // Popup windows
        queryWindow = leaderBoardQueryScreen();
        stage.addActor(queryWindow);

        nameEntryWindow = enterNameWindow();
        stage.addActor(nameEntryWindow);

    }

    /**
     * Calculates the final score the player got
     * @return The player's score
     */
    private int calcScore() {
        int score = 0;

        // Add score for activities
        for (Activity activity : activities.values()) {
            score += activity.getScore() * activity.getTimesCompleted();
        }

        // Add/minus penalties and achievements
        for (Achievement achievement : achievements) {
            score += achievement.getScore();
        }

//        if (score < 0) {
//            return 0;
//        }
        return score;
    }

    /**
     * Populates the HashMaps for the amount of score gained from
     * each category and the number of times an event in each category
     * was completed.
     */
    private void populateCategoryInformation() {
        categoryScores.put("sleep", 0);
        categoryScores.put("study", 0);
        categoryScores.put("recreation", 0);
        categoryScores.put("eat", 0);

        categoryHours.put("sleep", 0);
        categoryHours.put("study", 0);
        categoryHours.put("recreation", 0);
        categoryHours.put("eat", 0);

        // For each activity
        for (Activity activity : activities.values()) {
            // Find the score for this type, and add this activity's score
            int score = categoryScores.get(activity.getScoreType());
            categoryScores.put(activity.getScoreType(), score + activity.getScore() * activity.getTimesCompleted());

            // Same with the number of times completed
            // For eating just add times done
            if (activity.getScoreType() == "eat") {
                int times = categoryHours.get(activity.getScoreType());
                categoryHours.put("eat", times + activity.getTimesCompleted());
            } else {
                int hours = categoryHours.get(activity.getScoreType());
                categoryHours.put(activity.getScoreType(), hours + activity.getHoursSpent());
            }
        }

    }


    /**
     * Formats and draws the player's score breakdown in the form
     * of a report card
     */
    private void drawScorePaper() {
        // Background page
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


        // The individual score lines to write to the paper
        Array<String> scoreLines = new Array<String>();
        scoreLines.add("Hours slept: " + categoryHours.get("sleep"));
        scoreLines.add("Hours studied: " + categoryHours.get("study"));
        scoreLines.add("Hours of fun: " + categoryHours.get("recreation"));
        scoreLines.add("Times ate: " + categoryHours.get("eat"));
        scoreLines.add("Achievements: " + numAchievements);
        scoreLines.add("Penalties: " + (achievements.size() - numAchievements));

        // Draw lines of text
        for (int i = 0; i < scoreLines.size; i++) {
            // After the 4th line add a break
            int j = (i >= 4) ? 1 : 0;
            Label line = new Label(scoreLines.get(i), parentClass.skin, "handwriting48px");

            line.setPosition(parentClass.WIDTH / 2 - 183, parentClass.HEIGHT - ((i+j)*48 + 239));
            stage.addActor(line);
        }


        // Final score text
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
                if (!queryWindow.isVisible() && !nameEntryWindow.isVisible()) {
                    parentClass.soundController.playSound(Sounds.CONFIRM);
                    queryWindow.setVisible(true);
                }
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
            Label score = new Label(Integer.toString(achievement.getScore()), parentClass.skin, "achievementscore");

            if (achievement.isPositive()) {
                // Achievement
                achievementTable.add(banner).padBottom(20).prefWidth(300);
                achievementTable.row();
                achievementTable.add(description).padBottom(10);
                achievementTable.row();
                achievementTable.add(score);
                achievementTable.row();
            } else {
                // Penalty
                banner.setDisabled(true);
                penaltyTable.add(banner).padBottom(20).prefWidth(300);
                penaltyTable.row();
                penaltyTable.add(description).padBottom(10);
                penaltyTable.row();
                penaltyTable.add(score);
                penaltyTable.row();
            }

        }

        // Add both tables to the stage
        achievementTable.setWidth(300);
        achievementTable.setPosition(parentClass.WIDTH-350, (parentClass.HEIGHT - achievementTable.getHeight()) / 2 + 30);
        stage.addActor(achievementTable);

        penaltyTable.setWidth(300);
        penaltyTable.setPosition(30, (parentClass.HEIGHT - achievementTable.getHeight()) / 2 + 30);
        stage.addActor(penaltyTable);


    }

    /**
     * A simple popup window that asks the user if they would like to
     * submit their score to the leaderboard
     * @return
     */
    private Window leaderBoardQueryScreen() {
        Window popupWindow = new Window("", parentClass.skin, "popup");
        popupWindow.setSize(480, 280);
        popupWindow.setPosition(
                (parentClass.WIDTH - popupWindow.getWidth()) / 2,
                (parentClass.HEIGHT - popupWindow.getHeight()) / 2
        );

        // Table for elements
        Table popupTable = new Table();
        popupWindow.add(popupTable).prefSize(popupWindow.getWidth(), popupTable.getHeight());

        // Question
        Label question = new Label("Submit your score to the leaderboard?", parentClass.skin, "leaderboardscore");
        question.setWrap(true);
        question.setAlignment(1);
        popupTable.add(question).prefWidth(popupWindow.getWidth() * 0.9f).colspan(2).padTop(10);
        popupTable.row().padTop(20);

        // Two buttons
        TextButton yesButton = new TextButton("Yes", parentClass.skin);
        TextButton noButton = new TextButton("No", parentClass.skin);

        popupTable.add(yesButton).prefWidth(120);
        popupTable.add(noButton).prefWidth(120);

        noButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                parentClass.soundController.playSound(Sounds.CONFIRM);
                parentClass.changeScreen(AvailableScreens.MenuScreen);
            }
        });

        yesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                parentClass.soundController.playSound(Sounds.CONFIRM);
                queryWindow.setVisible(false);
                nameEntryWindow.setVisible(true);
            }
        });

        popupWindow.setVisible(false);
        return popupWindow;

    }

    /**
     * A window to ask the player for their name
     * @return
     */
    private Window enterNameWindow() {
        Window popupWindow = new Window("", parentClass.skin, "popup");
        popupWindow.setSize(480, 280);
        popupWindow.setPosition(
                (parentClass.WIDTH - popupWindow.getWidth()) / 2,
                (parentClass.HEIGHT - popupWindow.getHeight()) / 2
        );

        // Table for elements
        Table popupTable = new Table();
        popupWindow.add(popupTable).prefSize(popupWindow.getWidth(), popupTable.getHeight());

        // Question
        Label question = new Label("Enter name:", parentClass.skin, "leaderboardscore");
        question.setWrap(true);
        question.setAlignment(1);
        popupTable.add(question).prefWidth(popupWindow.getWidth() * 0.9f).colspan(2).padTop(20);
        popupTable.row();

        // Text entry field
        TextField nameField = new TextField("", parentClass.skin);
        nameField.setMaxLength(15);
        popupTable.add(nameField).prefWidth(300).padBottom(15);
        popupTable.row();

        // Submit button
        TextButton submitButton = new TextButton("Submit", parentClass.skin);

        popupTable.add(submitButton).prefWidth(200).padBottom(20);
        popupTable.row();

        Label errorText = new Label("Name must be non-blank and alphanumerical!", parentClass.skin, "smallerror");

        submitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                parentClass.soundController.playSound(Sounds.CONFIRM);
                // Check input
                String name = nameField.getText();
                if (LeaderboardManager.isValidName(name)) {
                    // If valid, write this score
                    LeaderboardManager.writeScore(name, calcScore());
                    parentClass.changeScreen(AvailableScreens.MenuScreen);
                } else {
                    // Show an error message
                    errorText.setVisible(true);
                }
            }
        });

        // Error text
        popupTable.add(errorText);
        errorText.setVisible(false);


        popupWindow.setVisible(false);
        return popupWindow;
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
        if (stepAchievement) {
            achievements.add(new Achievement(
                    "Walker",
                    "Walk 200 steps each day",
                    200
            ));
            numAchievements += 1;
        }

        // Give if the player went clubbing at least once
        if (activities.containsKey("club")) {
            achievements.add(new Achievement(
                    "Clubber",
                    "Go clubbing at least once",
                    500
            ));
            numAchievements += 1;
        }

        // Give if the player fed the ducks at least six times
        if (activities.containsKey("ducks")) {
            if (activities.get("ducks").getTimesCompleted() >= 6) {
                achievements.add(new Achievement(
                        "Duck duck go",
                        "Feed the ducks 6 times",
                        300
                ));
                numAchievements += 1;
            }
        }

    }

    /**
     * Adds negative achievements, or 'penalties' if the player has not eaten
     * or studied enough etc.
     * If the player hasn't done a type of activity, they get given a
     * 'super' penalty, which takes off even more points.
     */
    private void getPenalties() {
        // Player should ideally get between 50-60 hours of sleep each week
        if (categoryHours.get("sleep") < 50 ) {
            achievements.add(new Achievement(
                    "Zombie",
                    "You didn't get enough sleep!",
                    -500
            ));
        }

        // 2 hours of study each day
        if (categoryHours.get("study") < 14) {
            achievements.add(new Achievement(
                    "Clueless",
                    "You didn't study enough!",
                    -1000
            ));
        }

        // Eat 3 meals a day
        if (categoryHours.get("eat") < 21) {
            achievements.add(new Achievement(
                    "Hungry",
                    "You didn't eat well enough!",
                    -500
            ));
        }

        // Two hours of fun each day
        if (categoryHours.get("recreation") < 14) {
            achievements.add(new Achievement(
                    "Party Pooper",
                    "You didn't have enough fun!",
                    -500
            ));
        }

    }



}
