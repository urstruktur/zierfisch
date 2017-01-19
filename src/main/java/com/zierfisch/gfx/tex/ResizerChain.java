package com.zierfisch.gfx.tex;

public class ResizerChain implements Resizer {

	private Resizer[] resizers;
	
	public ResizerChain(Resizer... resizers) {
		this.resizers = resizers;
	}

	@Override
	public void resize() {
		for(Resizer resizer: resizers) {
			resizer.resize();
		}
	}
	
}
