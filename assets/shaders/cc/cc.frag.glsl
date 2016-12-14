#version 330

uniform sampler2D texture0;

out vec4 color;

in vec2 fragTexCoords;

void main()
{
    color = texture(texture0, fragTexCoords);
}
