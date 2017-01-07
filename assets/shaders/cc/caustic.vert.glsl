#version 330

uniform mat4 u_model;
uniform mat4 u_view;
uniform mat4 u_projection;

in vec3 position;
in vec2 texCoords;

out vec2 fragTexCoords;
out vec4 fragPos;

void main()
{
	mat4 mv = u_view * u_model;
	mat4 mvp = u_projection * mv;

    fragTexCoords = texCoords;
    fragPos = mv * vec4(position, 1.0);
    normalize(fragPos);
    gl_Position = mvp * vec4(position, 1.0);
}
