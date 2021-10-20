package us.itshighnoon.mirror.world;

import org.joml.Vector2f;

import us.itshighnoon.mirror.lwjgl.object.TexturedModel;

public class Player extends Entity {
	private static final int MAX_HP = 3;
	private static final int MAX_AMMO = 40;
	private static final float I_FRAMES = 0.5f;
	
	private int hp;
	private int ammo;
	private float iFrames;
	
	public Player(TexturedModel model, Vector2f position, float rotation, float scale) {
		super(model, position, rotation, scale);
		hp = MAX_HP;
		ammo = MAX_AMMO;
		iFrames = 0.0f;
	}
	
	public int getHp() {
		return hp;
	}
	
	public void setHp(int hp) {
		this.hp = hp;
	}
	
	public void increaseHp(int dh) {
		if (dh < 0) {
			if (iFrames < 0.0f) {
				hp += dh;
				iFrames = I_FRAMES;
			}
		} else {
			hp += dh;
		}
		if (hp > MAX_HP) {
			hp = MAX_HP;
		} else if (hp < 0) {
			hp = 0;
		}
	}
	
	public int getAmmo() {
		return ammo;
	}
	
	public void setAmmo(int ammo) {
		this.ammo = ammo;
	}
	
	public void increaseAmmo(int da) {
		ammo += da;
		if (ammo > MAX_AMMO) {
			ammo = MAX_AMMO;
		} else if (ammo < 0) {
			ammo = 0;
		}
	}
	
	public void tick(float dt) {
		iFrames -= dt;
	}
}
