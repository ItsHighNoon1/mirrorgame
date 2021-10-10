#version 440 core

in vec2 v_relPosition;

out vec4 color;

uniform sampler2D u_texture;
uniform vec2 u_absPosition;
uniform float u_camSize;

const vec2 wallOrigin = vec2(1.0, 3.0);
const vec2 wallVec = vec2(5.0, 5.0);

float cross2(vec2 a, vec2 b) {
	return a.x * b.y - a.y * b.x;
}

// https://youtu.be/c065KoXooSw
float intersect(vec2 a, vec2 c, vec2 r, vec2 s) {
	vec2 originDiff = c - a;
	
	float rCrossS = cross2(r * 2.0 * u_camSize, s);
	float u = cross2(originDiff, r) / rCrossS;
	float t = cross2(originDiff, s) / rCrossS;
	
	return (u > 1.0 || u < 0.0 || t > 1.0 || t < 0.0) ? -1.0 : t;
}

vec2 raytrace(vec2 origin, vec2 ray) {
	float intersectDist = intersect(origin, wallOrigin, ray, wallVec);
	if (intersectDist > -0.5) {
		vec2 normal = normalize(vec2(wallVec.y, -wallVec.x));
		vec2 newOrigin = origin + ray * intersectDist;
		vec2 newRay = reflect(ray * (1.0 - intersectDist), normal);
		origin = newOrigin;
		ray = newRay;
		return newOrigin + newRay;
	}
	return origin + ray;
}

void main() {
	vec2 samplePos = raytrace(u_absPosition, v_relPosition);
	color = texture(u_texture, samplePos - u_absPosition + 0.5);
	if (color.a < 0.5) discard;
}