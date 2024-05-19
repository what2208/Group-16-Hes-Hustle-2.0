package io.skloch.heshustle.tests;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.heslingtonhustle.renderer.HudRenderer;
import com.heslingtonhustle.sound.SoundController;
import com.heslingtonhustle.state.DialogueManager;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(GdxTestRunner.class)
public class HudRendererTests {
    @Test
    public void testHudRenderer() {
        Skin skin = new Skin();
        Object object = new Object();
        skin.add("informational", object);
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);
        int width = 600;
        int height = 350;
        HudRenderer hudRenderer = new HudRenderer(skin, dialogueManager, width, height);

        assertEquals("10:00am", hudRenderer.getDayButton().getText());
        assertEquals(skin, hudRenderer.getDayButton().getSkin());
        assertEquals(object, hudRenderer.getDayButton().getStyle());
        assertEquals(200, hudRenderer.getDayButton().getWidth(), 0f);
        assertEquals(width-200-15, hudRenderer.getDayButton().getX(), 0f);
        assertEquals(height-133, hudRenderer.getDayButton().getY(), 0f);
    }

    @Test
    public void testResize() {
        Skin skin = new Skin();
        Object object = new Object();
        skin.add("informational", object);
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);
        int width = 600;
        int height = 350;
        HudRenderer hudRenderer = new HudRenderer(skin, dialogueManager, width, height);
        int newWidth = 500;
        int newHeight = 325;

        hudRenderer.resize(newWidth, newHeight);

        assertEquals(newWidth, hudRenderer.getViewport().getScreenWidth());
        assertEquals(newHeight, hudRenderer.getViewport().getScreenHeight());
    }

    @Test
    public void testScreenIsBlack() {
        Skin skin = new Skin();
        Object object = new Object();
        skin.add("informational", object);
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);
        int width = 600;
        int height = 350;
        HudRenderer hudRenderer = new HudRenderer(skin, dialogueManager, width, height);

        hudRenderer.fadeIn(0.1f);

        assertTrue(hudRenderer.screenIsBlack());
    }

    @Test
    public void testScreenClear() {
        Skin skin = new Skin();
        Object object = new Object();
        skin.add("informational", object);
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);
        int width = 600;
        int height = 350;
        HudRenderer hudRenderer = new HudRenderer(skin, dialogueManager, width, height);

        hudRenderer.fadeIn(0.01f);
        hudRenderer.fadeOut(0.01f);

        assertFalse(hudRenderer.screenIsBlack());
    }

    @Test
    public void testUpdateValues() {
        Skin skin = new Skin();
        Object object = new Object();
        skin.add("informational", object);
        SoundController soundController = new SoundController();
        DialogueManager dialogueManager = new DialogueManager(soundController);
        int width = 600;
        int height = 350;
        HudRenderer hudRenderer = new HudRenderer(skin, dialogueManager, width, height);

        int newDay = 2;
        String newTimeText = "09:00";
        int newEnergy = 80;
        hudRenderer.updateValues(newTimeText, newDay, newEnergy);

        assertEquals("Day " + newDay, hudRenderer.getDayButton().getText());
        assertEquals(newTimeText, hudRenderer.getTimeButton().getText());
        assertEquals(80/100f, hudRenderer.getEnergyBar().getScaleY(), 0f);
    }
}