package io.skloch.heshustle.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.heslingtonhustle.HeslingtonHustleGame;
import com.heslingtonhustle.screens.*;
import com.heslingtonhustle.sound.SoundController;
import com.heslingtonhustle.sound.Sounds;
import com.heslingtonhustle.state.Activity;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(GdxTestRunner.class)
public class HeslingtonHustleGameTests {
    @Test
    public void testCreate() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(300, 150);
        Screen newMenuScreen = new MenuScreen(game);
        Music newMusic = Gdx.audio.newMusic(Gdx.files.internal("Sound/Music/menuMusic.mp3"));

        game.create();

        assertEquals(Application.LOG_DEBUG, Gdx.app.getLogLevel());
        assertNull(game.getPreviousScreen());
        assertEquals(newMenuScreen, game.getCurrentScreen());
        assertEquals(newMusic, game.soundController.getCurrentMusic());
    }

    @Test
    public void testGameOver() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(300, 150);
        HashMap<String, Activity> activitiesCompleted = new HashMap<>();
        activitiesCompleted.put("sleep", new Activity("sleep", "sleep", 5, 0, 8, -1));
        boolean stepAchievement = true;
        Screen newScreen = new GameOverScreen(game, activitiesCompleted, stepAchievement);

        game.gameOver(activitiesCompleted, stepAchievement);

        assertEquals(newScreen, game.getCurrentScreen());
    }

    @Test
    public void testReadTextFile() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(300, 150);
        String filepath = "Text/credits.txt";

        assertEquals(Gdx.files.internal(filepath).readString(), game.readTextFile(filepath));
    }

    @Test
    public void testStartGame() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(300, 150);
        String player = "player-0";
        PlayScreen playScreen = new PlayScreen(game, player);
        Music gameMusic = Gdx.audio.newMusic(Gdx.files.internal(SoundController.GameMusicAsset));

        game.startGame(player);

        assertEquals(playScreen, game.getCurrentScreen());
        assertEquals(gameMusic, game.getSoundController().getCurrentMusic());
    }

    @Test
    public void testSwitchScreenWhenStoreDontPreviousMenuScreen() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(300, 150);
        MenuScreen menuScreen = new MenuScreen(game);
        Music gameMusic = Gdx.audio.newMusic(Gdx.files.internal(SoundController.MenuMusicAsset));

        game.create();
        game.switchScreen(AvailableScreens.MenuScreen, false);

        assertNull(game.getPreviousScreen());
        assertEquals(menuScreen, game.getCurrentScreen());
        assertEquals(gameMusic, game.getSoundController().getCurrentMusic());
    }

    @Test
    public void testSwitchScreenWhenStorePreviousLeaderboardScreen() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(300, 150);
        MenuScreen previousScreen = new MenuScreen(game);
        LeaderboardScreen leaderboardScreen = new LeaderboardScreen(game);
        Music gameMusic = Gdx.audio.newMusic(Gdx.files.internal(SoundController.MenuMusicAsset));

        game.create();
        game.switchScreen(AvailableScreens.LeaderboardScreen, true);

        assertEquals(previousScreen, game.getPreviousScreen());
        assertEquals(leaderboardScreen, game.getCurrentScreen());
        assertEquals(gameMusic, game.getSoundController().getCurrentMusic());
    }

    @Test
    public void testSwitchScreenWhenOptionsScreen() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(300, 150);
        OptionsScreen optionsScreen = new OptionsScreen(game);
        Music gameMusic = Gdx.audio.newMusic(Gdx.files.internal(SoundController.MenuMusicAsset));

        game.create();
        game.switchScreen(AvailableScreens.OptionsScreen, false);

        assertNull(game.getPreviousScreen());
        assertEquals(optionsScreen, game.getCurrentScreen());
        assertEquals(gameMusic, game.getSoundController().getCurrentMusic());
    }

    @Test
    public void testSwitchScreenWhenCreditScreen() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(300, 150);
        CreditScreen creditScreen = new CreditScreen(game);
        Music gameMusic = Gdx.audio.newMusic(Gdx.files.internal(SoundController.MenuMusicAsset));

        game.create();
        game.switchScreen(AvailableScreens.CreditScreen, false);

        assertNull(game.getPreviousScreen());
        assertEquals(creditScreen, game.getCurrentScreen());
        assertEquals(gameMusic, game.getSoundController().getCurrentMusic());
    }

    @Test
    public void testSwitchScreenWhenTutorialScreen() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(300, 150);
        TutorialScreen tutorialScreen = new TutorialScreen(game);

        game.create();
        game.switchScreen(AvailableScreens.TutorialScreen, false);

        assertNull(game.getPreviousScreen());
        assertEquals(tutorialScreen, game.getCurrentScreen());
    }

    @Test
    public void testSwitchScreenWhenAvatarSelectScreen() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(300, 150);
        AvatarSelectScreen avatarSelectScreen = new AvatarSelectScreen(game);

        game.create();
        game.switchScreen(AvailableScreens.AvatarSelectScreen, false);

        assertNull(game.getPreviousScreen());
        assertEquals(avatarSelectScreen, game.getCurrentScreen());
    }

    @Test
    public void testSwitchToPreviousScreenWhenNoPrevious() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(300, 150);
        MenuScreen menuScreen = new MenuScreen(game);
        game.create();

        game.switchToPreviousScreen(AvailableScreens.MenuScreen);

        assertNull(game.getPreviousScreen());
        assertEquals(menuScreen, game.getCurrentScreen());
    }

    @Test
    public void testSwitchToPreviousScreenWhenPreviousExists() {
        HeslingtonHustleGame game = new HeslingtonHustleGame(300, 150);
        TutorialScreen newCurrentScreen = new TutorialScreen(game);
        game.create();
        game.switchScreen(AvailableScreens.TutorialScreen, true);

        game.switchToPreviousScreen(AvailableScreens.MenuScreen);

        assertNull(game.getPreviousScreen());
        assertEquals(newCurrentScreen, game.getCurrentScreen());
    }
}