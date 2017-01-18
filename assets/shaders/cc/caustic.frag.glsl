#version 330

#define TAU 6.28318530718
#define MAX_ITER 5

uniform float time;

uniform sampler2D texture0;	// diffuse
uniform sampler2D texture4; // depth gradient
uniform float uvscale;
uniform float averageLuminosity;

// the larger this factor, the faster atttenuation increases with distance from the light
const float attenuationFactor = 0.95f;
const int MAX_LIGHTS = 6;

struct Light {
	/** Light position in world space */
	vec4 position;
	/** rgb is color, a is intensity, if intensity 0.0, light has no effect */
	vec4 color;
};

uniform Light lights[MAX_LIGHTS];

layout (location = 0) out vec4 color;
layout (location = 1) out vec4 BrightColor;  

in vec2 fragTexCoords;
in vec4 fragPos; // view space
in vec4 fragPosWorld;
in vec4 fragNormalWorld;

const float FOGDENSITY = 0.15;
const int FOGSTART = 1;
const int FOGEND = 60;

float fogFactor = 0;
vec4 fogColor = vec4(0.5f,0.5f,0.5f,1.0f);

float bloomThreshold = 2.5;

/** Calculates intensity of diffuse light */
float diffuse(vec3 fragPosWorld, vec3 normalWorld, vec3 lightPosWorld) {
	float dist = length(lightPosWorld - fragPosWorld);

	// @see http://www.tomdalling.com/blog/modern-opengl/07-more-lighting-ambient-specular-attenuation-gamma/
	float att = 1 / (1 + attenuationFactor*dist*dist);

	vec3 lightDirWorld = normalize(lightPosWorld - fragPosWorld);
	float diffuse = clamp(dot(lightDirWorld, normalWorld), 0.0, 1.0);
	return att * diffuse;
}

// source: https://www.shadertoy.com/view/4ljXWh
vec3 caustic(vec2 uv)
{
    vec2 p = mod(uv*TAU, TAU)-250.0;
    float offset = time * .5+23.0; //time * .5+23.0;

	vec2 i = vec2(p);
	float c = 1.0;
	float inten = .005;

	for (int n = 0; n < MAX_ITER; n++)
	{
		float t = offset * (1.0 - (3.5 / float(n+1)));
		i = p + vec2(cos(t - i.x) + sin(t + i.y), sin(t - i.y) + cos(t + i.x));
		c += 1.0/length(vec2(p.x / (sin(i.x+t)/inten),p.y / (cos(i.y+t)/inten)));
	}

	c /= float(MAX_ITER);
	c = 1.17-pow(c, 1.4);
	vec3 color = vec3(pow(abs(c), 8.0));
    color = clamp(color + vec3(0.0, 0.35, 0.5), 0.0, 1.0);
    //color = mix(color, vec3(1.0,1.0,1.0),0.3);

    return color;
}

/**
* calculates to the relationship between normal and up vector
* 1 if perpendicular
**/
float upness(vec3 normalWorld) {
	vec3 up = vec3(0.0,1.0,0.0);
	float i = clamp(dot(up, normalWorld), 0.0, 1.0);
	return i;
}

void main()
{
	// -- FOG --

	// calculate length from camera to fragment to get range based fog (not plane based)
	float distance = length(fragPos);

	// exponential fog
	// fogFactor = 1.0/exp(distance*FOGDENSITY);

	// linear fog (used because exponential fog would distort texture gradient fog)
	fogFactor = (FOGEND - distance)/(FOGEND - FOGSTART); // 0 = yo fog, 1 = no fog
	fogFactor = clamp(fogFactor, 0.0, 1.0);

	// texture gradient fog
	fogColor = texture(texture4, vec2(clamp(1.0-fogFactor,0.01,0.99),0.1)); // clamp is a fix for the problem that edge pixels are grey (?)
	fogFactor = 1.0 - fogColor.a;

	// -- LIGHT --

	vec4 combinedLightColor = vec4(0.0, 0.0, 0.0, 1.0);

	for(int i = 0; i < MAX_LIGHTS; ++i) {
		if(lights[i].color.a > 0.0) {
			combinedLightColor.xyz += lights[i].color.a * (lights[i].color.xyz * diffuse(fragPosWorld.xyz, fragNormalWorld.xyz, lights[i].position.xyz));
		}
	}

	float causticIntensity = upness(vec3(fragNormalWorld.x,fragNormalWorld.y,fragNormalWorld.z));

	// fake chromatic aberration
	float offset = 0.0005;
	float r = vec3(caustic(vec2(fragTexCoords.x-offset,1.0-fragTexCoords.y+offset)*uvscale)).x;
	float g = vec3(caustic(vec2(fragTexCoords.x,1.0-fragTexCoords.y)*uvscale)).y;
	float b = vec3(caustic(vec2(fragTexCoords.x+offset,1.0-fragTexCoords.y-offset)*uvscale)).z;
	vec3 causticColor = vec3(r,g,b) * causticIntensity;

	combinedLightColor += vec4(causticColor,1.0);

	vec4 materialColor = combinedLightColor * texture(texture0, vec2(fragTexCoords.x, 1.0-fragTexCoords.y)*uvscale);

	// using photoshop screen formula (negativ multiplizieren): 1-(1-A)*(1-B)
	//materialColor = vec4(vec3(1.0) - (vec3(1.0) - vec3(materialColor)) * (vec3(1.0) - causticColor),1.0);

	color = mix(fogColor, materialColor, fogFactor);
	color.a = 1.0;
	
	// -- EXTRACT BRIGHT PIXELS --
	
	// calculate brigthness by applying luminosity contribution of rgb colors (see https://en.wikipedia.org/wiki/Relative_luminance)
	float brightness = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
	if(brightness/averageLuminosity > bloomThreshold)
        BrightColor = vec4(color.rgb/averageLuminosity, 1.0);
}
