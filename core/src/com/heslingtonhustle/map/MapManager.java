package com.heslingtonhustle.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.heslingtonhustle.state.Trigger;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Facilitates communication about maps between the State and the Renderer.
 * Caches loaded maps, map renderers, and collision data.
 * Use this class to load all of your maps, handle collisions, and retrieve data such as map size.
 */
public class MapManager implements Disposable {
    private TiledMap currentMap;
    private final TmxMapLoader mapLoader;
    private final HashMap<String, TiledMap> loadedMaps;
    private final HashMap<TiledMap, OrthogonalTiledMapRenderer> loadedMapRenderers;
    private ShapeRenderer collisionRenderer;
    private MapObjects collisionObjects;
    private MapObjects triggerObjects;
    private final HashMap<TiledMap, Array<RectangleMapObject>> collisionRectangles;
    private final HashMap<TiledMap, Array<RectangleMapObject>> triggerRectangles;

    private HashSet<MapProperties> collidableObjects;

    public MapManager() {
        mapLoader = new TmxMapLoader();
        loadedMaps = new HashMap<>();
        loadedMapRenderers = new HashMap<>();
        collisionRectangles = new HashMap<>();
        triggerRectangles = new HashMap<>();
    }

    public void loadMap(String path) {
        if (!loadedMaps.containsKey(path)) {
            loadedMaps.put(path, mapLoader.load(path));
        }
        currentMap = loadedMaps.get(path);

        try {
            MapLayer collisionLayer = currentMap.getLayers().get("Collisions");
            collisionObjects = collisionLayer.getObjects();
        } catch (NullPointerException e) {
            Gdx.app.debug("DEBUG", "NO COLLISION LAYER FOUND!");
        }

        try {
            MapLayer triggerLayer = currentMap.getLayers().get("Triggers");
            triggerObjects = triggerLayer.getObjects();
        } catch (NullPointerException e) {
            Gdx.app.debug("DEBUG", "NO TRIGGER LAYER FOUND!");
        }

    }

    public OrthogonalTiledMapRenderer getCurrentMapRenderer(SpriteBatch spriteBatch) {
        if (currentMap == null) return null;

        if (!loadedMapRenderers.containsKey(currentMap)) {
            loadedMapRenderers.put(currentMap, new OrthogonalTiledMapRenderer(currentMap, spriteBatch));
        }

        return loadedMapRenderers.get(currentMap);
    }

    public void drawObjectHitboxes() {
        // This method gets the renderer that is used to show the collision rectangles and trigger rectangles
        // for debugging purposes
        if (collisionRenderer == null) {
            collisionRenderer = new ShapeRenderer();
        }
        collisionRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (collisionObjects != null) {
            collisionRenderer.setColor(255, 0, 0, 50);
            for (RectangleMapObject rectangleObject : collisionObjects.getByType(RectangleMapObject.class)) {
                Rectangle collisionRectangle = rectangleObject.getRectangle();
                collisionRenderer.rect(collisionRectangle.x, collisionRectangle.y, collisionRectangle.width, collisionRectangle.height);
            }
        }
        if (triggerObjects != null) {
            collisionRenderer.setColor(0, 0, 255, 50);
            for (RectangleMapObject rectangleObject : triggerObjects.getByType(RectangleMapObject.class)) {
                Rectangle triggerRectangle = rectangleObject.getRectangle();
                collisionRenderer.rect(triggerRectangle.x, triggerRectangle.y, triggerRectangle.width, triggerRectangle.height);
            }
        }
        collisionRenderer.end();
    }

    /**
     * @return The shape renderer the map uses for debugging purposes
     */
    public ShapeRenderer getCollisionRenderer() {
        if (collisionRenderer == null) {
            collisionRenderer = new ShapeRenderer();
        }
        return collisionRenderer;
    }

    /**
     * Returns rectangle the player is colliding with
     * @param playerRectangle Player's hitbox
     * @return null if player is not colliding with anything, the rectangle of the overlapping object otherwise
     */
    public Array<Rectangle> getCollisionRectangles(Rectangle playerRectangle) {
        if (collisionObjects == null) {
            return null;
        }
        playerRectangle = worldRectangleToPixelRectangle(playerRectangle);
        // Get list of map's collidable rectangles
        Array<RectangleMapObject> mapCollisionRectangles = getRectangles(collisionRectangles, collisionObjects);

        // Return a list of all collidable objects the player is colliding with
        return getAllRectangleOverlaps(playerRectangle, mapCollisionRectangles);
    }

    public Trigger getTrigger(Rectangle playerRectangle) {
        if (triggerObjects == null) {
            return null;
        }
        playerRectangle = worldRectangleToPixelRectangle(playerRectangle);
        Array<RectangleMapObject> mapTriggerRectangles = getRectangles(triggerRectangles, triggerObjects);
        RectangleMapObject overlappingRectangle = null;
        if (overlappingRectangle == null) {
            return null;
        }
        MapProperties mapProperties = overlappingRectangle.getProperties();
        return new Trigger(mapProperties);
    }

    /**
     * Finds the nearest trigger the player is overlapping
     * @param playerHitbox The player's trigger hitbox
     * @return The MapProperties of the nearest trigger
     */
    public MapProperties getNearestTrigger(Rectangle playerHitbox) {
        if (triggerObjects == null) {
            return null;
        }
        // The distance of the nearest trigger
        float closestDistance = -1f;
        MapProperties closestObject = null;

        for (MapObject object : triggerObjects) {
            if (object instanceof RectangleMapObject) {
                // Only calc if the trigger box overlaps the player
                Rectangle objRect = ((RectangleMapObject) object).getRectangle();
                if (playerHitbox.overlaps(objRect)) {
                    float distance = distanceBetween(objRect, playerHitbox);
                    if (closestObject == null|| distance < closestDistance) {
                        closestObject = object.getProperties();
                        closestDistance = distance;
                    }
                }
            }
        }

        return closestObject;


    }

