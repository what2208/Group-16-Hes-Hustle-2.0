package io.skloch.heshustle.tests;

import com.heslingtonhustle.screens.*;
import org.junit.Test;
import com.badlogic.gdx.Gdx;

import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;

import com.heslingtonhustle.sound.SoundController;
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

    @Test
    public void testCreditsFilePath() {
        assertTrue("The text file for the credits exists",
                Gdx.files.internal(HeslingtonHustleGame.CreditsFilePath).exists());
    }

    // GameOverScreen assets
    @Test
    public void testgameOverFilePath() {
        assertTrue("The UI file for the Game Over screen exists",
                Gdx.files.internal(GameOverScreen.gameOverPath).exists());
    }

    @Test
    public void testGameOverTableBackgroundAsset() {
        assertTrue("The table background asset for the Game Over screen exists",
                Gdx.files.internal(GameOverScreen.gameOverTableBackgroundAsset).exists());
    }

    // AvatarSelectScreen assets
    @Test
    public void testAvatarMenuBackgroundAsset() {
        assertTrue("The background for the avatar selection screen exists",
                Gdx.files.internal(AvatarSelectScreen.avatarMenuBackgroundAsset).exists());
    }

    // CreditScreen assets
    @Test
    public void testCreditMenuBackgroundAsset() {
        assertTrue("The background for the credit screen exists",
                Gdx.files.internal(CreditScreen.creditMenuBackgroundAsset).exists());
    }

    // LeaderboardScreen assets
    @Test
    public void testLeaderboardBackgroundAsset() {
        assertTrue("The background for the leaderboard screen exists",
                Gdx.files.internal(LeaderboardScreen.leaderboardBackgroundAsset).exists());
    }

    // MenuScreen assets
    @Test
    public void testMenuBackgroundAsset() {
        assertTrue("The background for the menu screen exists",
                Gdx.files.internal(MenuScreen.menuBackgroundAsset).exists());
    }

    // OptionsScreen assets
    @Test
    public void testOptionsBackgroundAsset() {
        assertTrue("The background for the options screen exists",
                Gdx.files.internal(OptionsScreen.optionsBackgroundAsset).exists());
    }

    // TutorialScreen assets
    @Test
    public void testTutorialBackgroundAsset() {
        assertTrue("The background for the tutorial screen exists",
                Gdx.files.internal(TutorialScreen.tutorialBackgroundAsset).exists());
    }

    @Test
    public void testTutorialAsset1() {
        assertTrue("The first tutorial image asset exists",
                Gdx.files.internal(TutorialScreen.tutorialAsset1).exists());
    }

    @Test
    public void testTutorialAsset2() {
        assertTrue("The second tutorial image asset exists",
                Gdx.files.internal(TutorialScreen.tutorialAsset2).exists());
    }

}