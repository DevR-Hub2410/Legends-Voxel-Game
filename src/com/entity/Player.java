package com.entity;

import java.util.List;

import org.lwjgl.input.Keyboard;

import com.level.Level;
import com.phys.AABB;

public class Player {
	private final Level level;

	public double x;
	public double y;
	public double z;
	
	public double prevX;
	public double prevY;
	public double prevZ;
	
	public double motionX;
	public double motionY;
	public double motionZ;
	
	public float xRotation;
	public float yRotation;
	
	public boolean onGround;
	
	public AABB boundingBox;
	
	public Player(Level level) {
		this.level = level;
		
		resetPosition();
	}
	
	private void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		float width = 0.3F;
		float height = 0.9F;
		
		this.boundingBox = new AABB(x - width, y - height, z - width, x + width, y + height, z + width);
	}
	
	private void resetPosition() {
		float x = (float) Math.random() * level.width;
		float y = (float) (level.depth + 3);
		float z = (float) Math.random() * level.height;
		
		setPosition(x, y, z);
	}
	
	public void turn(float x, float y) {
		yRotation += x * 0.15F;
		xRotation -= y * 0.15F;
		
		xRotation = Math.max(-90.0F, xRotation);
		xRotation = Math.min(90.0F, xRotation);
	}
	
	public void tick() {
		prevX = x;
		prevY = y;
		prevZ = z;
		
		float forward = 0.0F;
		float vertical = 0.0F;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_R)) {
			resetPosition();
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			forward--;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			forward++;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			vertical--;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			vertical++;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			if(onGround) {
				motionY = 0.12F;
			}
		}
		
		moveRelative(vertical, forward, onGround ? 0.02F : 0.005F);
		motionY -= 0.005D;
		move(motionX, motionY, motionZ);
		
		motionX *= 0.91F;
		motionY *= 0.98F;
		motionZ *= 0.91F;
		
		if(onGround) {
			motionX *= 0.8F;
			motionZ *= 0.8F;
		}
	}
	
	public void move(double x, double y, double z) {
		double prevX = x;
		double prevY = y;
		double prevZ = z;
		
		List<AABB> aABBs = level.getCube(boundingBox.expand(x, y, z));
		
		for(AABB aabb : aABBs) {
			y = aabb.clipYCollide(boundingBox, y);
		}
		
		boundingBox.move(0.0F, y, 0.0F);
		
		for(AABB aabb : aABBs) {
			x = aabb.clipXCollide(boundingBox, x);
		}
		
		boundingBox.move(x, 0.0F, 0.0F);
		
		for(AABB aabb : aABBs) {
			z = aabb.clipZCollide(boundingBox, z);
		}
		
		boundingBox.move(0.0F, 0.0F, z);
		
		onGround = prevY != y && prevY < 0.0F;
		
		if(prevX != x) motionX = 0.0D;
		if(prevY != y) motionY = 0.0D;
		if(prevZ != z) motionZ = 0.0D;
		
		this.x = (boundingBox.x0 + boundingBox.x1) / 2.0D;
		this.y = boundingBox.y0 + 1.62D;
		this.z = (boundingBox.z0 + boundingBox.z1) / 2.0D;
	}
	
	private void moveRelative(float x, float z, float speed) {
		float distance = x * x + z * z;
		
		if(distance < 0.01F)
			return;
		
		distance = speed / (float) Math.sqrt(distance);
		x *= distance;
		z *= distance;
		
		double sin = Math.sin(Math.toRadians(yRotation));
		double cos = Math.cos(Math.toRadians(yRotation));
		
		motionX += x * cos - z * sin;
		motionZ += z * cos + x * sin;
	}
}
