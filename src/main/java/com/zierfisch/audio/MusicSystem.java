package com.zierfisch.audio;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

public class MusicSystem extends EntitySystem {

	private static final String SOUNDTRACK_WAV_FILE = "assets/audio/LustmordSubspaceExcerpt.wav";
	
	private GhettoBlaster blaster;
	private boolean playing;
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		blaster = new GhettoBlaster();
		blaster.load(SOUNDTRACK_WAV_FILE);
		
		playing = false;
	}
	
	@Override
	public void update(float deltaTime) {
		if(!playing) {
			//blaster.play();
			playing = true;
		}
	}
	
}
