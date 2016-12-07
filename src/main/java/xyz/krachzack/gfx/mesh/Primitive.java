package xyz.krachzack.gfx.mesh;

public enum Primitive {
	TRIANGLES(3, 3),
	LINES(2, 2);
	
	public final int width;
	public final int increment;

	private Primitive(int width, int increment) {
		this.width = width;
		this.increment = increment;
	}
}
