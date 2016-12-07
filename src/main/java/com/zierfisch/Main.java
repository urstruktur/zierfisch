package com.zierfisch;

import com.zierfisch.app.Application;

public class Main {
	
	public static void main(String[] args) {
		Application app = new Application(new Zierfisch());
		app.setTitle("zierfisch");
		app.setWindowSize(1400, 730);
		app.run();
	}

}
