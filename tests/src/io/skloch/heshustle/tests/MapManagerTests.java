package io.skloch.heshustle.tests;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.heslingtonhustle.map.MapManager;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@RunWith(GdxTestRunner.class)
public class MapManagerTests {
    @Test
    public void testLoadMap() {
        MapManager mapManager = new MapManager();
        String path = "Maps/campusEast.tmx";
        HashMap<String, TiledMap> newLoadedMaps = new HashMap<>();
        TmxMapLoader mapLoader = new TmxMapLoader();
        newLoadedMaps.put(path, mapLoader.load(path));
        TiledMap newCurrentMap = newLoadedMaps.get(path);

        mapManager.loadMap(path);

        assertEquals(newLoadedMaps, mapManager.getLoadedMaps());
        assertEquals(newCurrentMap, mapManager.getCurrentMap());
    }
}
//public void loadMap(String path) {
//
//        // Get collidable objects
//        try {
//            MapLayer collisionLayer = currentMap.getLayers().get("Collisions");
//            collisionObjects = collisionLayer.getObjects();
//        } catch (NullPointerException e) {
//            Gdx.app.debug("DEBUG", "NO COLLISION LAYER FOUND!");
//        }
//
//        // Get triggerable objects
//        try {
//            MapLayer triggerLayer = currentMap.getLayers().get("Triggers");
//            triggerObjects = triggerLayer.getObjects();
//        } catch (NullPointerException e) {
//            Gdx.app.debug("DEBUG", "NO TRIGGER LAYER FOUND!");
//        }
//
//        // Get NPCs
//        try {
//            MapLayer triggerLayer = currentMap.getLayers().get("NPCs");
//            createNPCs(triggerLayer.getObjects());
//        } catch (NullPointerException e) {
//            Gdx.app.debug("DEBUG", "NO NPC LAYER FOUND!");
//        }
//
//        // Get labels
//        try {
//            MapLayer labelLayer = currentMap.getLayers().get("Labels");
//            labelObjects = labelLayer.getObjects();
//        } catch (NullPointerException e) {
//            labelObjects = null;
//        }
//
//        // Get which layers are foreground and which are background
//
//        IntArray foreground = new IntArray(true, currentMap.getLayers().getCount());
//        IntArray background = new IntArray(true, currentMap.getLayers().getCount());
//
//        // Foreground and background
//        for (int i=0; i < currentMap.getLayers().getCount(); i++) {
//            MapProperties properties = currentMap.getLayers().get(i).getProperties();
//            if (properties.containsKey("foreground")) {
//                // If foreground property is true
//                if ((boolean) properties.get("foreground")) {
//                    foreground.add(i);
//                } else {
//                    background.add(i);
//                }
//            } else {
//                background.add(i);
//            }
//        }
//
//        backgroundLayers = background.toArray();
//        foregroundLayers = foreground.toArray();
//
//    }