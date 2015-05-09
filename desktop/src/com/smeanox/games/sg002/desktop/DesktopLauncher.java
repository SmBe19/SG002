package com.smeanox.games.sg002.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.smeanox.games.sg002.SG002;
import com.smeanox.games.sg002.util.Consts;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Consts.devWidth;
		config.height = Consts.devHeight;
		new LwjglApplication(new SG002(), config);
	}
}
