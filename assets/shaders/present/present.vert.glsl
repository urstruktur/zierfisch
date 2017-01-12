#version 330

in vec2 position;
in vec2 texCoords;

out vec2 st;

void main() {
    gl_Position = vec4(position, 0.0, 1.0);
    st = texCoords;
}
