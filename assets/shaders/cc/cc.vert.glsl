#version 330

uniform mat4 u_model;

in vec3 position;

void main()
{
    gl_Position = u_model * vec4(position, 1.0);
}
