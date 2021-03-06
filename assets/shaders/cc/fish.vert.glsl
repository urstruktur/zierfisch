#version 330

uniform mat4 u_model;
uniform mat4 u_view;
uniform mat4 u_projection;

in vec3 position;
in vec3 normal;
in vec2 texCoords;

out vec2 fragTexCoords;
out vec4 fragPos;
out vec4 fragPosWorld;
out vec4 fragNormalWorld;

void main()
{
	mat4 mv = u_view * u_model;
	mat4 mvp = u_projection * mv;

    fragTexCoords = texCoords;
    fragPos = mv * vec4(position, 1.0);
	fragPosWorld = u_model * vec4(position, 1.0);
	fragNormalWorld = u_model * vec4(normal, 0.0);

    gl_Position = mvp * vec4(position, 1.0);
}
