package io.skloch.heshustle.tests;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.screens.MenuScreen;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(GdxTestRunner.class)
public class MenuScreenTests {
    @Test
    public void testMenuScreen() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(600,400);
        MenuScreen menuScreen = new MenuScreen(game);


        TextButton playGameButton = new TextButton("Start game", game.skin);
        assertEquals(6, menuScreen.getOptionsTable().getCells().size);
    }
}
