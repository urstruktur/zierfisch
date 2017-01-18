package com.zierfisch.audio;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class GhettoBlaster {

	private Clip clip;

	public void load(String wavFilePath) {
		if(!wavFilePath.toLowerCase().endsWith(".wav")) {
			throw new RuntimeException("Specify a .wav file please");
		}
		
		try {
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(wavFilePath));
			clip = AudioSystem.getClip();
			clip.open(audioIn);
		} catch (LineUnavailableException e) {
			throw new RuntimeException("Apparently, the line is unavailable? Not sure what this means to be honest.", e);
		} catch (IOException e) {
			throw new RuntimeException("Could not load audio file: " + wavFilePath, e);
		} catch (UnsupportedAudioFileException e) {
			throw new RuntimeException("Unsupported format or something", e);
		}
	}

	public void play() {
		Objects.requireNonNull(clip, "No clip loaded");
		clip.start();
	}
	
	public void pause() {
		Objects.requireNonNull(clip, "No clip loaded");
		clip.stop();
	}
}
