package com.heslingtonhustle.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Disposable;
import com.heslingtonhustle.renderer.CharacterRenderer;
import com.heslingtonhustle.state.Facing;
import com.heslingtonhustle.state.NPC;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Facilitates communication about maps between the State and the Renderer.
 * Caches loaded maps, map renderers, and collision data.
 */
public class MapManager implements Disposable {
    private TiledMap currentMap;
    private final TmxMapLoader mapLoader;
    private final HashMap<String, TiledMap> loadedMaps;
    private final HashMap<TiledMap, OrthogonalTiledMapRenderer> loadedMapRenderers;
    private ShapeRenderer collisionRenderer;
    private MapObjects collisionObjects;
    private MapObjects triggerObjects;
    private MapObjects labelObjects;
    private HashMap<MapProperties, NPC> NPCs;
    private int[] backgroundLayers;
    private int[] foregroundLayers;
    private final TextureAtlas npcAtlas;
    private final BitmapFont labelFont;

    /**
     * Instantiates a new map manager to manage loading and switching
     * between maps loaded from Tiled
     */
    public MapManager() {
        mapLoader = new TmxMapLoader();
        loadedMaps = new HashMap<>();
        loadedMapRenderers = new HashMap<>();
        npcAtlas = new TextureAtlas("Players/npcs.atlas");
        labelFont = new BitmapFont(Gdx.files.internal("Fonts/labelfont.fnt"),false);
    }

    /**
     * Loads in the given map and the layers for collisions, NPCs
     * and triggers.
     * Also differentiates between foreground and background layers
     * @param path The filepath of the map to load
     */
    public void loadMap(String path) {
        if (!loadedMaps.containsKey(path)) {
            loadedMaps.put(path, mapLoader.load(path));
        }
        currentMap = loadedMaps.get(path);

        // Get collidable objects
        try {
            MapLayer collisionLayer = currentMap.getLayers().get("Collisions");
            collisionObjects = collisionLayer.getObjects();
        } catch (NullPointerException e) {
            Gdx.app.debug("DEBUG", "NO COLLISION LAYER FOUND!");
        }

        // Get triggerable objects
        try {
            MapLayer triggerLayer = currentMap.getLayers().get("Triggers");
            triggerObjects = triggerLayer.getObjects();
        } catch (NullPointerException e) {
            Gdx.app.debug("DEBUG", "NO TRIGGER LAYER FOUND!");
        }

        // Get NPCs
        try {
            MapLayer triggerLayer = currentMap.getLayers().get("NPCs");
            createNPCs(triggerLayer.getObjects());
        } catch (NullPointerException e) {
            Gdx.app.debug("DEBUG", "NO NPC LAYER FOUND!");
        }

        // Get labels
        try {
            MapLayer labelLayer = currentMap.getLayers().get("Labels");
            labelObjects = labelLayer.getObjects();
        } catch (NullPointerException e) {
            labelObjects = null;
        }

        // Get which layers are foreground and which are background

        IntArray foreground = new IntArray(true, currentMap.getLayers().getCount());
        IntArray background = new IntArray(true, currentMap.getLayers().getCount());

        // Foreground and background
        for (int i=0; i < currentMap.getLayers().getCount(); i++) {
            MapProperties properties = currentMap.getLayers().get(i).getProperties();
            if (properties.containsKey("foreground")) {
                // If foreground property is true
                if ((boolean) properties.get("foreground")) {
                    foreground.add(i);
                } else {
                    background.add(i);
                }
            } else {
                background.add(i);
            }
        }

        backgroundLayers = background.toArray();
        foregroundLayers = foreground.toArray();

    }

    /**
     * Creates NPC classes from loaded NPC objects from Tiled
     * @param NPCObjects The loaded NPCs from the NPC layer
     */
    private void createNPCs(MapObjects NPCObjects) {
        NPCs = new HashMap<>();
        for (MapObject npcObject : NPCObjects) {
            // Load info
            Facing[] directions = new Facing[]{Facing.UP, Facing.RIGHT, Facing.DOWN, Facing.LEFT};
            MapProperties props = npcObject.getProperties();

            try {
                // Create a new NPC
                NPC character = new NPC(
                        new Vector2((float) props.get("x"), (float) props.get("y")),
                        (String) props.get("type"),
                        directions[(int) props.get("direction")]
                );

                // Give it a renderer
                character.setRenderer(
                        new CharacterRenderer(
                                worldToPixelValue(0.9f),
                                worldToPixelValue(0.9f),
                                npcAtlas,
                                character.getType(),
                                true
                        ));

                NPCs.put(npcObject.getProperties(), character);
                // Also add a trigger
                if (npcObject.getProperties().containsKey("silent")) {
                    if (!npcObject.getProperties().get("silent", Boolean.class)) {
                        triggerObjects.add(npcObject);
                    }
                } else {
                    triggerObjects.add(npcObject);
                }

                collisionObjects.add(npcObject);

            } catch (NullPointerException e) {
                Gdx.app.debug("DEBUG", "ERROR LOADING NPC!");
            }
        }
    }

    /**
     * Renders all the NPCs
     * @param batch The sprite batch to render to
     */
    public void renderNPCs(SpriteBatch batch) {
        for (MapProperties object : NPCs.keySet()) {
            NPCs.get(object).render(batch);
        }
    }

    /**
     * Rotates an NPC based on the player's centre point
     * @param npc The properties of the NPC to rotate
     * @param playerCentre The current centre position of the player
     */
    public void rotateNPC(MapProperties npc, Vector2 playerCentre) {
        NPCs.get(npc).reposition(playerCentre);
    }

    /**
     * Gets the layers that should be rendered behind the player
     * @return An int list of layer indices
     */
    public int[] getBackgroundLayers() {
        return backgroundLayers;
    }

