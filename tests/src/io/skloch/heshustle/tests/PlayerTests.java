package io.skloch.heshustle.tests;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.heslingtonhustle.state.Action;
import com.heslingtonhustle.state.Facing;
import org.junit.Test;

import com.heslingtonhustle.state.Player;

import java.util.HashSet;

import static org.junit.Assert.*;

public class PlayerTests {
    @Test
    public void testSetPositionByCoordinates() {
        float startingX = 0f;
        float startingY = 0f;
        float width = 40f;
        float height = 25f;
        Player player = new Player(width, height);
        float newX = 10f;
        float newY = 10f;
        Vector2 newPosition = new Vector2(newX, newY);
        Vector2 triggerBoxScale = new Vector2(3, 2.5f);
        float newTriggerBoxX = newX - (width * triggerBoxScale.x - width) / 2;
        float newTriggerBoxY = newY - (height * triggerBoxScale.y - height) / 2;

        player.setPosition(newPosition);

        assertEquals(newPosition, player.getPosition());
        assertEquals(newX, player.getCollisionBox().x, 0.0f);
        assertEquals(newY, player.getCollisionBox().y, 0.0f);
        assertEquals(newTriggerBoxX, player.getTriggerBox().x, 0.0f);
        assertEquals(newTriggerBoxY, player.getTriggerBox().y, 0.0f);
    }

    @Test
    public void testSetPositionByVector() {
        float startingX = 0f;
        float startingY = 0f;
        float width = 40f;
        float height = 25f;
        Player player = new Player(width, height);
        float newX = 10f;
        float newY = 10f;
        Vector2 newPosition = new Vector2(newX,newY);
        Vector2 triggerBoxScale = new Vector2(3, 2.5f);
        float newTriggerBoxX = newX - (width * triggerBoxScale.x - width) / 2;
        float newTriggerBoxY = newY - (height * triggerBoxScale.y - height) / 2;

        player.setPosition(newPosition);

        assertEquals(newPosition, player.getPosition());
        assertEquals(newX, player.getCollisionBox().x, 0.0f);
        assertEquals(newY, player.getCollisionBox().y, 0.0f);
        assertEquals(newTriggerBoxX, player.getTriggerBox().x, 0.0f);
        assertEquals(newTriggerBoxY, player.getTriggerBox().y, 0.0f);
    }

    @Test
    public void testSetX() {
        float startingX = 0f;
        float startingY = 0f;
        float width = 40f;
        float height = 25f;
        Player player = new Player(width, height);
        float newX = 10f;
        Vector2 triggerBoxScale = new Vector2(3, 2.5f);
        float newTriggerX = newX-(width*triggerBoxScale.x-width)/2;

        player.setX(newX);

        assertEquals(newX, player.getPosition().x, 0.0f);
        assertEquals(newX, player.getCollisionBox().x, 0.0f);
        assertEquals(newTriggerX, player.getTriggerBox().x, 0.0f);
    }

    @Test
    public void testSetY() {
        float startingX = 0f;
        float startingY = 0f;
        float width = 40f;
        float height = 25f;
        Player player = new Player(width, height);
        float newY = 10f;
        Vector2 triggerBoxScale = new Vector2(3, 2.5f);
        float newTriggerY = newY -(height*triggerBoxScale.y-height)/2;

        player.setY(newY);

        assertEquals(newY, player.getPosition().y, 0.0f);
        assertEquals(newY, player.getCollisionBox().y, 0.0f);
        assertEquals(newTriggerY, player.getTriggerBox().y, 0.0f);
    }

    @Test
    public void testMoveWhenMoveLeft() {
        float startingX = 0f;
        float startingY = 0f;
        float width = 40f;
        float height = 25f;
        Player player = new Player(width, height);
        HashSet<Action> actions = new HashSet<>();
        actions.add(Action.MOVE_LEFT);
        float delta = 0f;
        Vector2 movementVector = new Vector2(0, 0);
        float SPEED = 6f;
        movementVector.add(-SPEED*delta, 0);
        Vector2 newPosition = new Vector2(startingX, startingY);
        float newDistanceTravelled = 0 + movementVector.len2();

        player.move(actions, delta);

        assertEquals(Facing.LEFT, player.getFacing());
        assertTrue(player.getMoving());
        assertEquals(newPosition, player.getPosition());
        assertEquals(newDistanceTravelled, player.getDistanceTravelled(), 0f);
    }

    @Test
    public void testMoveWhenMoveRight() {
        float startingX = 0f;
        float startingY = 0f;
        float width = 40f;
        float height = 25f;
        Player player = new Player(width, height);
        HashSet<Action> actions = new HashSet<>();
        actions.add(Action.MOVE_RIGHT);
        float delta = 0f;
        Vector2 movementVector = new Vector2(0, 0);
        float SPEED = 6f;
        movementVector.add(SPEED*delta, 0);
        Vector2 newPosition = new Vector2(startingX, startingY);
        float newDistanceTravelled = 0 + movementVector.len2();

        player.move(actions, delta);

        assertEquals(Facing.RIGHT, player.getFacing());
        assertTrue(player.getMoving());
        assertEquals(newPosition, player.getPosition());
        assertEquals(newDistanceTravelled, player.getDistanceTravelled(), 0f);
    }

