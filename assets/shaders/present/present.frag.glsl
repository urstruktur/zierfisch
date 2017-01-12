#version 330

uniform sampler2D texture0;
uniform sampler2D texture1;

in vec2 st;

out vec4 color;

void main() {
    color = fract(texture(texture0, st) + texture(texture1, st));
}
