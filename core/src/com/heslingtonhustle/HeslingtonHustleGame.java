package com.heslingtonhustle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.heslingtonhustle.screens.*;
import com.heslingtonhustle.sound.SoundController;
import com.heslingtonhustle.sound.Sounds;
import com.heslingtonhustle.state.Activity;

import java.util.HashMap;

public class HeslingtonHustleGame extends Game {
	private Screen currentScreen;
	public int WIDTH;
	public int HEIGHT;

	public Skin skin;
	public SoundController soundController;


	/**
	 * Constructor, gets the width and height of the game window
	 * @param WIDTH Width of the game in pixels
	 * @param HEIGHT Height of the game in pixels
	 */
	public HeslingtonHustleGame (int WIDTH, int HEIGHT) {
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
	}

	/**
	 * Creates some needed classes and loads the game's UI skin
	 */
	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG); // Logs all messages to the console
		// Load skin
		skin = new Skin(Gdx.files.internal("Graphics/uiskin/uiskin.json"));

		soundController = new SoundController();

		changeScreen(AvailableScreens.LeaderboardScreen);
	}

	/**
	 * Changes the game's current screen to the provided screen
	 * @param availableScreens The screen to switch to
	 */
	public void changeScreen(AvailableScreens availableScreens) {
		if (currentScreen != null) {
			currentScreen.dispose();
		}
		switch (availableScreens) {
			case MenuScreen:
				currentScreen = new MenuScreen(this);
				soundController.setMusic(Sounds.MENU);
				break;
			case PlayScreen:
				currentScreen = new PlayScreen(this);
				soundController.setMusic(Sounds.GAME);
				break;
			case LeaderboardScreen:
				currentScreen = new LeaderboardScreen(this);
				soundController.setMusic(Sounds.MENU);
				break;

		}
		setScreen(currentScreen);
    }

	/**
	 * Specifically changes the screen to the game over screen,
	 * passing along information about the player's scoring info
	 * @param activitiesCompleted A hashmap containing instances of activities
	 *                            the player completed.
	 * @param stepAchievement A boolean value determining if the player
	 *                        has earned the 'walk at least x steps each day'
	 *                        achievement
	 */
	public void gameOver(HashMap<String, Activity> activitiesCompleted, boolean stepAchievement) {
		if (currentScreen != null) {
			currentScreen.dispose();
		}
		currentScreen = new GameOverScreen(this, activitiesCompleted, stepAchievement);
		setScreen(currentScreen);


	}
}
