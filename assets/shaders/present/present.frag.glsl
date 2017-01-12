#version 330

uniform sampler2D content;

in vec2 st;

out vec4 color;

void main() {
    color = fract(texture(content, st));
}
