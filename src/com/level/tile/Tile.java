package com.level.tile;

import com.gfx.Tesellator;
import com.level.Level;

public class Tile {
	public static Tile grass = new Tile(0);
	public static Tile rock = new Tile(1);
	
	private final int id;
	
	public Tile(int id) {
		this.id = id;
	}
	
	public void render(Tesellator t, Level level, int layer, int x, int y, int z) {
		float u0 = id / 16.0F;
		float v0 = 0.0F;
		float u1 = u0 + 16.0F / 256.0F;
		float v1 = v0 + 16.0F / 256.0F;
		
		float shadeX = 0.6F;
		float shadeY = 1.0F;
		float shadeZ = 0.8F;
		
		float x0 = x + 0.0F;
		float y0 = y + 0.0F;
		float z0 = z + 0.0F;
		float x1 = x + 1.0F;
		float y1 = y + 1.0F;
		float z1 = z + 1.0F;
		
		if(!level.isSolidTile(x, y - 1, z)) {
			float brightness = level.getBrightness(x, y - 1, z) * shadeY;
			
			if(layer == 1 ^ brightness == shadeY) {
				t.color(brightness, brightness, brightness);
				t.texture(u0, v1);
				t.vertex(x0, y0, z1);
				t.texture(u0, v0);
				t.vertex(x0, y0, z0);
				t.texture(u1, v0);
				t.vertex(x1, y0, z0);
				t.texture(u1, v1);
				t.vertex(x1, y0, z1);
			}
		}
		if(!level.isSolidTile(x, y + 1, z)) {
			float brightness = level.getBrightness(x, y + 1, z) * shadeY;
			if(layer == 1 ^ brightness == shadeY) {
				t.color(brightness, brightness, brightness);
				t.texture(u1, v1);
				t.vertex(x1, y1, z1);
				t.texture(u1, v0);
				t.vertex(x1, y1, z0);
				t.texture(u0, v0);
				t.vertex(x0, y1, z0);
				t.texture(u0, v1);
				t.vertex(x0, y1, z1);
			}
		}
		
		if(!level.isSolidTile(x, y, z - 1)) {
			float brightness = level.getBrightness(x, y, z - 1) * shadeZ;
			if(layer == 1 ^ brightness == shadeZ) {
				t.color(brightness, brightness, brightness);
				t.texture(u1, v0);
				t.vertex(x0, y1, z0);
				t.texture(u0, v0);
				t.vertex(x1, y1, z0);
				t.texture(u0, v1);
				t.vertex(x1, y0, z0);
				t.texture(u1, v1);
				t.vertex(x0, y0, z0);
			}
		}
		if(!level.isSolidTile(x, y, z + 1)) {
			float brightness = level.getBrightness(x, y, z + 1) * shadeZ;
			if(layer == 1 ^ brightness == shadeZ) {
				t.color(brightness, brightness, brightness);
				t.texture(u0, v0);
				t.vertex(x0, y1, z1);
				t.texture(u0, v1);
				t.vertex(x0, y0, z1);
				t.texture(u1, v1);
				t.vertex(x1, y0, z1);
				t.texture(u1, v0);
				t.vertex(x1, y1, z1);
			}
		}
		if(!level.isSolidTile(x - 1, y, z)) {
			float brightness = level.getBrightness(x - 1, y, z) * shadeX;
			if(layer == 1 ^ brightness == shadeX) {
				t.color(brightness, brightness, brightness);
				t.texture(u1, v0);
				t.vertex(x0, y1, z1);
				t.texture(u0, v0);
				t.vertex(x0, y1, z0);
				t.texture(u0, v1);
				t.vertex(x0, y0, z0);
				t.texture(u1, v1);
				t.vertex(x0, y0, z1);
			}
		}
		if(!level.isSolidTile(x + 1, y, z)) {
			float brightness = level.getBrightness(x + 1, y, z) * shadeX;
			if(layer == 1 ^ brightness == shadeX) {
				t.color(brightness, brightness, brightness);
				t.texture(u0, v1);
				t.vertex(x1, y0, z1);
				t.texture(u1, v1);
				t.vertex(x1, y0, z0);
				t.texture(u1, v0);
				t.vertex(x1, y1, z0);
				t.texture(u0, v0);
				t.vertex(x1, y1, z1);
			}
		}
	}

}
