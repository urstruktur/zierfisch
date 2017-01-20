#version 330

uniform mat4 u_model;
uniform mat4 u_view;
uniform mat4 u_projection;

in vec3 position;
in vec3 normal;
in vec2 texCoords;

out vec4 fragPosWorld;

void main()
{
	mat4 view_rot = u_view;
	view_rot[0].w = 0;
	view_rot[1].w = 0;
	view_rot[2].w = 0;
	view_rot[3] = vec4(0.0,0.0,0.0,1.0);

	mat4 vp = u_projection * view_rot;

	fragPosWorld = vec4(position, 1.0);

    gl_Position = vp * vec4(position, 1.0);
}
