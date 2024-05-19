package io.skloch.heshustle.tests;

import com.badlogic.gdx.maps.MapProperties;
import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.screens.PlayScreen;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PlayScreenTests {
    @Test
    public void testPlayScreenPause() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(600,400);
        String player = "player-0";
        PlayScreen playScreen = new PlayScreen(game, player);

        playScreen.pause();

        assertTrue(playScreen.isPaused());
        assertTrue(playScreen.getPauseMenu().isVisible());
    }

    @Test
    public void testPlayScreenUnpause() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(600,400);
        String player = "player-0";
        PlayScreen playScreen = new PlayScreen(game, player);

        playScreen.pause();
        playScreen.unPause();

        assertFalse(playScreen.isPaused());
        assertFalse(playScreen.getPauseMenu().isVisible());
    }
}
