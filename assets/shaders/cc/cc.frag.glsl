#version 330

uniform sampler2D texture0;

out vec4 color;

in vec2 fragTexCoords;

void main()
{
    color = texture(texture0, vec2(fragTexCoords.x, 1.0-fragTexCoords.y));
    color.rg = fragTexCoords;
    color.b = 0.0;
}
