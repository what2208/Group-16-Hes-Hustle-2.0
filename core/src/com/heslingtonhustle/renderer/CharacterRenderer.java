package com.heslingtonhustle.renderer;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.heslingtonhustle.state.Facing;

/**
 * A class used to store and render the animations for the player
 * and other NPC characters
 */
public class CharacterRenderer {
    private final TextureManager characterTextures;
    private final Sprite characterSprite;
    private final TextureAtlas textureAtlas;
    private final String textureRegionPrefix;
    private final Vector2 size;


    /**
     * Instantiates a new renderer for a specific character
     * Because the idea is that this can be used for multiple characters, we use textureRegionPrefix
     * For example, in the texture atlas, if you have regions such as 'main-player-idle-down', 'main-player-idle-right',
     * the prefix is 'main-player'
     * @param width Width of the character in real world pixels
     * @param height Height of the character
     * @param textureAtlas The atlas containing the character textures
     * @param textureRegionPrefix The prefix to identify the character 'player-x'
     */
    public CharacterRenderer(float width, float height, TextureAtlas textureAtlas, String textureRegionPrefix, boolean npc) {

        this.textureAtlas = textureAtlas;
        this.textureRegionPrefix = textureRegionPrefix;

        size = new Vector2(width, height);

        characterTextures = new TextureManager();

        addStaticTextures();
        if (!npc) addMovingTextures();

        characterSprite = new Sprite(characterTextures.retrieveTexture("idle-down"));
        characterSprite.setSize(width, height);
        characterSprite.setOriginCenter();
        characterSprite.setScale(1.2f);
    }

    /**
     * Renders a character at a certain position
     * @param batch The spritebatch to render to
     * @param x x position of the character
     * @param y y position of the character
     * @param direction The direction the character should be facing
     * @param moving Whether teh character is moving or not, if so will play
     *               a walking animation
     */
    public void render(SpriteBatch batch, float x, float y, Facing direction, Boolean moving) {
        String textureKey = getTextureKey(direction, moving);
        characterSprite.setRegion(characterTextures.retrieveTexture(textureKey));
        characterSprite.setPosition(x, y);
        characterSprite.draw(batch);
    }

    /**
     * Specifically only loads the static characters for a texture.
     * Useful since NPCs don't have any moving textures
     */
    private void addStaticTextures() {
        // First by adding the static textures (when the character is idle)
        TextureRegion idleLeft = textureAtlas.findRegion(textureRegionPrefix+"-idle-left");
        characterTextures.addTexture("idle-left", idleLeft);
        TextureRegion idleRight = textureAtlas.findRegion(textureRegionPrefix+"-idle-right");
        characterTextures.addTexture("idle-right", idleRight);
        TextureRegion idleUp = textureAtlas.findRegion(textureRegionPrefix+"-idle-up");
        characterTextures.addTexture("idle-up", idleUp);
        TextureRegion idleDown = textureAtlas.findRegion(textureRegionPrefix+"-idle-down");
        characterTextures.addTexture("idle-down", idleDown);
    }

    /**
     * Specifically loads the frames for walking animations
     * Only used for players
     */
    public void addMovingTextures() {
        float speed = 0.15f;

        Array<TextureAtlas.AtlasRegion> walkingLeft = textureAtlas.findRegions(textureRegionPrefix+"-walking-left");
        characterTextures.addAnimation("walking-left", walkingLeft, speed);
        Array<TextureAtlas.AtlasRegion> walkingRight = textureAtlas.findRegions(textureRegionPrefix+"-walking-right");
        characterTextures.addAnimation("walking-right", walkingRight, speed);
        Array<TextureAtlas.AtlasRegion> walkingUp = textureAtlas.findRegions(textureRegionPrefix+"-walking-up");
        characterTextures.addAnimation("walking-up", walkingUp, speed);
        Array<TextureAtlas.AtlasRegion> walkingDown = textureAtlas.findRegions(textureRegionPrefix+"-walking-down");
        characterTextures.addAnimation("walking-down", walkingDown, speed);
    }

    /**
     * Returns the correct texture key used to identify an animation based
     * on whether the character is moving and in what direction
     * @param direction A direction from the Facing enum
     * @param moving True if the player is moving
     * @return The texture key used to identify an animation
     */
    private String getTextureKey(Facing direction, Boolean moving) {
        String textureKey = "idle-down";
        if (moving) {
            switch (direction) {
                case LEFT:
                    textureKey = "walking-left";
                    break;
                case RIGHT:
                    textureKey = "walking-right";
                    break;
                case UP:
                    textureKey = "walking-up";
                    break;
                case DOWN:
                    textureKey = "walking-down";
                    break;
            }
        } else {
                switch (direction) {
                    case LEFT:
                        textureKey = "idle-left";
                        break;
                    case RIGHT:
                        textureKey = "idle-right";
                        break;
                    case UP:
                        textureKey = "idle-up";
                        break;
                    case DOWN:
                        break;
                    default:
                        break;
            }
        }
        return textureKey;
    }

    /**
     * Returns the world width and height of the rendered sprite
     * @return A vector of the width and height
     */
    public Vector2 getSize() {
        return size;
    }
}
