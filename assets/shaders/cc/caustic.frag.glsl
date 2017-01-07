#version 330

#define TAU 6.28318530718
#define MAX_ITER 5

uniform sampler2D texture0;

out vec4 color;

in vec2 fragTexCoords;
in vec4 fragPos;

// source: https://www.shadertoy.com/view/4ljXWh
vec3 caustic(vec2 uv)
{
    vec2 p = mod(uv*TAU, TAU)-250.0;
    float time = fragPos.y * .5+23.0;//iGlobalTime * .5+23.0;

	vec2 i = vec2(p);
	float c = 1.0;
	float inten = .005;

	for (int n = 0; n < MAX_ITER; n++) 
	{
		float t = time * (1.0 - (3.5 / float(n+1)));
		i = p + vec2(cos(t - i.x) + sin(t + i.y), sin(t - i.y) + cos(t + i.x));
		c += 1.0/length(vec2(p.x / (sin(i.x+t)/inten),p.y / (cos(i.y+t)/inten)));
	}
    
	c /= float(MAX_ITER);
	c = 1.17-pow(c, 1.4);
	vec3 color = vec3(pow(abs(c), 8.0));
    color = clamp(color + vec3(0.0, 0.35, 0.5), 0.0, 1.0);
    color = mix(color, vec3(1.0,1.0,1.0),0.3);
    
    return color;
}

void main()
{
	color = vec4(caustic(vec2(fragPos.x,fragPos.z)*0.1),1);
   // color = texture(texture0, vec2(fragTexCoords.x, 1.0-fragTexCoords.y));
}
