#version 330

uniform sampler2D texture0;

out vec4 color;

in vec2 fragTexCoords;
in vec4 fragPos;

void main()
{
	color = vec4(-fragPos.z*0.1f, -fragPos.z*0.02f, -fragPos.z*0.5f,1); //- (texture(texture0, vec2(fragTexCoords.x, 1.0-fragTexCoords.y))/8);
   // color = texture(texture0, vec2(fragTexCoords.x, 1.0-fragTexCoords.y));
}
