package com.heslingtonhustle.state;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.heslingtonhustle.renderer.CharacterRenderer;

/**
 * Small class to store information about a loaded NPC
 */
public class NPC {
    private final Vector2 position;
    private final String typeString;
    private Facing direction;
    private CharacterRenderer renderer;

    public NPC (Vector2 position, String typeString, Facing direction) {
        this.position = position;
        this.typeString = typeString;
        this.direction = direction;
    }

    /**
     * @return The centre coordinates of the NPC
     */
    public Vector2 getCentre() {
        if (renderer == null) return null;

        return new Vector2(
                position.x - renderer.getSize().x/2,
                position.y - renderer.getSize().y/2
        );
    }

    /**
     * Repositions the NPC based on where the player is
     * Called after speaking to the NPC
     *
     * @param playerPos The centre position of the player
     */
    public void reposition(Vector2 playerPos) {
        Vector2 diff = playerPos.sub(getCentre());

        if (Math.abs(diff.x) > Math.abs(diff.y) && diff.x > 0) direction = Facing.RIGHT;
        if (Math.abs(diff.x) > Math.abs(diff.y) && diff.x < 0) direction = Facing.LEFT;
        if (Math.abs(diff.x) < Math.abs(diff.y) && diff.y > 0) direction = Facing.UP;
        if (Math.abs(diff.x) < Math.abs(diff.y) && diff.y < 0) direction = Facing.DOWN;
    }

    /**
     * Returns the string needed to identify the character on the character atlas sheet
     * @return The type of NPC, 'player-x'
     */
    public String getType() {
        return typeString;
    }

    /**
     * Gives the NPC a renderer to be able to be drawn to the screen
     */
    public void setRenderer(CharacterRenderer renderer) {
        this.renderer = renderer;
    }

    /**
     * Renders the NPC to the screen
     */
    public void render(SpriteBatch batch) {
        renderer.render(batch, position.x, position.y, direction, false);
    }

    /**
     * @return The direction the NPC is facing
     */
    public Facing getFacing() {
        return direction;
    }

    /**
     * Sets the direction the NPC is facing
     * @param facing The direction to face
     */
    public void setFacing(Facing facing) {
        this.direction = facing;
    }

}
