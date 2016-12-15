#version 330

uniform mat4 u_model;
uniform mat4 u_view;
uniform mat4 u_projection;

in vec3 position;
in vec2 texCoords;

out vec2 fragTexCoords;

void main()
{
	mat4 mvp = u_projection * u_view * u_model;

    fragTexCoords = texCoords;
    gl_Position = mvp * vec4(position, 1.0);
}
