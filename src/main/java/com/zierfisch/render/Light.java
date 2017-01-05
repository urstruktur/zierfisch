package com.zierfisch.render;

import java.awt.Color;
import com.badlogic.ashley.core.Component;

public class Light implements Component {
	float intensity;
	Color color;
	
	public Light(){
		color = new Color(1,1,1);
		intensity = 1f;
	}
}
