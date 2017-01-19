#version 330

in vec4 fragPosWorld;

out vec4 color;

vec4 colorTop =  vec4(0.09,0.24,0.29,1.0);
vec4 colorBottom = vec4(0.07,0.18,0.22,1.0);

void main()
{
    color = mix(colorBottom, colorTop, normalize(fragPosWorld).y);
}
