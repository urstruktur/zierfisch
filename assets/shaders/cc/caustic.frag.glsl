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


/** Calculates intensity of diffuse light */
float diffuse(vec3 fragPosWorld, vec3 normalWorld, vec3 lightPosWorld) {
	float dist = length(lightPosWorld - fragPosWorld);

	// @see http://www.tomdalling.com/blog/modern-opengl/07-more-lighting-ambient-specular-attenuation-gamma/
	float att = 1 / (1 + attenuationFactor*dist*dist);

	vec3 lightDirWorld = normalize(lightPosWorld - fragPosWorld);
	float diffuse = clamp(dot(lightDirWorld, normalWorld), 0.0, 1.0);
	return att * diffuse;
}

///
/// Procedural texture that simulates caustics using angular functions and
/// the running time in seconds.
///
/// See: https://www.shadertoy.com/view/4ljXWh
///
vec3 caustic(vec2 uv)
{
	// Each iteration adds one sine/cosine combination to c and i, essentially making the caustics more complex
	const int iterations = 5;
	const float timeScale = 0.5;
	const float timeOffset = 23.5;
	const float piHalved = TAU / 4.0;

	// Scale uv coordinates by 2π but ensure the result  is in the interval [0,2π] using mod
	// modulo is just in case the uv coordinates where scaled before and are not in range [0,1]
	vec2 scaledUV = mod(uv*TAU, TAU);

	// Then, subtract 250 from scaledUV, yielding a number in range [-250,-243.716814]
    vec2 p = scaledUV - 250.0;

	// Calculate scaled time scaledT, which accounts for desired animation speed in timeScale
    float scaledT = time * timeScale + timeOffset;

	// I is a version of p that is offset with each iteration of the for-loop by vector
	// with its components in range [-1,1] that is returned from cosine and sine functions
	// that are parametrized with the last version of i
	// i then is used for manipulation of c
	vec2 i = vec2(p);
	float c = 1.0;
	float intensity = .005;

	// The loop prepares a c value that is then used to make a tilable
	// texture, with some pow and abs postprocessing
	// More iterations make for more complex/interesting patterns in c values
	for (int n = 0; n < MAX_ITER; n++)
	{
		float t = scaledT * (3.5 / float(n+1));
		i = p + sin(vec2(t + piHalved, t) - i.xy) +
		        sin(vec2(t, t + piHalved) + i.yx);

		c += 1.0/length(vec2(p.x / (sin(i.x+t)/intensity),p.y / (cos(i.y+t)/intensity)));
	}

	// Make brightness independent of iteration count
	c /= float(MAX_ITER);

	// This gives low numbers a brightness of 1 or slightly higher
	// and high numbers become negative at a little ofer 1
	c = abs(1.2-c);

	// When this number is decreased, there are more distinguishible shades of
	// brightness, decreasing it makes for more linear brightness distribution
	const float contrast = 8.0;

	vec3 color = vec3( pow(c, contrast) );

	// This apparently makes for more blueish/greenish colors by adding to
	// these two channels and then clamping to [0,1]
    color = clamp(color + vec3(0.0, 0.35, 0.5), 0.0, 1.0);

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

	//color *= 5.5;

	// -- EXTRACT BRIGHT PIXELS --

	float bloomThreshold = averageLuminosity * 3;  // factor seems a bit arbitrary, comes from the low life of averageLuminosity

	// calculate brigthness by applying luminosity contribution of rgb colors (see https://en.wikipedia.org/wiki/Relative_luminance)
	float brightness = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
	if(brightness > bloomThreshold)
        BrightColor = vec4(color.rgb, 1.0);
}
