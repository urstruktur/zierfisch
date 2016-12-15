package com.zierfisch;

import com.zierfisch.app.Application;

public class Main {
	
	public static final int WINDOW_WIDTH = 1700;
	public static final int WINDOW_HEIGHT = 730;
	public static final float WINDOW_ASPECT = WINDOW_WIDTH/WINDOW_HEIGHT;
	
	public static void main(String[] args) {
		Application app = new Application(new Zierfisch());
		app.setTitle("zierfisch");
		app.setWindowSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		app.run();
	}

}
