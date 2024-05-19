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