    @Test
    public void testMoveWhenMoveUp() {
        float startingX = 0f;
        float startingY = 0f;
        float width = 40f;
        float height = 25f;
        Player player = new Player(width, height);
        HashSet<Action> actions = new HashSet<>();
        actions.add(Action.MOVE_UP);
        float delta = 0f;
        Vector2 movementVector = new Vector2(0, 0);
        float SPEED = 6f;
        movementVector.add(0, SPEED*delta);
        Vector2 newPosition = new Vector2(startingX, startingY);
        float newDistanceTravelled = 0 + movementVector.len2();

        player.move(actions, delta);

        assertEquals(Facing.UP, player.getFacing());
        assertTrue(player.getMoving());
        assertEquals(newPosition, player.getPosition());
        assertEquals(newDistanceTravelled, player.getDistanceTravelled(), 0f);
    }

    @Test
    public void testMoveWhenMoveDown() {
        float startingX = 0f;
        float startingY = 0f;
        float width = 40f;
        float height = 25f;
        Player player = new Player(width, height);
        HashSet<Action> actions = new HashSet<>();
        actions.add(Action.MOVE_DOWN);
        float delta = 0f;
        Vector2 movementVector = new Vector2(0, 0);
        float SPEED = 6f;
        movementVector.add(0, -SPEED*delta);
        Vector2 newPosition = new Vector2(startingX, startingY);
        float newDistanceTravelled = 0 + movementVector.len2();

        player.move(actions, delta);

        assertEquals(Facing.DOWN, player.getFacing());
        assertTrue(player.getMoving());
        assertEquals(newPosition, player.getPosition());
        assertEquals(newDistanceTravelled, player.getDistanceTravelled(), 0f);
    }

    @Test
    public void testMoveWhenMultipleDirections() {
        float startingX = 0f;
        float startingY = 0f;
        float width = 40f;
        float height = 25f;
        Player player = new Player(width, height);
        HashSet<Action> actions = new HashSet<>();
        actions.add(Action.MOVE_DOWN);
        actions.add(Action.MOVE_LEFT);
        float delta = 0f;
        Vector2 movementVector = new Vector2(0, 0);
        float SPEED = 6f;
        movementVector.add(0, -SPEED*delta);
        movementVector.add(-SPEED*delta, 0);
        movementVector.scl(0.7f);
        Vector2 newPosition = new Vector2(startingX, startingY);
        float newDistanceTravelled = 0 + movementVector.len2();

        player.move(actions, delta);

        assertTrue(player.getMoving());
        assertEquals(newPosition, player.getPosition());
        assertEquals(newDistanceTravelled, player.getDistanceTravelled(), 0f);
    }

    @Test
    public void testDontMove() {
        float width = 40f;
        float height = 25f;
        Player player = new Player(width, height);

        player.dontMove();

        assertFalse(player.getMoving());
    }

    @Test
    public void testSetInBounds() {
        float startingX = 0f;
        float startingY = 0f;
        float width = 40f;
        float height = 25f;
        Player player = new Player(width, height);
        Vector2 mapSize = new Vector2(50,50);
        float newX = MathUtils.clamp(startingX, 0, mapSize.x);
        float newY = MathUtils.clamp(startingY, 0, mapSize.y);

        player.setInBounds(mapSize);

        assertEquals(newX, player.getPosition().x, 0f);
        assertEquals(newY, player.getPosition().y, 0f);
    }

    @Test
    public void testResetDistanceTravelled() {
        float startingX = 0f;
        float startingY = 0f;
        float width = 40f;
        float height = 25f;
        Player player = new Player(width, height);

        player.resetDistanceTravelled();

        assertEquals(0, player.getDistanceTravelled(), 0f);
    }

    @Test
    public void testResetStepCounterOverGoal() {
        float startingX = 0f;
        float startingY = 0f;
        float width = 40f;
        float height = 25f;
        Player player = new Player(width, height);
        int newDaysWalkedOver200Steps = 1;
        int distanceTravelled = 300;
        player.setDistanceTravelled(distanceTravelled);

        player.resetStepCounter();

        assertEquals(newDaysWalkedOver200Steps, player.getDaysWalkedOver200Steps());
        assertEquals(0, player.getDistanceTravelled(), 0f);
    }

    @Test
    public void testResetStepCounterUnderGoal() {
        float width = 40f;
        float height = 25f;
        Player player = new Player(width, height);
        int newDaysWalkedOver200Steps = 0;
        int distanceTravelled = 100;
        player.setDistanceTravelled(distanceTravelled);

        player.resetStepCounter();

        assertEquals(newDaysWalkedOver200Steps, player.getDaysWalkedOver200Steps());
        assertEquals(0, player.getDistanceTravelled(), 0f);
    }

    @Test
    public void testCollide() {
        float width = 40f;
        float height = 25f;
        Player player = new Player(width, height);
        Rectangle collidable = new Rectangle(50f, 5f, 20f, 100f);
        HashSet<Rectangle> rectangles = new HashSet<Rectangle>();
        rectangles.add(collidable);
        HashSet<Action> actions = new HashSet<>();
        actions.add(Action.MOVE_RIGHT);
        player.move(actions, 0f);
        float newX = 0f;
        float newY = 0f;

        player.collide(rectangles);

        assertEquals(newX, player.getPosition().x, 0f);
        assertEquals(newY, player.getPosition().y, 0f);
    }
}