#version 330

layout(location = 0) in vec3 a_position;

void main()
{
    gl_Position.xyz = a_position;
    gl_Position.w = 1.0;
}
