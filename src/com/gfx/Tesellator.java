package com.gfx;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Tesellator {
	private static final int MAX_VERTEX = 100000;
	private final FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(MAX_VERTEX * 3);
	private final FloatBuffer textureBuffer = BufferUtils.createFloatBuffer(MAX_VERTEX * 2);
	private final FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(MAX_VERTEX * 3);
	
	private int vertices = 0;
	
	private boolean hasTexture = false;
	private float textureU = 0;
	private float textureV = 0;
	
	private boolean hasColor = false;
	private float red;
	private float green;
	private float blue;
	
	public void init() {
		clear();
	}
	
	public void vertex(float x, float y, float z) {
		vertexBuffer.put(vertices * 3, x);
		vertexBuffer.put(vertices * 3 + 1, y);
		vertexBuffer.put(vertices * 3 + 2, z);
		
		if(hasTexture) {
			textureBuffer.put(vertices * 2, textureU);
			textureBuffer.put(vertices * 2 + 1, textureV);
		}
		
		if(hasColor) {
			colorBuffer.put(vertices * 3, red);
			colorBuffer.put(vertices * 3 + 1, green);
			colorBuffer.put(vertices * 3 + 2, blue);
		}
		
		vertices++;
		
		if(vertices == MAX_VERTEX) {
			flush();
		}
	}
	
	public void texture(float textureU, float textureV) {
		hasTexture = true;
		this.textureU = textureU;
		this.textureV = textureV;
	}
	
	public void color(float red, float green, float blue) {
		hasColor = true;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public void flush() {
		vertexBuffer.flip();
		textureBuffer.flip();
		
		GL11.glVertexPointer(3, GL11.GL_POINTS, vertexBuffer);
		if(hasTexture) {
			GL11.glTexCoordPointer(2, GL11.GL_POINTS, textureBuffer);
		}
		if(hasColor) {
			GL11.glColorPointer(3, GL11.GL_POINTS, colorBuffer);
		}
	
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		if(hasTexture) {
			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		}
		if(hasColor) {
			GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		}
		
		GL11.glDrawArrays(GL11.GL_QUADS, GL11.GL_POINTS, vertices);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		if(hasTexture) {
			GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		}
		if(hasColor) {
			GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		}
		
		clear();
	}
	
	public void color(int color) {
		float r = (color >> 16 & 0xFF) / 255.0F;
		float g = (color >> 8 & 0xFF) / 255.0F;
		float b = (color & 0xFF) / 255.0F;
		
		color(r, g, b);
	}
	
	private void clear() {
		vertexBuffer.clear();
		textureBuffer.clear();
		vertices = 0;
		
		hasColor = false;
		hasTexture = false;
	}

}
