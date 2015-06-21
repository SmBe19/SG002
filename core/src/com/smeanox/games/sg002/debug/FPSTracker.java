package com.smeanox.games.sg002.debug;

/**
 * Allows to track the FPS
 * @author Benjamin Schmid
 */
public class FPSTracker {

	private static FPSTracker singleton;

	private float timePassed;
	private float framesRendered;
	private float printEveryXSecond;

	private FPSTracker(){
		timePassed = 0;
		framesRendered = 0;
		printEveryXSecond = 10;
	}

	public static FPSTracker get(){
		if(singleton == null){
			singleton = new FPSTracker();
		}
		return singleton;
	}

	public void frame(float delta){
		timePassed += delta;
		framesRendered++;
		if(timePassed > printEveryXSecond){
			System.out.println("FPS: " + (framesRendered / timePassed));
			framesRendered = 0;
			timePassed = 0;
		}
	}

	public float getPrintEveryXSecond() {
		return printEveryXSecond;
	}

	public void setPrintEveryXSecond(float printEveryXSecond) {
		this.printEveryXSecond = printEveryXSecond;
	}
}
