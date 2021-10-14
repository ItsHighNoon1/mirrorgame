#version 440 core

const int MAX_MIRRORS = 64;
const int N_REFLECTIONS = 10;

in vec2 v_relPosition;
in float v_camScalar;

out vec4 color;

uniform sampler2D u_texture;
uniform vec4 u_mirrors[MAX_MIRRORS];
uniform vec2 u_absPosition;
uniform int u_nMirrors;

float cross2(vec2 a, vec2 b) {
	return a.x * b.y - a.y * b.x;
}

// https://youtu.be/c065KoXooSw
float intersect(vec2 a, vec2 c, vec2 r, vec2 s) {
	vec2 originDiff = c - a;
	
	float rCrossS = cross2(r, s);
	float u = cross2(originDiff, r) / rCrossS;
	float t = cross2(originDiff, s) / rCrossS;
	
	return (u > 1.0 || u < 0.0 || t > 1.0 || t < 0.0) ? 2.0 : t;
}

vec3 raytrace(vec2 origin, vec2 ray) {
	float debug = 0.0;
	int lastReflection = -1;
	for (int i = 0; i < N_REFLECTIONS; i++) {
		float nearestMirror = 2.0;
		vec2 hitNormal = vec2(0.0, 0.0);
		
		int newReflection = -1;
		for (int j = 0; j < u_nMirrors; j++) {
			if (j == lastReflection) {
				continue;
			}
			
			vec2 mirrorOrigin = u_mirrors[j].xy;
			vec2 mirrorVec = u_mirrors[j].zw;
			float intersectDist = intersect(origin, mirrorOrigin, ray, mirrorVec);
			
			if (intersectDist < nearestMirror) {
				newReflection = j;
				nearestMirror = intersectDist;
				hitNormal = vec2(mirrorVec.y, -mirrorVec.x);
			}
		}
		
		if (nearestMirror > 1.0) {
			break;
		}
		
		lastReflection = newReflection;
		origin = origin + ray * nearestMirror;
		ray = reflect(ray * (1.0 - nearestMirror), normalize(hitNormal));
	}
	
	return vec3(origin + ray, debug);
}

void main() {
	vec3 samplePos = raytrace(u_absPosition, v_relPosition);
	samplePos.xy -= u_absPosition;
	samplePos *= v_camScalar;
	color = texture(u_texture, samplePos.xy + 0.5);
	color.z += samplePos.z / v_camScalar;
	if (color.a < 0.5) discard;
}