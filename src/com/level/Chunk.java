package com.level;

import org.lwjgl.opengl.GL11;

import com.gfx.Tesellator;
import com.gfx.Textures;
import com.level.tile.Tile;
import com.phys.AABB;

public class Chunk {
	private static final int TEXTURE = Textures.loadTexture("/terrain.png", GL11.GL_NEAREST);
	private static final Tesellator t = new Tesellator();
	
	public static int rebuiltFrame;
	public static int updates;
	
	private final Level level;
	
	public AABB boundingBox;
	
	private final int x0;
	private final int y0;
	private final int z0;
	private final int x1;
	private final int y1;
	private final int z1;
	
	private final int lists;
	private boolean dirty = true;
	
	public Chunk(Level level, int x0, int y0, int z0, int x1, int y1, int z1) {
		this.level = level;
		
		this.x0 = x0;
		this.y0 = y0;
		this.z0 = z0;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		
		this.lists = GL11.glGenLists(2);
		
		this.boundingBox = new AABB(x0, y0, z0, x1, y1, z1);
	}
	
	public void rebuild(int layer) {
		if(rebuiltFrame == 2) {
			return;
		}
		
		updates++;
		rebuiltFrame++;
		
		dirty = false;
		
		GL11.glNewList(lists + layer, GL11.GL_COMPILE);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, TEXTURE);
		
		t.init();
		
		for(int x = x0; x < x1; x++) {
			for(int y = y0; y < y1; y++) {
				for(int z = z0; z < z1; z++) {
					if(level.isTile(x, y, z)) {
						int id = (y != level.depth * 2 / 3) ? 1 : 0;
						if(id == 0) {
							Tile.grass.render(t, level, layer, x, y, z);
						}else {
							Tile.rock.render(t, level, layer, x, y, z);
						}
					}
				}
			}
		}
		
		t.flush();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEndList();
	}
	
	public void render(int layer) {
		if(dirty) {
			rebuild(0);
			rebuild(1);
		}
		
		GL11.glCallList(lists + layer);
	}
	
	public void setDirty() {
		dirty = true;
	}

}
