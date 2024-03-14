package com.heslingtonhustle.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.heslingtonhustle.map.MapManager;
import com.heslingtonhustle.state.State;

public class Renderer implements Disposable {

    private int screenWidth;
    public int screenHeight;

    private final ExtendViewport viewport;
    private OrthographicCamera camera;
    private MapRenderer mapRenderer;
    private final State gameState;
    private SpriteBatch batch;
    private Sprite playerSprite;
    private TextureAtlas textureAtlas;

    private final MapManager mapManager;

    private final TextureRegion playerTexture;
    private final TextureRegion buildingTexture;

    public Renderer(State state, MapManager mapManager)
    {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        gameState = state;
        camera = new OrthographicCamera();
        this.mapManager = mapManager;
        mapManager.loadMap("Maps/fieldMap.tmx");
        TiledMap map = mapManager.getCurrentMap();
        batch = new SpriteBatch();
        mapRenderer = mapManager.getCurrentMapRenderer(batch);
        viewport = new ExtendViewport(screenWidth /2, screenHeight /2, camera);

        textureAtlas = new TextureAtlas("pack.atlas");
        playerTexture = textureAtlas.findRegion("circle");
        buildingTexture = textureAtlas.findRegion("triangle");

        playerSprite = new Sprite(playerTexture);
        playerSprite.setScale(0.5f, 0.5f);
    }

    public void update() {
        Vector2 playerPixelPosition = mapManager.worldToPixelCoords(gameState.getPlayerPosition());
        Vector2 clampedPlayerPosition = clampCoordsToScreen(playerPixelPosition);
        camera.position.set(clampedPlayerPosition, 0);

        viewport.update(screenWidth, screenHeight);
        batch.setProjectionMatrix(camera.combined);

        ScreenUtils.clear(0.2f, 0.2f, 0.5f, 1);

        mapRenderer = mapManager.getCurrentMapRenderer(batch); // Maybe change how this works
        mapRenderer.setView(camera);
        mapRenderer.render();

        playerSprite.setRotation(gameState.getPlayerFacing());
        playerSprite.setPosition(playerPixelPosition.x, playerPixelPosition.y);

        batch.begin();
        playerSprite.draw(batch);
        batch.draw(buildingTexture, 50, 50);
        batch.end();
    }

    private Vector2 clampCoordsToScreen(Vector2 coords) {
        float x = MathUtils.clamp(
                coords.x,
                camera.viewportWidth/2,
                mapManager.getCurrentMapPixelDimensions().x - camera.viewportWidth/2
        );
        float y = MathUtils.clamp(
                coords.y,
                camera.viewportHeight/2,
                mapManager.getCurrentMapPixelDimensions().y - camera.viewportHeight/2
        );
        return new Vector2(x, y);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public void windowResized(int width, int height) {
        screenWidth = width;
        screenHeight = height;
        viewport.update(width, height, true);
    }
}
