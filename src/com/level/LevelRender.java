package com.level;

import org.lwjgl.opengl.GL11;

import com.entity.Player;
import com.gfx.Frustum;
import com.gfx.Tesellator;
import com.level.tile.Tile;
import com.phys.AABB;

public class LevelRender {
	private static final int CHUNK_SIZE = 16;
	
	private final Chunk[] chunks;
	private Level level;
	private Tesellator t;
	
	private final int chunkX;
	private final int chunkY;
	private final int chunkZ;
	
	public LevelRender(Level level) {
		this.level = level;
		this.chunkX = level.width / CHUNK_SIZE;
		this.chunkY = level.depth / CHUNK_SIZE;
		this.chunkZ = level.height / CHUNK_SIZE;
		
		this.chunks = new Chunk[chunkX * chunkY * chunkZ];
		
		for(int x = 0; x < chunkX; x++) {
			for(int y = 0; y < chunkY; y++) {
				for(int z = 0; z < chunkZ; z++) {
					int minChunkX = x * CHUNK_SIZE;
					int minChunkY = y * CHUNK_SIZE;
					int minChunkZ = z * CHUNK_SIZE;
					
					int maxChunkX = (x + 1) * CHUNK_SIZE;
					int maxChunkY = (y + 1) * CHUNK_SIZE;
					int maxChunkZ = (z + 1) * CHUNK_SIZE;
					
					if(maxChunkX > level.width)
						maxChunkX = level.width;
					if(maxChunkY > level.depth)
						maxChunkY = level.depth;
					if(maxChunkZ > level.height)
						maxChunkZ = level.height;
					
					Chunk chunk = new Chunk(level, minChunkX, minChunkY, minChunkZ, maxChunkX, maxChunkY, maxChunkZ);
					this.chunks[(x + y * chunkX) * chunkZ + z] = chunk;
				}
			}
		}
	}
	
	public void render(int layer) {
		Frustum frustum = Frustum.getFrustum();
		Chunk.rebuiltFrame = 0;
		
		for(Chunk chunk : chunks) {
			if(frustum.cubeInFrustum(chunk.boundingBox)) {
				chunk.render(layer);
			}
		}
	}
	
	public void pick(Player player) {
		float r = 3.0F;
		AABB box = player.boundingBox.grow(r, r, r);
		int x0 = (int)box.x0;
		int y0 = (int)box.y0;
		int z0 = (int)box.z0;
		int x1 = (int)(box.x1 + 1.0F);
		int y1 = (int)(box.y1 + 1.0F);
		int z1 = (int)(box.z1 + 1.0F);
		GL11.glInitNames();
		for(int x = x0; x < x1; x++) {
			GL11.glPushName(x);
			for(int y = y0; y < y1; y++) {
				GL11.glPushName(y);
				for(int z = z0; z < z1; z++) {
					GL11.glPushName(z);
					if(level.isSolidTile(x, y, z)) {
						GL11.glPushName(0);
						for(int i = 0; i < 6; i++) {
							GL11.glPushName(i);
							t.init();
							Tile.grass.render(t, level, i, x, y, z);
							t.flush();
							GL11.glPopName();
						}
						GL11.glPopName();
					}
					GL11.glPopName();
				}
				GL11.glPopName();
			}
			GL11.glPopName();
		}
	}

}
