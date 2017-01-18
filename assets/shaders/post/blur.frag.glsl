#version 330 core
out vec4 FragColor;
in vec2 TexCoords;

uniform sampler2D texture0;

uniform int horizontal;

//const float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);
const float weight[8] = float[] (0.5,0.4,0.3,0.227027, 0.1945946, 0.1216216, 0.154054, 0.116216);

// source: https://learnopengl.com/#!Advanced-Lighting/Bloom
void main()
{             
    vec2 tex_offset = 1.0 / textureSize(texture0, 0); // gets size of single texel
    vec3 result = texture(texture0, TexCoords).rgb * weight[0]; // current fragment's contribution
    if(horizontal == 1)
    {
        for(int i = 1; i < 8; ++i)
        {
            result += texture(texture0, TexCoords + vec2(tex_offset.x * i, 0.0)).rgb * weight[i];
            result += texture(texture0, TexCoords - vec2(tex_offset.x * i, 0.0)).rgb * weight[i];
        }
    }
    else
    {
        for(int i = 1; i < 8; ++i)
        {
            result += texture(texture0, TexCoords + vec2(0.0, tex_offset.y * i)).rgb * weight[i];
            result += texture(texture0, TexCoords - vec2(0.0, tex_offset.y * i)).rgb * weight[i];
        }
    }
 FragColor = vec4(result, 1.0);
  // FragColor = fract(texture(texture0, TexCoords));
}