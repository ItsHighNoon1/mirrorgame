#version 440 core

in vec2 v_texCoords;

out vec4 color;

uniform sampler2D u_texture;

void main() {
	color = texture(u_texture, v_texCoords);
	if (color.a < 0.5) discard;
}