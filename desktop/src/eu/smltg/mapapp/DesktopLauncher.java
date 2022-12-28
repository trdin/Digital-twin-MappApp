package eu.smltg.mapapp;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import eu.smltg.mapapp.DataVisualiserMap;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("data visualiser map");
		config.setWindowedMode(900, 900);
		config.setResizable(false);
		new Lwjgl3Application(new DataVisualiserMap(), config);
	}
}
