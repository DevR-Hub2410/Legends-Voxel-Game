package com.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class Textures {
	private static int lastId = Integer.MIN_VALUE;
	
	public static int loadTexture(String res, int mode) {
		int id = GL11.glGenTextures();
		
		bind(id);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mode);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mode);
		
		InputStream input = Textures.class.getResourceAsStream(res);
		
		try {
			BufferedImage img = ImageIO.read(input);
			
			int width = img.getWidth();
			int height = img.getHeight();
			
			int[] pixels = new int[width * height];
			img.getRGB(0, 0, width, height, pixels, 0, width);
			
			for(int i = 0; i < pixels.length; i++) {
				int alpha = pixels[i] >> 24 & 0xFF;
				int red = pixels[i] >> 16 & 0xFF;
				int green = pixels[i] >> 8 & 0xFF;
				int blue = pixels[i] & 0xFF;
				
				pixels[i] = alpha << 24 | blue << 16 | green << 8 | red;
			}
			
			ByteBuffer byteBuffer = BufferUtils.createByteBuffer(width * height * 4);
			byteBuffer.asIntBuffer().put(pixels);
			
			GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, GL11.GL_RGBA, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, byteBuffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static void bind(int id) {
		if(id != lastId) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
			lastId = id;
		}
	}

}
