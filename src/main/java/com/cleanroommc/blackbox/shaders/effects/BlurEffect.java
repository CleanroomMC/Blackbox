package com.cleanroommc.blackbox.shaders.effects;

import com.cleanroommc.blackbox.buffer.BufferHelpers;
import com.cleanroommc.blackbox.buffer.PingPongBuffer;
import com.cleanroommc.blackbox.shaders.ShaderLoaders;
import com.cleanroommc.blackbox.shaders.management.BlackboxShaderManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

public class BlurEffect {

	private static final BlurEffect INSTANCE = new BlurEffect();

	public static BlurEffect getInstance() {
		return INSTANCE;
	}

	private Framebuffer BLUR_H;
	private Framebuffer BLUR_W;
	private Framebuffer BLUR_H2;
	private Framebuffer BLUR_W2;

	public void updateSize(int lastWidth, int lastHeight) {
		if (BLUR_H == null) {
			BLUR_H = new Framebuffer(lastWidth / 8, lastHeight / 8, false);
			BLUR_H2 = new Framebuffer(lastWidth / 4, lastHeight / 4, false);
			BLUR_W = new Framebuffer(lastWidth / 8, lastHeight / 8, false);
			BLUR_W2 = new Framebuffer(lastWidth / 4, lastHeight / 4, false);
			BLUR_H.setFramebufferColor(0, 0, 0, 0);
			BLUR_H2.setFramebufferColor(0, 0, 0, 0);
			BLUR_W.setFramebufferColor(0, 0, 0, 0);
			BLUR_W2.setFramebufferColor(0, 0, 0, 0);
			BLUR_H.setFramebufferFilter(GL11.GL_LINEAR);
			BLUR_H2.setFramebufferFilter(GL11.GL_LINEAR);
			BLUR_W.setFramebufferFilter(GL11.GL_LINEAR);
			BLUR_W2.setFramebufferFilter(GL11.GL_LINEAR);
		} else if (BufferHelpers.updateFramebufferSize(BLUR_H, lastWidth / 8, lastHeight / 8)){
			BufferHelpers.updateFramebufferSize(BLUR_H2, lastWidth / 4, lastHeight / 4);
			BufferHelpers.updateFramebufferSize(BLUR_W, lastWidth / 8, lastHeight / 8);
			BufferHelpers.updateFramebufferSize(BLUR_W2, lastWidth / 4, lastHeight / 4);
			BLUR_H.setFramebufferFilter(GL11.GL_LINEAR);
			BLUR_H2.setFramebufferFilter(GL11.GL_LINEAR);
			BLUR_W.setFramebufferFilter(GL11.GL_LINEAR);
			BLUR_W2.setFramebufferFilter(GL11.GL_LINEAR);
		}
		PingPongBuffer.updateSize(lastWidth, lastHeight);
	}

	public Framebuffer renderBlur1(float step) {
		BlackboxShaderManager.getInstance().renderFullImageInFramebuffer(BLUR_H2, ShaderLoaders.BLUR, uniformCache -> uniformCache.glUniform2F("blurDir", 0, step)).bindFramebufferTexture();
		BlackboxShaderManager.getInstance().renderFullImageInFramebuffer(BLUR_W2, ShaderLoaders.BLUR, uniformCache -> uniformCache.glUniform2F("blurDir", step, 0)).bindFramebufferTexture();
		BlackboxShaderManager.getInstance().renderFullImageInFramebuffer(BLUR_H, ShaderLoaders.BLUR, uniformCache -> uniformCache.glUniform2F("blurDir", 0, step)).bindFramebufferTexture();
		BlackboxShaderManager.getInstance().renderFullImageInFramebuffer(BLUR_W, ShaderLoaders.BLUR, uniformCache -> uniformCache.glUniform2F("blurDir", step, 0)).bindFramebufferTexture();
		return BLUR_W;
	}

	public static Framebuffer renderBlur2(int loop, float step) {
		for (int i = 0; i < loop; i++) {
			BlackboxShaderManager.getInstance().renderFullImageInFramebuffer(PingPongBuffer.swap(true), ShaderLoaders.BLUR, uniformCache -> uniformCache.glUniform2F("blurDir", 0, step)).bindFramebufferTexture();
			BlackboxShaderManager.getInstance().renderFullImageInFramebuffer(PingPongBuffer.swap(), ShaderLoaders.BLUR, uniformCache -> uniformCache.glUniform2F("blurDir", step, 0)).bindFramebufferTexture();
		}
		return PingPongBuffer.getCurrentBuffer(false);
	}
	
}
