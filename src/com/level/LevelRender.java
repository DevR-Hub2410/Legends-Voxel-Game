package com.level;

import org.lwjgl.opengl.GL11;

import com.entity.Player;
import com.gfx.Frustum;
import com.gfx.HitResult;
import com.gfx.Tesellator;
import com.level.tile.Tile;

public class LevelRender implements LevelListener {
	private static final int CHUNK_SIZE = 16;

	private final Chunk[] chunks;
	private final Tesellator t;
	private final Level level;

	private final int chunkX;
	private final int chunkY;
	private final int chunkZ;

	public LevelRender(Level level) {
		level.addListener(this);

		this.t = new Tesellator();
		this.level = level;
		this.chunkX = level.width / CHUNK_SIZE;
		this.chunkY = level.depth / CHUNK_SIZE;
		this.chunkZ = level.height / CHUNK_SIZE;

		this.chunks = new Chunk[chunkX * chunkY * chunkZ];

		for (int x = 0; x < chunkX; x++) {
			for (int y = 0; y < chunkY; y++) {
				for (int z = 0; z < chunkZ; z++) {
					int minChunkX = x * CHUNK_SIZE;
					int minChunkY = y * CHUNK_SIZE;
					int minChunkZ = z * CHUNK_SIZE;

					int maxChunkX = (x + 1) * CHUNK_SIZE;
					int maxChunkY = (y + 1) * CHUNK_SIZE;
					int maxChunkZ = (z + 1) * CHUNK_SIZE;

					if (maxChunkX > level.width)
						maxChunkX = level.width;
					if (maxChunkY > level.depth)
						maxChunkY = level.depth;
					if (maxChunkZ > level.height)
						maxChunkZ = level.height;

					Chunk chunk = new Chunk(level, minChunkX, minChunkY, minChunkZ, maxChunkX, maxChunkY, maxChunkZ);
					this.chunks[(x + y * chunkX) * chunkZ + z] = chunk;
				}
			}
		}
	}

	public void setDirty(int x0, int y0, int z0, int x1, int y1, int z1) {
		x0 /= CHUNK_SIZE;
		y0 /= CHUNK_SIZE;
		z0 /= CHUNK_SIZE;
		x1 /= CHUNK_SIZE;
		y1 /= CHUNK_SIZE;
		z1 /= CHUNK_SIZE;

		x0 = Math.max(x0, 0);
		y0 = Math.max(y0, 0);
		z0 = Math.max(z0, 0);

		x1 = Math.min(x1, chunkX - 1);
		y1 = Math.min(y1, chunkY - 1);
		z1 = Math.min(z1, chunkZ - 1);

		for (int x = x0; x <= x1; x++) {
			for (int y = y0; y <= y1; y++) {
				for (int z = z0; z <= z1; z++) {
					Chunk chunk = chunks[(x + y * chunkX) * chunkZ + z];
					chunk.setDirty();
				}
			}
		}
	}

	public void render(int layer) {
		Frustum frustum = Frustum.getFrustum();
		Chunk.rebuiltFrame = 0;

		for (Chunk chunk : chunks) {
			if (frustum.cubeInFrustum(chunk.boundingBox)) {
				chunk.render(layer);
			}
		}
	}

	/*
	 * public void pick(Player player) { float r = 3.0F; AABB box =
	 * player.boundingBox.grow(r, r, r); int x0 = (int)box.x0; int y0 = (int)box.y0;
	 * int z0 = (int)box.z0; int x1 = (int)(box.x1 + 1.0F); int y1 = (int)(box.y1 +
	 * 1.0F); int z1 = (int)(box.z1 + 1.0F); GL11.glInitNames(); for(int x = x0; x <
	 * x1; x++) { GL11.glPushName(x); for(int y = y0; y < y1; y++) {
	 * GL11.glPushName(y); for(int z = z0; z < z1; z++) { GL11.glPushName(z);
	 * if(level.isSolidTile(x, y, z)) { GL11.glPushName(0); for(int i = 0; i < 6;
	 * i++) { GL11.glPushName(i); t.init(); Tile.rock.renderFace(t, x, y, z, i);
	 * t.flush(); GL11.glPopName(); } GL11.glPopName(); } GL11.glPopName(); }
	 * GL11.glPopName(); } GL11.glPopName(); } }
	 */

	public HitResult pick(Player player) {
		final double reach = 5.0D;

		double ox = player.x;
		double oy = player.y;
		double oz = player.z;

		double yaw = Math.toRadians(player.yRotation);
		double pitch = Math.toRadians(player.xRotation);

		double dx = Math.sin(yaw) * Math.cos(pitch);
		double dy = -Math.sin(pitch);
		double dz = -Math.cos(yaw) * Math.cos(pitch);

		int x = (int) Math.floor(ox);
		int y = (int) Math.floor(oy);
		int z = (int) Math.floor(oz);

		if (level.isSolidTile(x, y, z)) {
			return new HitResult(x, y, z, 0, 0);
		}

		int stepX = dx > 0 ? 1 : -1;
		int stepY = dy > 0 ? 1 : -1;
		int stepZ = dz > 0 ? 1 : -1;

		double deltaX = dx == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0D / dx);
		double deltaY = dy == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0D / dy);
		double deltaZ = dz == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0D / dz);

		double maxX = dx > 0 ? (x + 1.0D - ox) / dx : dx < 0 ? (ox - x) / -dx : Double.POSITIVE_INFINITY;

		double maxY = dy > 0 ? (y + 1.0D - oy) / dy : dy < 0 ? (oy - y) / -dy : Double.POSITIVE_INFINITY;

		double maxZ = dz > 0 ? (z + 1.0D - oz) / dz : dz < 0 ? (oz - z) / -dz : Double.POSITIVE_INFINITY;

		double distance = 0.0D;
		int face = 0;

		while (distance <= reach) {
			if (maxX < maxY && maxX < maxZ) {
				x += stepX;
				distance = maxX;
				maxX += deltaX;
				face = stepX > 0 ? 4 : 5;
			} else if (maxY < maxZ) {
				y += stepY;
				distance = maxY;
				maxY += deltaY;
				face = stepY > 0 ? 0 : 1;
			} else {
				z += stepZ;
				distance = maxZ;
				maxZ += deltaZ;
				face = stepZ > 0 ? 2 : 3;
			}

			if (distance > reach) {
				break;
			}

			if (level.isSolidTile(x, y, z)) {
				return new HitResult(x, y, z, 0, face);
			}
		}

		return null;
	}

	public void renderHit(HitResult hitResult) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_CURRENT_BIT);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, (float) Math.sin(System.currentTimeMillis() / 100.0) * 0.2F + 0.4F);

		t.init();
		Tile.rock.renderFace(t, hitResult.x, hitResult.y, hitResult.z, hitResult.face);
		t.flush();

		GL11.glDisable(GL11.GL_BLEND);
	}

	@Override
	public void lightColumnChanged(int x, int z, int y0, int y1) {
		setDirty(x - 1, y0 - 1, z - 1, x + 1, y1 + 1, z + 1);
	}

	@Override
	public void tileChanged(int x, int y, int z) {
		setDirty(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
	}

	@Override
	public void allChanged() {
		setDirty(0, 0, 0, level.width, level.depth, level.height);
	}

}
