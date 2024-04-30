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
import com.heslingtonhustle.state.LeaderboardManager;

/**
 * A screen to display a leaderboard of player's high scores
 */
public class LeaderboardScreen implements Screen {
    private final boolean DEBUG = false;
    private final HeslingtonHustleGame parentClass;
    private final Stage stage;
    private final Texture backgroundTexture;


    /**
     * A screen to display a leaderboard window containing player scores as
     * well as a back button.
     * @param parentClass
     */
    public LeaderboardScreen(HeslingtonHustleGame parentClass) {
        this.parentClass = parentClass;
        stage = new Stage(new FitViewport(parentClass.WIDTH, parentClass.HEIGHT));
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture("Graphics/UI/Backgrounds/menu_background.jpg");
        Image backgroundImage = new Image(backgroundTexture);
        stage.addActor(backgroundImage);

        drawLeaderBoard();

        // Back button
        TextButton backButton = new TextButton("Back", parentClass.skin);
        backButton.setPosition(15, 15);
        backButton.setWidth(250);
        stage.addActor(backButton);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                parentClass.soundController.playSound(Sounds.CONFIRM);
                parentClass.changeScreen(AvailableScreens.MenuScreen);
            }
        });

    }


    /**
     * Method to draw the leaderboard window as well as a list of player scores
     */
    public void drawLeaderBoard() {
        // Leaderboard window
        Window leaderboard = new Window("", parentClass.skin);
        leaderboard.setSize(500, 630);
        leaderboard.setPosition(
                (parentClass.WIDTH - leaderboard.getWidth()) / 2,
                (parentClass.HEIGHT - leaderboard.getHeight()) / 2);
        stage.addActor(leaderboard);

        // Table for content
        Table table = new Table();
        leaderboard.add(table).prefHeight(leaderboard.getHeight()-50)
                .prefWidth(leaderboard.getWidth()-50);

        // Title
        table.add(new Label("Leaderboard", parentClass.skin, "button")).top()
                .padBottom(20).padTop(10);
        table.row();

        // Table for scores
        Table scoresTable = new Table();
        table.add(scoresTable).prefWidth(leaderboard.getWidth()-50).expandY().top();

        // Add scores
        Array<String> scores = LeaderboardManager.getScores();

        for (String score : scores) {
            scoresTable.add(new Label(score, parentClass.skin, "leaderboardscore"))
                    .padTop(25);
            scoresTable.row();
        }
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
