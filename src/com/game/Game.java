package com.game;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.entity.Player;
import com.level.Chunk;
import com.level.Level;
import com.level.LevelRender;

public class Game implements Runnable{
	private static final int WIDTH = 950;
	private static final int HEIGHT = 650;
	private static final String TITLE = "Legends Voxel Game";
	public static final String VERSION = "0.0.0.1";
	
	private final FloatBuffer fogColor = BufferUtils.createFloatBuffer(4);

	private Level level;
	private LevelRender render;
	private Player player;
	
	private boolean running = false;
	
	private void init() throws Exception {
		fogColor.put(new float[] {
				14 / 255.0F,
				11 / 255.0F,
				10 / 255.0F,
				255 / 255.0F,
		}).flip();
		Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
		Display.setTitle(TITLE);
		
		Display.create();
		Mouse.create();
		Keyboard.create();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glClearColor(0.5F, 0.8F, 1.0F, 0.0F);
		GL11.glClearDepth(1.0);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		
		GLU.gluPerspective(70.0F, WIDTH / (float) HEIGHT, 0.05F, 1000.0F);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		level = new Level(64, 64, 64);
		render = new LevelRender(level);
		player = new Player(level);
		
		Mouse.setGrabbed(true);
	}
	
	public void start() {
		running = true;
		new Thread(this).start();
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000.0 / 60.0;
		double unprocessed = 0;
		int ticks = 0;
		int frames = 0;
		long lastTimer = System.currentTimeMillis();
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		while(!Display.isCloseRequested() && running && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			long now = System.nanoTime();
			unprocessed += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = true;
			while(unprocessed >= 1) {
				ticks++;
				tick();
				unprocessed--;
				shouldRender = true;
			}
			
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(shouldRender) {
				frames++;
				render();
			}
			
			while(System.currentTimeMillis() - lastTimer > 1000) {
				lastTimer += 1000;
				System.out.println("fps: " + frames + ", ticks: " + ticks);
				Chunk.updates = 0;
				frames = 0;
				ticks = 0;
			}
		}
	}
	
	private void moveCameraToPlayer() {
		Player player = this.player;
		
		GL11.glTranslatef(0.0F, 0.0F, -0.3F);
		
		GL11.glRotatef(player.xRotation, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(player.yRotation, 0.0F, 1.0F, 0.0F);
		
		double x = this.player.prevX + (this.player.x - this.player.prevX);
		double y = this.player.prevY + (this.player.y - this.player.prevY);
		double z = this.player.prevZ + (this.player.z - this.player.prevZ);
		
		GL11.glTranslated(-x, -y, -z);
	}
	
	public void destroy() {
		running = false;
		
		Display.destroy();
		Keyboard.destroy();
		Mouse.destroy();
	}
	
	private void tick() {
		player.tick();
	}
	
	private void render() {
		float motionX = Mouse.getDX();
		float motionY = Mouse.getDY();
		
		player.turn(motionX, motionY);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glLoadIdentity();
		moveCameraToPlayer();
		
		GL11.glEnable(GL11.GL_FOG);
		GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
		GL11.glFogf(GL11.GL_FOG_START, -10);
		GL11.glFogf(GL11.GL_FOG_END, 20);
		GL11.glFog(GL11.GL_FOG_COLOR, fogColor);
		GL11.glDisable(GL11.GL_FOG);
		
		render.render(0);
		
		GL11.glEnable(GL11.GL_FOG);
		
		render.render(1);
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		Display.update();
	}

	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}
}
