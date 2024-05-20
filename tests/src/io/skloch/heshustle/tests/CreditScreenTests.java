package io.skloch.heshustle.tests;

import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.screens.CreditScreen;
import com.heslingtonhustle.screens.OptionsScreen;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CreditScreenTests {
    @Test
    public void testResize() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(600,400);
        CreditScreen creditScreen = new CreditScreen(game);
        int newWidth = 650;
        int newHeight = 450;

        creditScreen.resize(newWidth, newHeight);

        assertEquals(newWidth, creditScreen.getViewport().getScreenWidth());
        assertEquals(newHeight, creditScreen.getViewport().getScreenHeight());
    }
}
