package us.itshighnoon.mirror;

import org.joml.Vector2f;

import us.itshighnoon.mirror.world.Wall;

public class Physics {
	public static void collide(Vector2f p, float size, Wall[] colliders) {
		// Since we are passing diameters 99% of the time, divide by 2
		
		size /= 2.0f;
		for (int i = 0; i < colliders.length; i++) {
			// Check if the ball is within the bounds of the line
			Vector2f a = colliders[i].getA();
			Vector2f b = colliders[i].getB();
			Vector2f ab = new Vector2f(b.x - a.x, b.y - a.y);
			Vector2f ba = new Vector2f(a.x - b.x, a.y - b.y);
			Vector2f ap = new Vector2f(p.x - a.x, p.y - a.y);
			Vector2f bp = new Vector2f(p.x - b.x, p.y - b.y);
			if (ab.dot(ap) > 0.0f && ba.dot(bp) > 0.0f) {
				// Check if the ball is touching the line
				Vector2f n = new Vector2f(ab.y, -ab.x);
				n.normalize();
				float depth = ap.dot(n);
				if (depth < 0.0f) {
					// Normal facing the wrong way
					n.mul(-1.0f);
					depth = -depth;
				}
				if (depth < size) {
					float correction = size - depth;
					p.x += n.x * correction;
					p.y += n.y * correction;
				}
			}
		}
		
		// Do a second check to move the object away from points since the above algorithm only catches edges
		float size2 = size * size;
		for (int i = 0; i < colliders.length; i++) {
			float dx1 = p.x - colliders[i].getA().x;
			float dy1 = p.y - colliders[i].getA().y;
			float dx2 = p.x - colliders[i].getB().x;
			float dy2 = p.y - colliders[i].getB().y;
			
			float aDist2 = dx1 * dx1 + dy1 * dy1;
			float bDist2 = dx2 * dx2 + dy2 * dy2;
			if (aDist2 < size2) {
				float aDist = (float)Math.sqrt(aDist2);
				float correction = size - aDist;
				p.x += correction * dx1 / aDist;
				p.y += correction * dy1 / aDist;
			} else if (bDist2 < size2) {
				float bDist = (float)Math.sqrt(bDist2);
				float correction = size - bDist;
				p.x += correction * dx2 / bDist;
				p.y += correction * dy2 / bDist;
			}
		}
	}
}
