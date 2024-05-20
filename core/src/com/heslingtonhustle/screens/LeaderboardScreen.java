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
import com.heslingtonhustle.sound.SoundController;
import com.heslingtonhustle.sound.Sounds;
import com.heslingtonhustle.state.LeaderboardManager;

/**
 * A screen to display a leaderboard of player's high scores
 */
public class LeaderboardScreen implements Screen {
    private final HeslingtonHustleGame game;
    private final Skin skin;
    private final SoundController soundController;
    private final Stage stage;
    private final Texture backgroundTexture;
    public static final String leaderboardBackgroundAsset = "Graphics/UI/Backgrounds/menu_background.jpg";


    /**
     * A screen to display a leaderboard window containing player scores as
     * well as a back button.
     * @param game An instance of the game class
     */
    public LeaderboardScreen(HeslingtonHustleGame game) {
        this.game = game;
        this.skin = game.skin;
        this.soundController = game.soundController;

        stage = new Stage(new FitViewport(game.width, game.height));
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(leaderboardBackgroundAsset);
        Image backgroundImage = new Image(backgroundTexture);
        stage.addActor(backgroundImage);

        drawLeaderBoard();

        // Back button
        TextButton backButton = new TextButton("Back", skin);
        backButton.setPosition(15, 15);
        backButton.setWidth(250);
        stage.addActor(backButton);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                soundController.playSound(Sounds.CONFIRM);
                game.switchScreen(AvailableScreens.MenuScreen, false);
            }
        });

    }


    /**
     * Method to draw the leaderboard window as well as a list of player scores
     */
    public void drawLeaderBoard() {
        // Leaderboard window
        Window leaderboard = new Window("", skin);
        leaderboard.setSize(500, 630);
        leaderboard.setPosition(
                (game.width - leaderboard.getWidth()) / 2,
                (game.height - leaderboard.getHeight()) / 2);
        stage.addActor(leaderboard);

        // Table for content
        Table table = new Table();
        leaderboard.add(table).prefHeight(leaderboard.getHeight()-50)
                .prefWidth(leaderboard.getWidth()-50);

        // Title
        table.add(new Label("Leaderboard", skin, "button")).top()
                .padBottom(20).padTop(10);
        table.row();

        // Table for scores
        Table scoresTable = new Table();
        table.add(scoresTable).prefWidth(leaderboard.getWidth()-50).expandY().top();

        // Add scores
        Array<String> scores = LeaderboardManager.getScores();

        for (String score : scores) {
            scoresTable.add(new Label(score, skin, "leaderboardscore"))
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
