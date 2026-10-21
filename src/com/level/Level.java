package com.level;

import java.util.ArrayList;

import com.phys.AABB;

public class Level {
	public final int width;
	public final int height;
	public final int depth;
	
	private final byte[] blocks;
	private final int[] lightDepths;
	
	private final ArrayList<LevelListener> levelListeners = new ArrayList<>();
	
	public Level(int width, int height, int depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		
		this.blocks = new byte[width * height * depth];
		this.lightDepths = new int[width * height];
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < depth; y++) {
				for(int z = 0; z < height; z++) {
					int index = (y * this.height + z) * this.width + x;
					
					blocks[index] = (byte) ((y <= depth * 2 / 3) ? 1 : 0);
				}
			}
		}
		
		calcLightDepths(0, 0, width, height);
	}
	
	private void calcLightDepths(int x0, int z0, int x1, int z1) {
		for(int x = x0; x < x0 + x1; x++) {
			for(int z = z0; z < z0 + z1; z++) {
				int prevDepth = lightDepths[x + z * width];
				int depth = this.depth - 1;
				
				while(depth > 0 && !isLightBlocker(x, depth, z)) {
					depth--;
				}
				
				lightDepths[x + z * width] = depth;
				
				if(prevDepth != depth) {
					int minTileChangeY = Math.min(prevDepth, depth);
					int maxTileChangeY = Math.max(prevDepth, depth);
					
					for(LevelListener levelListener : levelListeners) {
						levelListener.lightColumnChanged(x, z, minTileChangeY, maxTileChangeY);
					}
				}
			}
		}
	}
	
	public boolean isTile(int x, int y, int z) {
		if(x < 0 || y < 0 || z < 0 || x >= width || y >= depth || z >= height) {
			return false;
		}
		
		int i = (y * this.height + z) * this.width + x;
		
		return this.blocks[i] != 0;
	}
	
	public boolean isSolidTile(int x, int y, int z) {
		return isTile(x, y, z);
	}
	
	public boolean isLightBlocker(int x, int y, int z) {
		return isSolidTile(x, y, z);
	}
	
	public float getBrightness(int x, int y, int z) {
		float dark = 0.8F;
		float light = 1.0F;
		
		if(x < 0 || y < 0 || z < 0 || x >= width || y >= depth || z >= height) {
			return light;
		}
		
		if(y < lightDepths[x + z * width]) {
			return dark;
		}
		
		return light;
	}
	
	public ArrayList<AABB> getCube(AABB boundingBox) {
		ArrayList<AABB> boundingBoxList = new ArrayList<>();
		
		int x0 = (int) (Math.floor(boundingBox.x0) - 1);
		int y0 = (int) (Math.floor(boundingBox.y0) - 1);
		int z0 = (int) (Math.floor(boundingBox.z0) - 1);
		int x1 = (int) (Math.ceil(boundingBox.x1) + 1);
		int y1 = (int) (Math.ceil(boundingBox.y1) + 1);
		int z1 = (int) (Math.ceil(boundingBox.z1) + 1);
		
		x0 = Math.max(0, x0);
		y0 = Math.max(0, y0);
		z0 = Math.max(0, z0);
		
		x1 = Math.min(width, x1);
		y1 = Math.min(depth, y1);
		z1 = Math.min(height, z1);
		
		for(int x = x0; x < x1; x++) {
			for(int y = y0; y < y1; y++) {
				for(int z = z0; z < z1; z++) {
					if(isSolidTile(x, y, z)) {
						boundingBoxList.add(new AABB(x, y, z, x + 1, y + 1, z + 1));
					}
				}
			}
		}
		
		return boundingBoxList;
	}
	
	public void setTile(int x, int y, int z, int id) {
		if(x < 0 || y < 0 || z < 0 || x >= width || y >= depth || z >= height) {
			return;
		}
		
		blocks[(y * height + z) * width + x] = (byte) id;
		
		calcLightDepths(x, z, 1, 1);
		
		for(LevelListener levelListener : levelListeners) {
			levelListener.tileChanged(x, y, z);
		}
	}
	
	public void addListener(LevelListener levelListener) {
		levelListeners.add(levelListener);
	}

}