    /**
     * Gets the layers that should be rendered in front of the player
     * @return An int list of layer indices
     */
    public int[] getForegroundLayers() {
        return foregroundLayers;
    }

    public OrthogonalTiledMapRenderer getCurrentMapRenderer(SpriteBatch spriteBatch) {
        if (currentMap == null) return null;

        if (!loadedMapRenderers.containsKey(currentMap)) {
            loadedMapRenderers.put(currentMap, new OrthogonalTiledMapRenderer(currentMap, spriteBatch));
        }

        return loadedMapRenderers.get(currentMap);
    }

    /**
     * DEBUG - Draws the hitboxes of colllsion and trigger objects
     * over the map.
     * Collision are red and triggers are blue
     */
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
     * Finds the nearest trigger the player is overlapping
     * @param playerHitbox The player's trigger hitbox
     * @return The MapProperties of the nearest trigger
     */
    public MapProperties getNearestTrigger(Rectangle playerHitbox) {
        if (triggerObjects == null) {
            return null;
        }
        playerHitbox = worldRectangleToPixelRectangle(playerHitbox);
        // The distance of the nearest trigger
        float closestDistance = -1f;
        MapProperties closestObject = null;

        for (MapObject object : triggerObjects) {
            if (object instanceof RectangleMapObject) {
                // Only calc if the trigger box overlaps the player
                Rectangle objRect = ((RectangleMapObject) object).getRectangle();
                if (playerHitbox.overlaps(objRect)) {
                    float distance = distanceBetween(objRect, playerHitbox);
                    if (closestObject == null || distance < closestDistance) {
                        closestObject = object.getProperties();
                        closestDistance = distance;
                    }
                }
            }
        }

        return closestObject;
    }

    /**
     * Renders any layers the map may have to the screen
     * @param batch The sprite batch to render to
     */
    public void renderLabels(SpriteBatch batch) {
        if (labelObjects == null) return;
        for (MapObject object : labelObjects) {
            MapProperties props = object.getProperties();
            float x = props.get("x", Float.class);
            float y = props.get("y", Float.class);

            labelFont.draw(batch, props.get("text", String.class), x, y);
        }
    }

    /**
     * Finds the coordinates of the spawn rectangle on the spawn layer
     * @return The spawn position to give to the player
     */
    public Vector2 getSpawnPoint() {
        MapLayer spawnLayer = currentMap.getLayers().get("Spawn");
        if (spawnLayer == null) {
            return new Vector2(0, 0);
        }

        for (MapObject object : spawnLayer.getObjects()) {
            // Return the coordinates of the first object
            MapProperties properties = object.getProperties();
            if (properties == null) return new Vector2(0, 0);

            return new Vector2(
                    pixelToWorldValue((float) properties.get("x")),
                    pixelToWorldValue((float) properties.get("y"))
            );
        }

        return new Vector2(0, 0);
    }

    /**
     * Returns the centre of the map in player's relative pixels
     * @return A 2D vector representing the centre of the map
     */
    public Vector2 getCentre() {
        if (currentMap == null) return null;

        MapProperties props = currentMap.getProperties();

        return new Vector2(
                worldToPixelValue(props.get("width", Integer.class) / 2f),
                worldToPixelValue(props.get("height", Integer.class) / 2f)
        );

    }

    /**
     * Calculates the distance between the centre of two rectangles
     * @param rect1 The first rectangle
     * @param rect2 The second rectangle
     * @return The distance between the centre of the two rectangles
     */
    private float distanceBetween(Rectangle rect1, Rectangle rect2) {
        Vector2 centres1 = new Vector2(rect1.width / 2 + rect1.x, rect1.height / 2 + rect1.y);
        Vector2 centres2 = new Vector2(rect2.width / 2 +  rect2.x, rect2.height / 2 + rect2.y);

        return (float) Math.sqrt((Math.pow((centres1.x - centres2.x), 2) + Math.pow((centres1.y - centres2.y), 2)));
    }


    /**
     * @return Returns the tile width and height of the current loaded map
     */
    public Vector2 getCurrentMapTileDimensions() {
        if (currentMap == null) {
            throw new NullPointerException("There is no currently loaded map!");
        }
        return new Vector2(
                (int)currentMap.getProperties().get("tilewidth"),
                (int)currentMap.getProperties().get("tileheight")
        );
    }

    /**
     * @return The dimensions of the current loaded map
     */
    public Vector2 getCurrentMapWorldDimensions() {
        if (currentMap == null) {
            throw new NullPointerException("There is no currently loaded map!");
        }
        return new Vector2(
                (int)currentMap.getProperties().get("width"),
                (int)currentMap.getProperties().get("height")
        );
    }

    /**
     * Takes a player's coordinates and translates it to map coordinates
     * @param worldCoords The player's coordinates
     * @return The equivalent of these coordinates in the maps coordinate system
     */
    public Vector2 worldToPixelCoords(Vector2 worldCoords) {
        return new Vector2(
                worldCoords.x * getCurrentMapTileDimensions().x,
                worldCoords.y * getCurrentMapTileDimensions().y
        );
    }

    /**
     * Converts a player's single coordinate to the map's coordinate
     * @param worldValue The value to translate
     * @return The translated value
     */
    public float worldToPixelValue(float worldValue) {
        return worldValue * getCurrentMapTileDimensions().x;
    }

    /**
     * Translates a world variable to the player's coordinate system
     * @param pixelValue The value to convert
     * @return The converted value
     */
    public float pixelToWorldValue(float pixelValue) {
        return pixelValue / getCurrentMapTileDimensions().x;
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
    public HashSet<Rectangle> getOverlappingRectangles(Rectangle playerRectangle) {
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
     * Correctly disposes of any loaded maps and any map renderers
     */
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
