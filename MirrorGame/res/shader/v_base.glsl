#version 460 core

layout (location = 0) in vec2 a_position;
layout (location = 1) in vec2 a_texCoords;

out vec2 v_texCoords;

uniform mat4 u_mvpMatrix;

void main() {
	gl_Position = u_mvpMatrix * vec4(a_position.x, a_position.y, 0.0, 1.0);
	v_texCoords = a_texCoords;
}