package com.heslingtonhustle.state;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

/** Represents the player character. Is responsible for the movement of the player. */
public class Player {
    public static final float SPEED = 0.1f;

    private Vector2 position;
    private Rectangle collisionBox;
    private HashSet<Action> movement = new HashSet<>();
    private Facing facing = Facing.DOWN;
    private final float width;
    private final float height;
    private final float scale = 2f;

    /**
     * @param startingX Spawn location
     * @param startingY Spawn location
     * @param width Width in game units
     * @param height Height in game units
     */
    public Player(float startingX, float startingY, float width, float height) {
        this.width = width;
        this.height = height;
        setPosition(startingX,startingY);
    }

    /**
     * The behaviour of the player character in a single frame. Call move() first.
     */
    public void update() {
        // Respond to the actions of all pressed keys
        float speed = SPEED;
        if (movement.size() >= 2) {
            // Correct for boost in distance travelled when moving diagonally
            speed *= 0.75f;
        }

        for (Action action : movement) {
            switch (action) {
                case STOP:
                    break;
                case MOVE_LEFT:
                    facing = Facing.LEFT;
                    setPosition(position.x-speed, position.y);
                    break;
                case MOVE_RIGHT:
                    facing = Facing.RIGHT;
                    setPosition(position.x+speed, position.y);
                    break;
                case MOVE_UP:
                    facing = Facing.UP;
                    setPosition(position.x, position.y+speed);
                    break;
                case MOVE_DOWN:
                    facing = Facing.DOWN;
                    setPosition(position.x, position.y-speed);
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * @return Copy of the player's position.
     */
    public Vector2 getPosition() {
        return new Vector2(position);
    }

    public void setPosition(float x, float y) {
        if (position == null) {
            position = new Vector2();
        }
        position.x = x;
        position.y = y;

        if (collisionBox == null) {
            collisionBox = new Rectangle();
            collisionBox.setWidth(width);
            collisionBox.setHeight(height*0.4f);
        }
        collisionBox.setPosition(position.x, position.y);
    }
    public void setPosition(Vector2 newPosition) {
        position = newPosition;
        collisionBox.setPosition(position);
    }

    /**
     * Sets just the player's x coordinate
     * @param x
     */
    public void setX(float x) {
        if (position == null) {
            position = new Vector2();
        }
        position.x = x;
        collisionBox.setX(x);
    }

    /**
     * Sets just the player's y coordinate
     * @param y
     */
    public void setY(float y) {
        if (position == null) {
            position = new Vector2();
        }
        position.y = y;
        collisionBox.setY(y);
    }

    // Player can only move in one direction at a time because of this
    // But what if we wanted MORE!?
    public void move(HashSet<Action> actions) {
        // Pick out only movement actions from all pressed actions
        movement.clear();
        if (actions.contains(Action.MOVE_UP)) movement.add(Action.MOVE_UP);
        if (actions.contains(Action.MOVE_DOWN)) movement.add(Action.MOVE_DOWN);
        if (actions.contains(Action.MOVE_LEFT)) movement.add(Action.MOVE_LEFT);
        if (actions.contains(Action.MOVE_RIGHT)) movement.add(Action.MOVE_RIGHT);

        // Check for opposite directions
        if (movement.contains(Action.MOVE_UP) && movement.contains(Action.MOVE_DOWN)) {
            movement.remove(Action.MOVE_UP);
            movement.remove(Action.MOVE_DOWN);
        }

        if (movement.contains(Action.MOVE_LEFT) && movement.contains(Action.MOVE_RIGHT)) {
            movement.remove(Action.MOVE_LEFT);
            movement.remove(Action.MOVE_RIGHT);
        }

        // If no actions are pressed, just stop
        if (movement.isEmpty()) {
            movement.add(Action.STOP);
        }
    }

    /**
     * Ensures the player does not move this frame
     */
    public void freeze() {
        movement.clear();
        movement.add(Action.STOP);
    }

    public Facing getFacing() {
        return facing;
    }

    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    public float getPlayerWidth() {
        return width;
    }

    public float getPlayerHeight() {
        return height;
    }

    public float getScale() {
        return scale;
    }

    public void setInBounds(Vector2 mapSize) {
        position.x = MathUtils.clamp(position.x, 0, mapSize.x);
        position.y = MathUtils.clamp(position.y, 0, mapSize.y);
    }

    public HashSet<Action> getMovement() {
        return movement;
    }
}
