#version 460 core

layout (location = 0) in vec2 a_position;
layout (location = 1) in vec2 a_texCoords;

out vec2 v_relPosition;
out float v_camScalar;

uniform float u_aspect;
uniform float u_camSize;

void main() {
	gl_Position = vec4(a_position.x * 2.0, a_position.y * 2.0, 0.0, 1.0);
	v_relPosition = vec2(a_position.x, a_position.y / u_aspect) * 2.0 * u_camSize;
	v_camScalar = 0.5 / u_camSize;
}