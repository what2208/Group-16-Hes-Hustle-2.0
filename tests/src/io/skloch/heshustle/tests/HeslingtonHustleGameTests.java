package io.skloch.heshustle.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.screens.AvailableScreens;
import com.heslingtonhustle.screens.GameOverScreen;
import com.heslingtonhustle.screens.MenuScreen;
import com.heslingtonhustle.state.Activity;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(GdxTestRunner.class)
public class HeslingtonHustleGameTests {
    @Test
    public void testCreate() {
        HeslingtonHustleGame heslingtonHustleGame = new HeslingtonHustleGame(300, 150);
        Screen newMenuScreen = new MenuScreen(heslingtonHustleGame);
        Music newMusic = Gdx.audio.newMusic(Gdx.files.internal("Sound/Music/menuMusic.mp3"));

        heslingtonHustleGame.create();

        assertEquals(Application.LOG_DEBUG, Gdx.app.getLogLevel());
        assertNull(heslingtonHustleGame.getPreviousScreen());
        assertEquals(newMenuScreen, heslingtonHustleGame.getCurrentScreen());
        assertEquals(newMusic, heslingtonHustleGame.soundController.getCurrentMusic());
    }

    @Test
    public void testGameOver() {
        HeslingtonHustleGame heslingtonHustleGame = new HeslingtonHustleGame(300, 150);
        HashMap<String, Activity> activitiesCompleted = new HashMap<>();
        activitiesCompleted.put("sleep", new Activity("sleep", "sleep", 5, 0, 8, -1));
        boolean stepAchievement = true;
        Screen newScreen = new GameOverScreen(heslingtonHustleGame, activitiesCompleted, stepAchievement);

        heslingtonHustleGame.gameOver(activitiesCompleted, stepAchievement);

        assertEquals(newScreen, heslingtonHustleGame.getCurrentScreen());
    }

}