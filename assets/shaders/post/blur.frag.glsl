#version 330 core
out vec4 FragColor;
in vec2 TexCoords;

uniform sampler2D texture0;

uniform int horizontal;

//const float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);
const float weight[10] = float[] (0.220008, 0.188977, 0.119759, 0.055986, 0.019304, 0.004908, 0.00092, 0.000127, 0.000013, 0.000001);
// source: https://learnopengl.com/#!Advanced-Lighting/Bloom
void main()
{             
    vec2 tex_offset = 1.0 / textureSize(texture0, 0); // gets size of single texel
    vec3 result = texture(texture0, TexCoords).rgb * weight[0]; // current fragment's contribution
    if(horizontal == 1)
    {
        for(int i = 1; i < 10; ++i)
        {
            result += texture(texture0, TexCoords + vec2(tex_offset.x * i, 0.0)).rgb * weight[i];
            result += texture(texture0, TexCoords - vec2(tex_offset.x * i, 0.0)).rgb * weight[i];
        }
    }
    else
    {
        for(int i = 1; i < 10; ++i)
        {
            result += texture(texture0, TexCoords + vec2(0.0, tex_offset.y * i)).rgb * weight[i];
            result += texture(texture0, TexCoords - vec2(0.0, tex_offset.y * i)).rgb * weight[i];
        }
    }
 FragColor = vec4(result, 1.0);
  // FragColor = fract(texture(texture0, TexCoords));
}