    /**
     * Calculates the distance between the centre of two rectangles
     * @param rect1 The first rectangle
     * @param rect2 The second rectangle
     * @return The distance between the centre of the two rectangles
     */
    private float distanceBetween(Rectangle rect1, Rectangle rect2) {
        Vector2 centres1 = new Vector2(rect1.width / 2, rect1.height / 2);
        Vector2 centres2 = new Vector2(rect2.width / 2, rect2.height / 2);

        return (float) Math.sqrt((Math.pow((centres1.x - centres2.x), 2) + Math.pow((centres1.y - centres2.y), 2)));
    }


    /**
     * @param cache Collection containing cached object. May be modified to add a new array.
     * @return The array of rectangles added to/retrieved from the cache.
     */
    private Array<RectangleMapObject> getRectangles(HashMap<TiledMap,
                                                    Array<RectangleMapObject>> cache,
                                                    MapObjects objects) {
        if (currentMap == null) return new Array<>();

        if (!cache.containsKey(currentMap)) {
            cache.put(currentMap, objects.getByType(RectangleMapObject.class));
        }

        return cache.get(currentMap);
    }

    public Vector2 getCurrentMapTileDimensions() {
        //TODO: Add null check
        return new Vector2(
                (int)currentMap.getProperties().get("tilewidth"),
                (int)currentMap.getProperties().get("tileheight")
        );
    }

    public Vector2 getCurrentMapWorldDimensions() {
        //TODO: Add null check
        return new Vector2(
                (int)currentMap.getProperties().get("width"),
                (int)currentMap.getProperties().get("height")
        );
    }

    public Vector2 getCurrentMapPixelDimensions() {
        //TODO: Add null check
        return new Vector2(
                getCurrentMapWorldDimensions().x * getCurrentMapTileDimensions().x,
                getCurrentMapWorldDimensions().y * getCurrentMapTileDimensions().y
        );
    }

    public Vector2 worldToPixelCoords(Vector2 worldCoords) {
        return new Vector2(
                worldCoords.x * getCurrentMapTileDimensions().x,
                worldCoords.y * getCurrentMapTileDimensions().y
        );
    }

    public float worldToPixelValue(float worldValue) {
        return worldValue * getCurrentMapTileDimensions().x;
    }

    public Rectangle worldRectangleToPixelRectangle(Rectangle rectangle) {
        float x = rectangle.x * getCurrentMapTileDimensions().x;
        float y = rectangle.y * getCurrentMapTileDimensions().y;
        float width = rectangle.width * getCurrentMapTileDimensions().x;
        float height = rectangle.height * getCurrentMapTileDimensions().y;
        return new Rectangle(x, y, width, height);
    }

    public Rectangle pixelToWorldRectangle(Rectangle rectangle) {
        float x = rectangle.x / getCurrentMapTileDimensions().x;
        float y = rectangle.y / getCurrentMapTileDimensions().y;
        float width = rectangle.width / getCurrentMapTileDimensions().x;
        float height = rectangle.height / getCurrentMapTileDimensions().y;
        return new Rectangle(x, y, width, height);
    }

    /**
     * Returns a set of rectangles the player is overlapping
     * @param playerRectangle The player's collision rectangles
     * @return A set of rectangles, may be empty
     */
    public HashSet<Rectangle> getRectanglesInside(Rectangle playerRectangle) {
        // For each rectangle in the collisions layer, check whether the player rectangle intercepts
        HashSet<Rectangle> overlaps = new HashSet<>();
        playerRectangle = worldRectangleToPixelRectangle(playerRectangle);
        for (MapObject object : collisionObjects) {
            // If rectangle
            if (object instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                if (playerRectangle.overlaps(rectangle)) {
                    // Translate to screen coordinates for player's collision
                    overlaps.add(pixelToWorldRectangle(rectangle));
                }
            }
        }
        return overlaps;
    }

    /**
     * Returns a list of all the rectangles the player is overlapping with,
     * useful for collision
     * @param playerRectangle The player's hitbox
     * @param mapRectangles All collision rectangles in the map
     * @return A list of rectangles the player is overlapping, empty if none
     */
    private Array<Rectangle> getAllRectangleOverlaps(Rectangle playerRectangle, Array<RectangleMapObject> mapRectangles) {
        Array<Rectangle> overlaps = new Array<>();

        // For each rectangle in the collisions layer, check whether the player rectangle intercepts
        for (RectangleMapObject rectangleObject : mapRectangles) {
            Rectangle collisionRectangle = rectangleObject.getRectangle();
            if (Intersector.overlaps(collisionRectangle, playerRectangle)) {
                overlaps.add(collisionRectangle);
            }
        }

        return overlaps;
    }

    @Override
    public void dispose() {
        for (TiledMap map : loadedMaps.values()) {
            map.dispose();
        }
        for (OrthogonalTiledMapRenderer mapRenderer: loadedMapRenderers.values()) {
            mapRenderer.dispose();
        }
        if (collisionRenderer != null) {
            collisionRenderer.dispose();
        }
    }
}
