package io.skloch.heshustle.tests;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.heslingtonhustle.renderer.CharacterRenderer;
import com.heslingtonhustle.state.Facing;
import com.heslingtonhustle.state.NPC;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

//@RunWith(GdxTestRunner.class)
public class NPCTests {
    @Test
    public void testGetCentre() {
        Vector2 position = new Vector2(0,0);
        String typeString = "idk";
        Facing direction = Facing.RIGHT;
        NPC npc = new NPC(position, typeString, direction);
        CharacterRenderer characterRenderer = new CharacterRenderer(
                15,
                30,
                new TextureAtlas("Players/npcs.atlas"),
                "npc-16",
                true);
        characterRenderer.addMovingTextures();
        npc.setRenderer(characterRenderer);
        Vector2 newPosition = new Vector2();

        assertEquals(newPosition, npc.getCentre());
    }

    @Test
    public void testGetCentreNoRenderer() {
        Vector2 position = new Vector2(0,0);
        String typeString = "idk";
        Facing direction = Facing.RIGHT;
        NPC npc = new NPC(position, typeString, direction);

        assertNull(npc.getCentre());
    }

    @Test
    public void testRepositionRight() {
        Vector2 position = new Vector2(0,0);
        String typeString = "idk";
        Facing direction = Facing.RIGHT;
        NPC npc = new NPC(position, typeString, direction);
        Vector2 newPlayerPos = new Vector2(20,1);
        float width = 25f;
        float height = 40f;
        TextureAtlas textureAtlas = new TextureAtlas();
        String textureRegionPrefix = "Prefix";
        boolean isNpc = false;
        CharacterRenderer characterRenderer = new CharacterRenderer(
                width, height, textureAtlas, textureRegionPrefix, isNpc);
        characterRenderer.addMovingTextures();
        npc.setRenderer(characterRenderer);

        npc.reposition(newPlayerPos);

        assertEquals(Facing.RIGHT, npc.getFacing());
    }

    @Test
    public void testRepositionLeft() {
        Vector2 position = new Vector2(0,0);
        String typeString = "idk";
        Facing direction = Facing.RIGHT;
        NPC npc = new NPC(position, typeString, direction);
        Vector2 newPlayerPos = new Vector2(-15,-22);
        float width = 25f;
        float height = 40f;
        TextureAtlas textureAtlas = new TextureAtlas();
        String textureRegionPrefix = "Prefix";
        boolean isNpc = false;
        CharacterRenderer characterRenderer = new CharacterRenderer(
                width, height, textureAtlas, textureRegionPrefix, isNpc);
        characterRenderer.addMovingTextures();
        npc.setRenderer(characterRenderer);

        npc.reposition(newPlayerPos);

        assertEquals(Facing.LEFT, npc.getFacing());
    }

    @Test
    public void testRepositionUp() {
        Vector2 position = new Vector2(0,0);
        String typeString = "idk";
        Facing direction = Facing.RIGHT;
        NPC npc = new NPC(position, typeString, direction);
        Vector2 newPlayerPos = new Vector2(1,1);
        float width = 25f;
        float height = 40f;
        TextureAtlas textureAtlas = new TextureAtlas();
        String textureRegionPrefix = "Prefix";
        boolean isNpc = false;
        CharacterRenderer characterRenderer = new CharacterRenderer(
                width, height, textureAtlas, textureRegionPrefix, isNpc);
        characterRenderer.addMovingTextures();
        npc.setRenderer(characterRenderer);

        npc.reposition(newPlayerPos);

        assertEquals(Facing.UP, npc.getFacing());
    }

    @Test
    public void testRepositionDown() {
        Vector2 position = new Vector2(0,0);
        String typeString = "idk";
        Facing direction = Facing.RIGHT;
        NPC npc = new NPC(position, typeString, direction);
        Vector2 newPlayerPos = new Vector2(-14.5f,-23);
        float width = 25f;
        float height = 40f;
        TextureAtlas textureAtlas = new TextureAtlas();
        String textureRegionPrefix = "Prefix";
        boolean isNpc = false;
        CharacterRenderer characterRenderer = new CharacterRenderer(
                width, height, textureAtlas, textureRegionPrefix, isNpc);
        characterRenderer.addMovingTextures();
        npc.setRenderer(characterRenderer);

        npc.reposition(newPlayerPos);

        assertEquals(Facing.DOWN, npc.getFacing());
    }

    @Test
    public void testRender() {
        Vector2 position = new Vector2(0,0);
        String typeString = "idk";
        Facing direction = Facing.RIGHT;
        NPC npc = new NPC(position, typeString, direction);
        float scale = 1.2f;
        SpriteBatch spriteBatch = new SpriteBatch();
        float x = 0f;
        float y = 0f;
        Boolean moving = true;
        float width = 25f;
        float height = 40f;
        TextureAtlas textureAtlas = new TextureAtlas();
        String textureRegionPrefix = "Prefix";
        boolean isNpc = true;
        CharacterRenderer characterRenderer = new CharacterRenderer(
                width, height, textureAtlas, textureRegionPrefix, isNpc);
        characterRenderer.render(spriteBatch, x, y, direction, moving);

        characterRenderer.render(new SpriteBatch(), 10f, 10f, Facing.RIGHT, false);

        assertEquals(x, characterRenderer.getCharacterSprite().getX(), 0f);
        assertEquals(y, characterRenderer.getCharacterSprite().getY(), 0f);
        assertEquals(width, characterRenderer.getCharacterSprite().getWidth(), 0f);
        assertEquals(height, characterRenderer.getCharacterSprite().getHeight(), 0f);
        assertEquals(scale, characterRenderer.getCharacterSprite().getScaleX(), 0f);
        assertEquals(scale, characterRenderer.getCharacterSprite().getScaleY(), 0f);
        assertEquals(width/2f, characterRenderer.getCharacterSprite().getOriginX(), 0f);
        assertEquals(height/2f, characterRenderer.getCharacterSprite().getOriginY(), 0f);
    }

}