package com.heslingtonhustle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.heslingtonhustle.screens.MenuScreen;
import com.heslingtonhustle.screens.PlayScreen;
import com.heslingtonhustle.screens.AvailableScreens;
import com.heslingtonhustle.sound.SoundController;
import com.heslingtonhustle.sound.Sounds;

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

		changeScreen(AvailableScreens.MenuScreen);
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
		}
		setScreen(currentScreen);
    }
}
