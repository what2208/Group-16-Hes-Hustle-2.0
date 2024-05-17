package com.heslingtonhustle;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.heslingtonhustle.HeslingtonHustleGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		int WIDTH = 1280;
		int HEIGHT = 720;
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setHdpiMode(HdpiMode.Logical);
		config.setForegroundFPS(60);
		config.setWindowedMode(WIDTH, HEIGHT);
		config.setWindowSizeLimits(WIDTH/3, HEIGHT/3, 1920, 1080);
		config.useVsync(true);
		config.setTitle("Heslington Hustle V2.0");

		// Icons
		config.setWindowIcon(Files.FileType.Internal, "Icons/icon_16x16.png");
		config.setWindowIcon(Files.FileType.Internal, "Icons/icon_32x32.png");
		config.setWindowIcon(Files.FileType.Internal, "Icons/icon_128x128.png");

		new Lwjgl3Application(new HeslingtonHustleGame(WIDTH, HEIGHT), config);
	}
}
