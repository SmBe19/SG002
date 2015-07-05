package com.smeanox.games.sg002.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.smeanox.games.sg002.SG002;
import com.smeanox.games.sg002.SG002NoGui;
import com.smeanox.games.sg002.nogui.InvalidConfigurationException;
import com.smeanox.games.sg002.util.Consts;
import com.smeanox.games.sg002.util.ProgramArguments;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.FileNotFoundException;

public class DesktopLauncher {
	public static void main(String[] arg) {
		if (!ProgramArguments.readArgs(arg)) {
			return;
		}

		if(ProgramArguments.noGUI){
			try {
				new SG002NoGui().start();
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
			} catch (InvalidConfigurationException e) {
				System.out.println(e.getMessage());
			}
		} else {
			Consts.textureFilter = Texture.TextureFilter.MipMapLinearLinear;

			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			config.width = Consts.devWidth * 2;
			config.height = Consts.devHeight * 2;
			if (ProgramArguments.fullScreen) {
				// move this line out of the if for production (only in fullscreen so window can be resized to test different screenSizes faster)
				config.samples = 32;

				config.fullscreen = true;

				GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
				config.width = gd.getDisplayMode().getWidth();
				config.height = gd.getDisplayMode().getHeight();
			}

			new LwjglApplication(new SG002(), config);
		}
	}
}
