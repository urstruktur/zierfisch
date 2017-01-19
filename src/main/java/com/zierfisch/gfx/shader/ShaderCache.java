package com.zierfisch.gfx.shader;

import java.util.HashMap;
import java.util.Map;

public class ShaderCache {

	private Map<String, ShaderBuilder> shaderBuilders = new HashMap<>(16);
	
	public ShaderBuilder define(String key) {
		ShaderBuilder builder = new ShaderBuilder();
		shaderBuilders.put(key, builder);
		return builder;
	}
	
	public Shader get(String key) {
		ShaderBuilder builder = shaderBuilders.get(key);
		
		if(builder == null) {
			throw new IllegalStateException("No shader \"" +  key + "\" defined in cache");
		}
		
		return builder.build();
	}
	
}
