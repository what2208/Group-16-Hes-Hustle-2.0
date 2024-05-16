package io.skloch.heshustle.tests;

import org.junit.Test;
import com.badlogic.gdx.Gdx;

import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;

import com.heslingtonhustle.sound.SoundController;
import com.heslingtonhustle.screens.MenuScreen;
import com.heslingtonhustle.HeslingtonHustleGame;

@RunWith(GdxTestRunner.class)
public class AssetTests {
    // SoundController Asset tests
    @Test
    public void testMenuMusicAssetExists() {
        assertTrue("The asset for Menu Music exists",
                Gdx.files.internal(SoundController.MenuMusicAsset).exists());
    }

    @Test
    public void testGameMusicAssetExists() {
        assertTrue("The asset for Game Music exists",
                Gdx.files.internal(SoundController.GameMusicAsset).exists());
    }

    @Test
    public void testOpenSfxAssetExists() {
        assertTrue("The asset for the open sound effect exists",
                Gdx.files.internal(SoundController.OpenSfxAsset).exists());
    }

    @Test
    public void testCloseSfxAssetExists() {
        assertTrue("The asset for the close sound effect exists",
                Gdx.files.internal(SoundController.CloseSfxAsset).exists());
    }

    @Test
    public void testOptionSwitchSfxAssetExists() {
        assertTrue("The asset for the option switch sound effect exists",
                Gdx.files.internal(SoundController.OptionSwitchSfxAsset).exists());
    }

    @Test
    public void testConfirmSfxAssetExists() {
        assertTrue("The asset for the confirm sound effect effect exists",
                Gdx.files.internal(SoundController.ConfirmSfxAsset).exists());
    }

    // HeslingtonHustleGame assets
    @Test
    public void testUiSkinAssetExists() {
        assertTrue("The asset for the UI skin exists",
                Gdx.files.internal(HeslingtonHustleGame.UiSkinAsset).exists());
    }

}