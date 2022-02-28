package com.cleanroommc.blackbox.shaders.effects;

import com.cleanroommc.blackbox.shaders.management.BlackboxShaderManager;
import com.cleanroommc.blackbox.buffer.BufferHelpers;
import com.cleanroommc.blackbox.buffer.PingPongBuffer;
import com.cleanroommc.blackbox.shaders.ShaderLoaders;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class BloomEffect {

	private static final BloomEffect INSTANCE = new BloomEffect();

	public static BloomEffect getInstance() {
		return INSTANCE;
	}

	private Framebuffer[] BUFFERS_D;
	private Framebuffer[] BUFFERS_U;
	
	public float strength = 1.5F;
	public float baseBrightness = 0.1F;
	public float highBrightnessThreshold = 0.5F;
	public float lowBrightnessThreshold = 0.2F;
	public float step = 1F;
	public int nMips = 5;

	private BloomEffect() { }

	private void blend(Framebuffer bloom, Framebuffer backgroundFBO) {
		// Bind main FBO
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
		GlStateManager.enableTexture2D();
		backgroundFBO.bindFramebufferTexture();
		// Bind blur FBO
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
		GlStateManager.enableTexture2D();
		bloom.bindFramebufferTexture();
		// Blend shaders
		BlackboxShaderManager.getInstance().renderFullImageInFramebuffer(PingPongBuffer.swap(), ShaderLoaders.BLOOM_COMBINE, uniformCache -> {
			uniformCache.glUniform1I("buffer_a", 0);
			uniformCache.glUniform1I("buffer_b", 1);
			uniformCache.glUniform1F("intensive", strength);
			uniformCache.glUniform1F("base", baseBrightness);
			uniformCache.glUniform1F("threshold_up", highBrightnessThreshold);
			uniformCache.glUniform1F("threshold_down", lowBrightnessThreshold);
		});
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
		GlStateManager.bindTexture(0);
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
		GlStateManager.bindTexture(0);
		PingPongBuffer.bindFramebufferTexture();
	}

	private void cleanUp(int lastWidth, int lastHeight) {
		if (BUFFERS_D == null || BUFFERS_D.length != nMips) {
			if (BUFFERS_D != null) {
				for (int i = 0; i < BUFFERS_D.length; i++) {
					BUFFERS_D[i].deleteFramebuffer();
					BUFFERS_U[i].deleteFramebuffer();
				}
			}
			BUFFERS_D = new Framebuffer[nMips];
			BUFFERS_U = new Framebuffer[nMips];
			int resX = lastWidth / 2;
			int resY = lastHeight / 2;
			for (int i = 0; i < nMips; i++) {
				BUFFERS_D[i] = new Framebuffer(resX, resY, false);
				BUFFERS_U[i] = new Framebuffer(resX, resY, false);
				BUFFERS_D[i].setFramebufferColor(0, 0, 0, 0);
				BUFFERS_U[i].setFramebufferColor(0, 0, 0, 0);
				BUFFERS_D[i].setFramebufferFilter(GL11.GL_LINEAR);
				BUFFERS_U[i].setFramebufferFilter(GL11.GL_LINEAR);
				resX /= 2;
				resY /= 2;
			}
		} else if (BufferHelpers.updateFramebufferSize(BUFFERS_D[0], lastWidth / 2, lastHeight / 2)) {
			int resX = lastWidth / 2;
			int resY = lastHeight / 2;
			for (int i = 0; i < nMips; i++) {
				BufferHelpers.updateFramebufferSize(BUFFERS_D[i], resX, resY);
				BufferHelpers.updateFramebufferSize(BUFFERS_U[i], resX, resY);
				BUFFERS_D[i].setFramebufferFilter(GL11.GL_LINEAR);
				BUFFERS_U[i].setFramebufferFilter(GL11.GL_LINEAR);
				resX /= 2;
				resY /= 2;
			}
		}
		PingPongBuffer.updateSize(lastWidth, lastHeight);
	}

	public void renderLOG(Framebuffer highLightFBO, Framebuffer backgroundFBO) {
		PingPongBuffer.updateSize(backgroundFBO.framebufferWidth, backgroundFBO.framebufferHeight);
		BlurEffect.getInstance().updateSize(backgroundFBO.framebufferWidth, backgroundFBO.framebufferHeight);
		highLightFBO.bindFramebufferTexture();
		blend(BlurEffect.getInstance().renderBlur1(step), backgroundFBO);
	}

	public void renderUnity(Framebuffer highLightFBO, Framebuffer backgroundFBO) {
		cleanUp(backgroundFBO.framebufferWidth, backgroundFBO.framebufferHeight);
		renderDownSampling(highLightFBO, BUFFERS_D[0]);
		for (int i = 0; i < BUFFERS_D.length - 1; i++) {
			renderDownSampling(BUFFERS_D[i], BUFFERS_D[i + 1]);
		}
		renderUpSampling(BUFFERS_D[BUFFERS_D.length - 1], BUFFERS_D[BUFFERS_D.length - 2], BUFFERS_U[BUFFERS_D.length - 2]);
		for (int i = BUFFERS_U.length - 2; i > 0; i--) {
			renderUpSampling(BUFFERS_U[i], BUFFERS_D[i - 1], BUFFERS_U[i-1]);
		}
		renderUpSampling(BUFFERS_U[0], highLightFBO, PingPongBuffer.swap());
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
		GlStateManager.bindTexture(0);
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
		GlStateManager.bindTexture(0);
		blend(PingPongBuffer.getCurrentBuffer(false), backgroundFBO);
	}

	private static void renderDownSampling(Framebuffer U, Framebuffer D) {
		U.bindFramebufferTexture();
		BlackboxShaderManager.getInstance().renderFullImageInFramebuffer(D, ShaderLoaders.DOWN_SAMPLING, uniformCache -> uniformCache.glUniform2F("u_resolution2", U.framebufferWidth, U.framebufferHeight));
	}

	private static void renderUpSampling(Framebuffer U, Framebuffer D, Framebuffer T) {
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
		GlStateManager.enableTexture2D();
		U.bindFramebufferTexture();
		GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
		GlStateManager.enableTexture2D();
		D.bindFramebufferTexture();
		BlackboxShaderManager.getInstance().renderFullImageInFramebuffer(T, ShaderLoaders.UP_SAMPLING,uniformCache -> {
			uniformCache.glUniform1I("upTexture", 0);
			uniformCache.glUniform1I("downTexture", 1);
			uniformCache.glUniform2F("u_resolution2", U.framebufferWidth, U.framebufferHeight);
		});
	}

	public void renderUnreal(Framebuffer highLightFBO, Framebuffer backgroundFBO) {
		cleanUp(backgroundFBO.framebufferWidth, backgroundFBO.framebufferHeight);
		// Blur all mips
		int[] kernelSizeArray = new int[] { 3, 5, 7, 9, 11 };
		highLightFBO.bindFramebufferTexture();
		for (int i = 0; i < BUFFERS_D.length; i++) {
			Framebuffer buffer_h = BUFFERS_D[i];
			int kernel = kernelSizeArray[i];
			BlackboxShaderManager.getInstance().renderFullImageInFramebuffer(buffer_h, ShaderLoaders.SEPARABLE_BLUR, uniformCache -> {
				uniformCache.glUniform2F("texSize", buffer_h.framebufferWidth, buffer_h.framebufferHeight);
				uniformCache.glUniform2F("blurDir", step, 0);
				uniformCache.glUniform1I("kernel_radius", kernel);
			}).bindFramebufferTexture();
			Framebuffer buffer_v = BUFFERS_U[i];
			BlackboxShaderManager.getInstance().renderFullImageInFramebuffer(buffer_v, ShaderLoaders.SEPARABLE_BLUR, uniformCache -> {
				uniformCache.glUniform2F("texSize", buffer_v.framebufferWidth, buffer_v.framebufferHeight);
				uniformCache.glUniform2F("blurDir", 0, step);
				uniformCache.glUniform1I("kernel_radius", kernel);
			}).bindFramebufferTexture();
		}
		// Composite all mips
		for (int i = 0; i < BUFFERS_D.length; i++) {
			GlStateManager.setActiveTexture(GL13.GL_TEXTURE0 + i);
			GlStateManager.enableTexture2D();
			BUFFERS_U[i].bindFramebufferTexture();
		}
		BlackboxShaderManager.getInstance().renderFullImageInFramebuffer(BUFFERS_D[0], ShaderLoaders.COMPOSITE, uniformCache -> {
			uniformCache.glUniform1I("blurTexture1", 0);
			uniformCache.glUniform1I("blurTexture2", 1);
			uniformCache.glUniform1I("blurTexture3", 2);
			uniformCache.glUniform1I("blurTexture4", 3);
			uniformCache.glUniform1I("blurTexture5", 4);
			uniformCache.glUniform1F("bloomStrength", strength);
			uniformCache.glUniform1F("bloomRadius", 1);
		});
		for (int i = BUFFERS_D.length - 1; i >= 0; i--) {
			GlStateManager.setActiveTexture(GL13.GL_TEXTURE0 + i);
			GlStateManager.bindTexture(0);
		}
		blend(BUFFERS_D[0], backgroundFBO);
	}

}
