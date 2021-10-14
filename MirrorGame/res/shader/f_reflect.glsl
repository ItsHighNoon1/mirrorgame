#version 440 core

const int MAX_MIRRORS = 64;
const int MAX_WALLS = 64;
const int N_REFLECTIONS = 10;

in vec2 v_relPosition;
in float v_camScalar;

out vec4 color;

uniform sampler2D u_texture;
uniform vec4 u_mirrors[MAX_MIRRORS];
uniform vec4 u_walls[MAX_WALLS];
uniform vec2 u_absPosition;
uniform int u_nMirrors;
uniform int u_nWalls;

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

// calculates samplePos, but now main does that
/*
vec2 raytrace(vec2 origin, vec2 ray) {
	int lastReflection = -1;
	for (int i = 0; i < N_REFLECTIONS; i++) {
		bool nearestIsWall = false;
		float nearest = 2.0;
		vec2 hitNormal = vec2(0.0, 0.0);
		
		for (int j = 0; j < u_nWalls; j++) {
			vec2 wallOrigin = u_walls[j].xy;
			vec2 wallVec = u_walls[j].zw;
			float intersectDist = intersect(origin, wallOrigin, ray, wallVec);
			
			if (intersectDist < nearest) {
				nearestIsWall = true;
				nearest = intersectDist;
			}
		}
		
		int newReflection = -1;
		for (int j = 0; j < u_nMirrors; j++) {
			if (j == lastReflection) {
				continue;
			}
			
			vec2 mirrorOrigin = u_mirrors[j].xy;
			vec2 mirrorVec = u_mirrors[j].zw;
			float intersectDist = intersect(origin, mirrorOrigin, ray, mirrorVec);
			
			if (intersectDist < nearest) {
				nearestIsWall = false;
				newReflection = j;
				nearest = intersectDist;
				hitNormal = vec2(mirrorVec.y, -mirrorVec.x);
			}
		}
		
		if (nearest > 1.0) {
			break;
		}
		
		if (nearestIsWall) {
			return origin + ray * nearest;
		}
		
		lastReflection = newReflection;
		origin = origin + ray * nearest;
		ray = reflect(ray * (1.0 - nearest), normalize(hitNormal));
	}
	
	return origin + ray;
}
*/

void main() {
	vec2 origin = u_absPosition;
	vec2 ray = v_relPosition;
	
	int lastReflection = -1;
	for (int i = 0; i < N_REFLECTIONS; i++) {
		bool nearestIsWall = false;
		float nearest = 2.0;
		vec2 hitNormal = vec2(0.0, 0.0);
		
		for (int j = 0; j < u_nWalls; j++) {
			vec2 wallOrigin = u_walls[j].xy;
			vec2 wallVec = u_walls[j].zw;
			float intersectDist = intersect(origin, wallOrigin, ray, wallVec);
			
			if (intersectDist < nearest) {
				nearestIsWall = true;
				nearest = intersectDist;
			}
		}
		
		int newReflection = -1;
		for (int j = 0; j < u_nMirrors; j++) {
			if (j == lastReflection) {
				continue;
			}
			
			vec2 mirrorOrigin = u_mirrors[j].xy;
			vec2 mirrorVec = u_mirrors[j].zw;
			float intersectDist = intersect(origin, mirrorOrigin, ray, mirrorVec);
			
			if (intersectDist < nearest) {
				nearestIsWall = false;
				newReflection = j;
				nearest = intersectDist;
				hitNormal = vec2(mirrorVec.y, -mirrorVec.x);
			}
		}
		
		if (nearest > 1.0) {
			break;
		}
		
		if (nearestIsWall) {
			discard;
		}
		
		lastReflection = newReflection;
		origin = origin + ray * nearest;
		ray = reflect(ray * (1.0 - nearest), normalize(hitNormal));
	}
	
	color = texture(u_texture, (origin + ray - u_absPosition) * v_camScalar + 0.5);
}