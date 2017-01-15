#version 330

uniform sampler2D texture0;	// diffuse
uniform sampler2D texture1;	// emission
uniform sampler2D texture4; // depth gradient
uniform float uvscale;
uniform bool emissionActive;

// the larger this factor, the faster atttenuation increases with distance from the light
const float attenuationFactor = 0.3f;
const int MAX_LIGHTS = 6;

struct Light {
	/** Light position in world space */
	vec4 position;
	/** rgb is color, a is intensity, if intensity 0.0, light has no effect */
	vec4 color;
};

uniform Light lights[MAX_LIGHTS];

out vec4 color;

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

	vec4 combinedLightColor = vec4(0.0, 0.0, 0.0, 1.0);

	for(int i = 0; i < MAX_LIGHTS; ++i) {
		if(lights[i].color.a > 0.0) {
			combinedLightColor.xyz += lights[i].color.xyz * diffuse(fragPosWorld.xyz, fragNormalWorld.xyz, lights[i].position.xyz);
		}
	}

	vec4 emission = texture(texture1, vec2(vec2(fragTexCoords.x, 1.0-fragTexCoords.y)*uvscale));
	vec4 diffuseColor = texture(texture0, vec2(fragTexCoords.x, 1.0-fragTexCoords.y)*uvscale);

	vec4 materialColor = combinedLightColor * diffuseColor + emission + vec4(0.1,0.2,0.22,1.0) * diffuseColor;

	color = mix(fogColor, materialColor, fogFactor);
	color.a = 1.0;
}
