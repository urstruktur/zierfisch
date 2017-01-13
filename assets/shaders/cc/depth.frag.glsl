#version 330

uniform sampler2D texture0;
uniform sampler2D texture4;
uniform float uvscale;

const int MAX_LIGHTS = 6;

struct Light {
	/** Light position in world space */
	vec4 position;
	/** rgb is color, a is intensity, if intensity 0.0, light has no effect */
	vec4 color;
};

uniform Light lights[MAX_LIGHTS];

out vec4 color;

in vec4 viewSpace;
in vec2 fragTexCoords;
in vec4 fragPos; // view space
in vec4 fragPosWorld;

const float FOGDENSITY = 0.15;
const int FOGSTART = 1;
const int FOGEND = 60;

float fogFactor = 0;
vec4 fogColor = vec4(0.5f,0.5f,0.5f,1.0f);

void main()
{
	// light dimmed for testing
	vec4 lightColor = texture(texture0, vec2(fragTexCoords.x, 1.0-fragTexCoords.y)*uvscale) / 6;


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

	for(int i = 0; i < MAX_LIGHTS; ++i) {
		if(lights[i].color.a > 0.0) {
			color = lights[i].color;
		}
	}

	color = mix(fogColor, lightColor, fogFactor);
}
