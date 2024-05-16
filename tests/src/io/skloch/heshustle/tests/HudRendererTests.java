package io.skloch.heshustle.tests;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.heslingtonhustle.renderer.HudRenderer;
import com.heslingtonhustle.sound.SoundController;
import com.heslingtonhustle.state.DialogueManager;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
}