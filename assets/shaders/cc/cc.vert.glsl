#version 330

layout(location = 0) in vec3 position;

void main()
{
    gl_Position.xy = position.xy;
    gl_Position.z = 0.0;
    gl_Position.w = 1.0;
}
