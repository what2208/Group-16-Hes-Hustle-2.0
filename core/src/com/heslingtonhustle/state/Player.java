package com.heslingtonhustle.state;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.HashSet;

/**
 * Represents the player character. Is responsible for the movement of the player.
 * */
public class Player {
    // Positional and hitbox variables
    private Vector2 position;
    private Vector2 previousPosition;
    private final Rectangle collisionBox;
    private final Rectangle triggerBox;
    private final float width;
    private final float height;
    private final Vector2 triggerBoxScale;

    // Movement variables
    private boolean moving = false;
    public static final float SPEED = 6f;
    private Facing facing = Facing.DOWN;
    private final float scale = 2f;

    // Achievement
    private float distanceTravelled = 0;
    private int daysWalkedOver200Steps = 0;

    /**
     * A new player instance
     *
     * @param width Width in game units
     * @param height Height in game units
     */
    public Player(float width, float height) {
        this.width = width;
        this.height = height;

        position = new Vector2(0, 0);
        previousPosition = new Vector2(0, 0);

        // Hitbox used to collide with objects
        collisionBox = new Rectangle(0, 0, width, height*0.4f);
        // Hitbox used to trigger events, it's larger than the player
        triggerBoxScale = new Vector2(3, 2.5f);
        triggerBox = new Rectangle(
                -(width*triggerBoxScale.x)/2,
                -(height*triggerBoxScale.y)/2,
                width*triggerBoxScale.x, height*triggerBoxScale.y);
    }

    /**
     * @return Copy of the player's position.
     */
    public Vector2 getPosition() {
        return new Vector2(position);
    }

    /**
     * Sets the player's position, updating all of their relevant hit boxes
     * @param newPosition Vector of the new position
     */
    public void setPosition(Vector2 newPosition) {
        position = newPosition;

        collisionBox.setPosition(position.x,position.y);

        triggerBox.setPosition(
                position.x-(width*triggerBoxScale.x-width)/2,
                position.y-(height*triggerBoxScale.y-height)/2);
    }

    /**
     * Sets just the player's x coordinate
     * @param x The x coordinate to set
     */
    public void setX(float x) {
        if (position == null) {
            position = new Vector2();
        }
        position.x = x;
        collisionBox.setX(x);
        triggerBox.setX(position.x-(width*triggerBoxScale.x-width)/2);
    }

    /**
     * Sets just the player's y coordinate
     * @param y The y coordinate to set to
     */
    public void setY(float y) {
        if (position == null) {
            position = new Vector2();
        }
        position.y = y;
        collisionBox.setY(y);
        triggerBox.setY(position.y-(height*triggerBoxScale.y-height)/2);
    }

    // Player can only move in one direction at a time because of this
    // But what if we wanted MORE!?
    public void move(HashSet<Action> actions, float delta) {
        // Stores the previous position for collision
        previousPosition = getPosition();

        //
        Vector2 movementVector = new Vector2(0, 0);
        moving = false;

        // Find the directions the player needs to move in, accounting
        // for opposite key presses

        // Move left
        if (actions.contains(Action.MOVE_LEFT) && !actions.contains(Action.MOVE_RIGHT)) {
            facing = Facing.LEFT;
            movementVector.add(-SPEED*delta, 0);
            moving = true;
        }

        // Move right
        if (actions.contains(Action.MOVE_RIGHT) && !actions.contains(Action.MOVE_LEFT)) {
            facing = Facing.RIGHT;
            movementVector.add(SPEED*delta, 0);
            moving = true;
        }

        // Move up
        if (actions.contains(Action.MOVE_UP) && !actions.contains(Action.MOVE_DOWN)) {
            facing = Facing.UP;
            movementVector.add(0, SPEED*delta);
            moving = true;
        }

        // Move down
        if (actions.contains(Action.MOVE_DOWN) && !actions.contains(Action.MOVE_UP)) {
            facing = Facing.DOWN;
            movementVector.add(0, -SPEED*delta);
            moving = true;
        }

        // If move in more than 1 direction at a time, scale the movement vector
        // so you move less far, otherwise you move sqrt(2) units, which is > 1
        if (movementVector.x != 0 && movementVector.y != 0) {
            movementVector.scl(0.7f);
        }

        // Set new position
        setPosition(position.add(movementVector));
        distanceTravelled += movementVector.len2();
    }

    /**
     * The player will move itself to the edge of any objects it is
     * overlapping
     * @param rectangles The set of rectangles the player is overlapping
     */
    public void collide(HashSet<Rectangle> rectangles) {

        // For each object
        for (Rectangle collider : rectangles) {
            // If null then the player is not colliding with anything, so do nothing
            if (collider != null) {
                // Find which previous dimension was not overlapping, and then only reset that one to allow
                // sliding to happen

                // If previously not overlapping in x direction, revert them back
                if (!(previousPosition.x < collider.x + collider.width &&
                        previousPosition.x + collisionBox.width > collider.x)) {
                    setX(previousPosition.x);
                }
                // Same with y dimension
                if (!(previousPosition.y < collider.y + collider.height &&
                        previousPosition.y + collisionBox.height > collider.y)) {
                    setY(previousPosition.y);
                }
            }
        }
    }

    /**
     * Stops the player's walking animation
     */
    public void dontMove() {
        moving = false;
    }


    /**
     * @return The centre of the player as a vector
     */
    public Vector2 getCentre() {
        return new Vector2(
                position.x - width/2,
                position.y - height/2
        );
    }

    /**
     * @return The direction the player is facing from Facing
     */
    public Facing getFacing() {
        return facing;
    }

    /**
     * @return True if the player moved the last frame
     */
    public boolean getMoving(){
        return moving;
    }

    /**
     * @return The player's collision hitbox
     */
    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    /**
     * @return The player's trigger hitbox
     */
    public Rectangle getTriggerBox() {
        return triggerBox;
    }

    /**
     * @return Width of the player
     */
    public float getPlayerWidth() {
        return width;
    }

    /**
     * @return Height of the player
     */
    public float getPlayerHeight() {
        return height;
    }

    /**
     * @return The scale the player is drawn at
     */
    public float getScale() {
        return scale;
    }

    /**
     * Sets the player's position to be inside the map, assumes it is drawn from (0, 0)
     */
    public void setInBounds(Vector2 mapSize) {
        position.x = MathUtils.clamp(position.x, 0, mapSize.x);
        position.y = MathUtils.clamp(position.y, 0, mapSize.y);
    }

    /**
     * @return The number of units the player has moved
     */
    public float getDistanceTravelled() {
        return distanceTravelled;
    }

    /**
     * Sets the player's distance travelled to 0
     */
    public void resetDistanceTravelled() {
        distanceTravelled = 0;
    }

    /**
     * Reset's the player's daily step count
     * If the player walked over 200 steps, this is counted
     */
    public void resetStepCounter() {
        if (distanceTravelled > 200) {
            daysWalkedOver200Steps++;
        }
        distanceTravelled = 0;
    }

    public boolean getStepAchievement() {
        return daysWalkedOver200Steps == 7;
    }

    public void setDistanceTravelled(float newDistanceTravelled) { distanceTravelled = newDistanceTravelled; }

    public int getDaysWalkedOver200Steps() { return daysWalkedOver200Steps; }
}
