package com.zierfisch.app;

public interface ApplicationListener {
	/**
	 * <p>
	 * Called before the first iteration of the main loop.
	 * </p>
	 * 
	 * <p>
	 * At this point, OpenGL is guaranteed to be available and a window is
	 * opened.
	 * </p>
	 */
	void enter();

	/**
	 * <p>
	 * Called after the window has been closed by the user or the
	 * {@link Application} has been scheduled for exiting.
	 * </p>
	 * 
	 * <p>
	 * NOTE: This method is never called if the application crashes or throws an
	 * exception.
	 * </p>
	 */
	void exit();

	/**
	 * <p>
	 * Called on each iteration of the main loop right before render.
	 * </p>
	 * 
	 * <p>
	 * The method is intended for updating the state of the application prior to presenting
	 * a frame.
	 * </p>
	 */
	void update();

	/**
	 * <p>
	 * Renders the current state of the application.
	 * </p>
	 */
	void render();
}
