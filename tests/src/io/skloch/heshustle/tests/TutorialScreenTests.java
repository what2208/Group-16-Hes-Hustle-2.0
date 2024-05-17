package io.skloch.heshustle.tests;

import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.screens.TutorialScreen;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TutorialScreenTests {
    @Test
    public void testResize() {
        HeslingtonHustleGame heslingtonHustleGame = new HeslingtonHustleGame(500,400);
        TutorialScreen tutorialScreen = new TutorialScreen(heslingtonHustleGame);
        int newWidth = 400;
        int newHeight = 350;

        tutorialScreen.resize(newWidth,newHeight);

        //assertEquals(newWidth, tutorialScreen.getViewport().getScreenX());
    }
}
//public void resize(int width, int height){
//        tutStage.getViewport().update(width, height);
//        viewport.update(width, height);
//    }