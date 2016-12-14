#version 330

out vec4 color;

in vec2 fragTexCoords;

void main()
{
    color = vec4(fragTexCoords.s, 1.0, fragTexCoords.t, 1.0);
}
