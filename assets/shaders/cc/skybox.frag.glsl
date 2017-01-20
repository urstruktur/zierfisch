#version 330

uniform float averageLuminosity;

in vec4 fragPosWorld;

layout (location = 0) out vec4 color;
layout (location = 1) out vec4 BrightColor;

vec4 colorTop =  vec4(0.1,0.3,0.35,1.0);
//vec4 colorBottom = vec4(0.07,0.18,0.22,1.0);
vec4 colorBottom = vec4(0.02,0.18,0.2,1.0);

void main()
{
    color = mix(colorBottom, colorTop, normalize(fragPosWorld).y);
    
    // -- EXTRACT BRIGHT PIXELS --
	
	float bloomThreshold = averageLuminosity * 8;  // factor seems a bit arbitrary, comes from the low life of averageLuminosity
	
	// calculate brigthness by applying luminosity contribution of rgb colors (see https://en.wikipedia.org/wiki/Relative_luminance)
	float brightness = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
	if(brightness > bloomThreshold)
        BrightColor = vec4(color.rgb, 1.0);
}
