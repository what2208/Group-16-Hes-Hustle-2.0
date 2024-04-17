package com.heslingtonhustle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.heslingtonhustle.screens.MenuScreen;
import com.heslingtonhustle.screens.PlayScreen;
import com.heslingtonhustle.screens.AvailableScreens;

public class HeslingtonHustleGame extends Game {
	private Screen currentScreen;
	public int WIDTH;
	public int HEIGHT;

	public Skin skin;


	/**
	 * Constructor, gets the width and height of the game window
	 * @param WIDTH
	 * @param HEIGHT
	 */
	public HeslingtonHustleGame (int WIDTH, int HEIGHT) {
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
	}

	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG); // Logs all messages to the console
		skin = new Skin(Gdx.files.internal("Graphics/uiskin/uiskin.json"));

		changeScreen(AvailableScreens.MenuScreen);
	}

	public void changeScreen(AvailableScreens availableScreens) {
		if (currentScreen != null) {
			currentScreen.dispose();
		}
		switch (availableScreens) {
			case MenuScreen:
				currentScreen = new MenuScreen(this);
				break;
			case PlayScreen:
				currentScreen = new PlayScreen(this);
				break;
		}
		setScreen(currentScreen);
    }
}
