package io.skloch.heshustle.tests;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.screens.PlayScreen;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static org.junit.Assert.*;

@RunWith(GdxTestRunner.class)
public class PlayScreenTests {
    @Test
    public void testPause() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(600,400);
        String player = "player-0";
        PlayScreen playScreen = new PlayScreen(game, player);

        playScreen.pause();

        assertTrue(playScreen.isPaused());
        assertTrue(playScreen.getPauseMenu().isVisible());
    }

    @Test
    public void testUnpause() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(600,400);
        String player = "player-0";
        PlayScreen playScreen = new PlayScreen(game, player);

        playScreen.pause();
        playScreen.unPause();

        assertFalse(playScreen.isPaused());
        assertFalse(playScreen.getPauseMenu().isVisible());
    }

    @Test
    public void testResumeWhenPauseMenuIsVisible() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(600,400);
        String player = "player-0";
        PlayScreen playScreen = new PlayScreen(game, player);
        playScreen.getPauseMenu().showPauseMenu();

        playScreen.resume();

        assertTrue(playScreen.isPaused());
    }

    @Test
    public void testResumeWhenPauseMenuIsNotVisible() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(600,400);
        String player = "player-0";
        PlayScreen playScreen = new PlayScreen(game, player);
        playScreen.getPauseMenu().hidePauseMenu();

        playScreen.resume();

        assertFalse(playScreen.isPaused());
    }

    @Test
    public void testResize() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(600,400);
        String player = "player-0";
        PlayScreen playScreen = new PlayScreen(game, player);
        int newWidth = 550;
        int newHeight = 450;

        playScreen.resize(newWidth, newHeight);

        assertEquals(newWidth, playScreen.getHudRenderer().getViewport().getScreenWidth());
        assertEquals(newHeight, playScreen.getHudRenderer().getViewport().getScreenHeight());
    }

    @Test
    public void testPlayScreenLoadsInitialMap() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(600,400);
        String player = "player-0";
        PlayScreen playScreen = new PlayScreen(game, player);
        HashMap<String, TiledMap> newLoadedMaps = new HashMap<>();
        TmxMapLoader mapLoader = new TmxMapLoader();
        newLoadedMaps.put(PlayScreen.campusEastMapPath, mapLoader.load(PlayScreen.campusEastMapPath));

        assertEquals(newLoadedMaps, playScreen.getMapManager().getLoadedMaps());
    }

    @Test
    public void testChangeMap() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(600,400);
        String player = "player-0";
        PlayScreen playScreen = new PlayScreen(game, player);
        MapProperties trigger = new MapProperties();
        String key = "new_map";
        String text = "urbanMap.tmx";
        trigger.put(key, text);
        playScreen.getState().setNearestTrigger(trigger);
        playScreen.getState().handleInteraction();
        String newPath = "Maps/urbanMap.tmx";
        TmxMapLoader tmxMapLoader = new TmxMapLoader();
        TiledMap newMap = tmxMapLoader.load(newPath);

        playScreen.changeMap(trigger);

        assertEquals(newMap, playScreen.getMapManager().getCurrentMap());
    }
}
//    public void changeMap(MapProperties currentTrigger) {
//        mapManager.loadMap("Maps/" + currentTrigger.get("new_map"));
//        player.setPosition(new Vector2(
//                (float) currentTrigger.get("new_map_x"),
//                (float) currentTrigger.get("new_map_y")));
//        Vector2 playerPixelPosition = mapManager.worldToPixelCoords(player.getPosition());
//        camera.position.set(
//            camera.position.set(
//                new Vector3(
//                        playerPixelPosition.x + (mapManager.worldToPixelValue(player.getPlayerWidth())/2),
//                        playerPixelPosition.y + (mapManager.worldToPixelValue(player.getPlayerHeight())/2),
//                        0
//                )
//            )
//        );
//    }