package io.skloch.heshustle.tests;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.heslingtonhustle.renderer.CharacterRenderer;
import com.heslingtonhustle.state.Facing;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class CharacterRendererTests {
    @Test
    public void testRender() {
        float width = 25f;
        float height = 40f;
        TextureAtlas textureAtlas = new TextureAtlas();
        String textureRegionPrefix = "Prefix";
        boolean npc = false;
        CharacterRenderer characterRenderer = new CharacterRenderer(
                width, height, textureAtlas, textureRegionPrefix, npc);
        float scale = 1.2f;

        SpriteBatch spriteBatch = new SpriteBatch();
        float x = 0f;
        float y = 0f;
        Facing direction = Facing.DOWN;
        Boolean moving = true;
        characterRenderer.render(spriteBatch, x, y, direction, moving);

        assertEquals(x, characterRenderer.getCharacterSprite().getX(), 0f);
        assertEquals(y, characterRenderer.getCharacterSprite().getY(), 0f);
        assertEquals(width, characterRenderer.getCharacterSprite().getWidth(), 0f);
        assertEquals(height, characterRenderer.getCharacterSprite().getHeight(), 0f);
        assertEquals(scale, characterRenderer.getCharacterSprite().getScaleX(), 0f);
        assertEquals(scale, characterRenderer.getCharacterSprite().getScaleY(), 0f);
        assertEquals(width/2f, characterRenderer.getCharacterSprite().getOriginX(), 0f);
        assertEquals(height/2f, characterRenderer.getCharacterSprite().getOriginY(), 0f);
//        assertEquals(0, characterRenderer.getCharacterSprite().getRegionHeight());
//        assertEquals(0, characterRenderer.getCharacterSprite().getRegionWidth());
//        assertEquals(0, characterRenderer.getCharacterSprite().getRegionX());
//        assertEquals(0, characterRenderer.getCharacterSprite().getRegionY());
    }
}