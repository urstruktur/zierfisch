#version 330

uniform mat4 u_model;

in vec3 position;
in vec2 texCoords;

out vec2 fragTexCoords;

void main()
{
    fragTexCoords = texCoords;
    gl_Position = u_model * vec4(position, 1.0);
}
