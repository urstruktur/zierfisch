package com.zierfisch.gfx.surf;

public class PhysicalSurface extends AbstractSurface {
	
	private int width;
	private int height;
	
	public PhysicalSurface() {
		// Note that this is not guaranteed to work
		// Usually 0 is used for default rendering, but some platforms like iOS
		// are known to implement this differently.
		// An alternative aproach is to get the initially bound framebuffer, which
		// is actually already done in application but then never used.
		super(0);
	}
	
	/**
	 * PhysicalSurface depends on something regularly calling this. This is done
	 * by the Application for now.
	 * 
	 * @param width
	 * @param height
	 */
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
}
