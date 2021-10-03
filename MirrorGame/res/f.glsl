#version 440 core

in vec2 v_texCoords;

out vec4 color;

void main() {
	color = vec4(v_texCoords.x, v_texCoords.y, v_texCoords.x / 2.0 + v_texCoords.y / 2.0, 1.0);
}