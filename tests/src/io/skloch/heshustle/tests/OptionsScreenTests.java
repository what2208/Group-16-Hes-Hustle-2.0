package io.skloch.heshustle.tests;

import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.screens.OptionsScreen;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(GdxTestRunner.class)
public class OptionsScreenTests {
    @Test
    public void testResize() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(600,400);
        game.create();
        OptionsScreen optionsScreen = new OptionsScreen(game);
        int newWidth = 650;
        int newHeight = 450;

        optionsScreen.resize(newWidth, newHeight);

        assertEquals(newWidth, optionsScreen.getViewport().getScreenWidth());
        assertEquals(newHeight, optionsScreen.getViewport().getScreenHeight());
    }
}
