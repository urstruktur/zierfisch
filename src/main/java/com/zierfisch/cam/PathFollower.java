package com.zierfisch.cam;

import com.badlogic.ashley.core.Component;

public class PathFollower implements Component {
	public float speed = 0.018f;
	public float position = 0;
	
	public PathFollower(){}
	
	public PathFollower(PathFollower p){
		p.speed = speed;
		p.position = position;
	}
}
