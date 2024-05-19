package io.skloch.heshustle.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.screens.PauseMenu;
import com.heslingtonhustle.screens.TutorialScreen;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PauseMenuTests {
    @Test
    public void testPauseMenu() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(600, 400);
        game.create();
        Screen parentClass = new TutorialScreen(game);
        PauseMenu pauseMenu = new PauseMenu(parentClass, game);
        Stage newStage = new Stage(new FitViewport(600, 400));

        assertEquals(newStage, Gdx.input.getInputProcessor());
    }
}