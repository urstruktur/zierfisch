package com.zierfisch.render;

public class RenderSinks {
	private static final Surface PHYSICAL = new PhysicalSurface();
	
	public static final Surface physical() {
		return PHYSICAL;
	}
}
