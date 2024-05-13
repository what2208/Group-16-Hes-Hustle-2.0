package com.heslingtonhustle.renderer;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.heslingtonhustle.state.Facing;

import java.util.Vector;

/**
 * A class used to store and render the animations for the player
 * and other NPC characters
 */
public class CharacterRenderer {
    private final TextureManager characterTextures;
    private final Sprite characterSprite;
    private final TextureAtlas textureAtlas;
    private final String textureRegionPrefix;
    private Vector2 size;


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
    public CharacterRenderer(float width, float height, TextureAtlas textureAtlas, String textureRegionPrefix) {

        this.textureAtlas = textureAtlas;
        this.textureRegionPrefix = textureRegionPrefix;

        size = new Vector2(width, height);

        characterTextures = new TextureManager();
        addCharacterTextures();
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
     * @param moving
     */
    public void render(SpriteBatch batch, float x, float y, Facing direction, Boolean moving) {
        String textureKey = getTextureKey(direction, moving);
        characterSprite.setRegion(characterTextures.retrieveTexture(textureKey));
        characterSprite.setPosition(x, y);
        characterSprite.draw(batch);
    }

    private void addCharacterTextures() {
        // First by adding the static textures (when the character is idle)
        TextureRegion idleLeft = textureAtlas.findRegion(textureRegionPrefix+"-idle-left");
        characterTextures.addTexture("idle-left", idleLeft);
        TextureRegion idleRight = textureAtlas.findRegion(textureRegionPrefix+"-idle-right");
        characterTextures.addTexture("idle-right", idleRight);
        TextureRegion idleUp = textureAtlas.findRegion(textureRegionPrefix+"-idle-up");
        characterTextures.addTexture("idle-up", idleUp);
        TextureRegion idleDown = textureAtlas.findRegion(textureRegionPrefix+"-idle-down");
        characterTextures.addTexture("idle-down", idleDown);

        // Now we need to add the textures that are used in animation.
        // the findRegions() function will find all areas of the atlas that have the same name and a number suffix
        // For example findRegions("walking_left") will find "walking_left_00", "walking_left_01", "walking_left_02 etc.

        float speed = 0.15f;

        // NB: finderegion is quite an expensive method, if performance is an issue consider caching the TextureRegions.
        Array<TextureAtlas.AtlasRegion> walkingLeft = textureAtlas.findRegions(textureRegionPrefix+"-walking-left");
        characterTextures.addAnimation("walking-left", walkingLeft, speed);
        Array<TextureAtlas.AtlasRegion> walkingRight = textureAtlas.findRegions(textureRegionPrefix+"-walking-right");
        characterTextures.addAnimation("walking-right", walkingRight, speed);
        Array<TextureAtlas.AtlasRegion> walkingUp = textureAtlas.findRegions(textureRegionPrefix+"-walking-up");
        characterTextures.addAnimation("walking-up", walkingUp, speed);
        Array<TextureAtlas.AtlasRegion> walkingDown = textureAtlas.findRegions(textureRegionPrefix+"-walking-down");
        characterTextures.addAnimation("walking-down", walkingDown, speed);
    }

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
