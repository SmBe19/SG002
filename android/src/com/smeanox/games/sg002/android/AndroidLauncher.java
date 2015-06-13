package com.smeanox.games.sg002.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.smeanox.games.sg002.SG002;
import com.smeanox.games.sg002.util.Consts;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Consts.textureFilter = Texture.TextureFilter.Nearest; //To enable texture filtering on Android change this line to:
		Consts.textureFilter = Texture.TextureFilter.MipMapLinearLinear;

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.numSamples = 2;
		initialize(new SG002(), config);
	}
}
