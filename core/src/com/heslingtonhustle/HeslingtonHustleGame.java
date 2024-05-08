package com.heslingtonhustle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.heslingtonhustle.screens.*;
import com.heslingtonhustle.sound.SoundController;
import com.heslingtonhustle.sound.Sounds;
import com.heslingtonhustle.state.Activity;

import java.util.HashMap;

public class HeslingtonHustleGame extends Game {
	private Screen currentScreen;
	private Screen previousScreen;

	public int width;
	public int height;
	public Skin skin;
	public SoundController soundController;
	public String credits;


	/**
	 * Constructor, gets the width and height of the game window
	 * @param width Width of the game in pixels
	 * @param height Height of the game in pixels
	 */
	public HeslingtonHustleGame (int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Creates some needed classes and loads the game's UI skin
	 */
	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG); // Logs all messages to the console
		// Load skin
		skin = new Skin(Gdx.files.internal("Graphics/uiskin/uiskin.json"));

		credits = readTextFile("Text/credits.txt");

		soundController = new SoundController();

		switchScreen(AvailableScreens.MenuScreen, false);
	}


	/**
	 * Switches the game's screen to a new screen, while optionally storing
	 * a reference to the previous screen so it can be restored
	 * later.
	 * @param screen The new screen to switch to
	 * @param storePreviousScreen True if a reference to the old screen should
	 *                            be kept, use switchToPreviousScreen to restore
	 */
	public void switchScreen(AvailableScreens screen, boolean storePreviousScreen) {
		if (storePreviousScreen) {
			previousScreen = currentScreen;
		} else {
			previousScreen = null;
			if (currentScreen != null) {
				currentScreen.dispose();
			}
		}

		switch (screen) {
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
//				soundController.setMusic(Sounds.MENU);
				break;
			case OptionsScreen:
				currentScreen = new OptionsScreen(this);
//				soundController.setMusic(Sounds.MENU);
				break;
			case CreditScreen:
				currentScreen = new CreditScreen(this);
				soundController.setMusic(Sounds.MENU);
				break;
			case TutorialScreen:
				currentScreen = new TutorialScreen(this);
				soundController.setMusic(Sounds.MENU);
				break;
		}

		setScreen(currentScreen);
	}

	/**
	 * Attempts to switch to a previously already loaded screen.
	 * If no screen was previously loaded, switches to the provided screen
	 * instead.
	 * @param onNone The screen to switch to no previous screen is found
	 */
	public void switchToPreviousScreen(AvailableScreens onNone) {
		if (previousScreen != null) {
			if (currentScreen != null) {
				currentScreen.dispose();
			}
			currentScreen = previousScreen;
			setScreen(currentScreen);
			currentScreen.resume();
			previousScreen = null;
		} else {
			switchScreen(onNone, false);
		}
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

	/**
	 * A method to read strings from a text file to be used/displayed elsewhere.
	 * @param filepath The directory of the file
	 * @return The string contained in the file
	 */
	public String readTextFile(String filepath) {
		FileHandle file = Gdx.files.internal(filepath);

		if (!file.exists()) {
			System.out.println("WARNING: Couldn't load file " + filepath);
			return "Couldn't load " + filepath;
		} else {
			return file.readString();
		}

	}
}
