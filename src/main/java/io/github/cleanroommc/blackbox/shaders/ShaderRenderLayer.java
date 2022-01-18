package io.github.cleanroommc.blackbox.shaders;

import io.github.cleanroommc.blackbox.Blackbox;
import io.github.cleanroommc.blackbox.shaders.depth.DepthHelpers;
import io.github.cleanroommc.blackbox.shaders.effects.BloomEffect;
import io.github.cleanroommc.blackbox.shaders.management.BlackboxShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.client.EnumHelperClient;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_LINEAR;

public class ShaderRenderLayer {

	public static void init() {
		Blackbox.LOGGER.info("Registering {} to BlockRenderLayer enum", Bloom.LAYER);
	}

	public static class Bloom {

		public static final BlockRenderLayer LAYER;

		public static Framebuffer FBO;

		static {
			LAYER = EnumHelperClient.addEnum(BlockRenderLayer.class, "BLOOM", new Class[] { String.class }, "Bloom");
		}

		public static int renderLayer(Minecraft minecraft, RenderGlobal renderGlobal, BlockRenderLayer translucentLayer, double partialTicks, int pass, Entity entity) {
			Framebuffer mcFBO = minecraft.getFramebuffer();
			if (FBO == null) {
				FBO = new Framebuffer(mcFBO.framebufferWidth, mcFBO.framebufferHeight, false);
				FBO.setFramebufferColor(0, 0, 0, 0);
			} else {
				FBO.createBindFramebuffer(mcFBO.framebufferWidth, mcFBO.framebufferHeight);
			}
			if (FBO.framebufferWidth != mcFBO.framebufferWidth || FBO.framebufferHeight != mcFBO.framebufferHeight) {
				if (mcFBO.isStencilEnabled() && !FBO.isStencilEnabled()) {
					FBO.enableStencil();
				}
				if (DepthHelpers.isLastBind() && DepthHelpers.isUseDefaultFBO()) {
					DepthHelpers.hookDepthTexture(FBO, DepthHelpers.framebufferDepthTexture);
				} else {
					DepthHelpers.hookDepthBuffer(FBO, mcFBO.depthBuffer);
				}
				FBO.setFramebufferFilter(GL_LINEAR);
			}

			GlStateManager.depthMask(true);
			// Commenting this out restores textures - fast render hook was originally going to be here
			/*
			mcFBO.bindFramebuffer(true);
			FBO.framebufferClear();
			FBO.bindFramebuffer(false);
			 */
			// Render to Bloom Buffer
			renderGlobal.renderBlockLayer(LAYER, partialTicks, pass, entity);
			GlStateManager.depthMask(false);
			// Fast Render Bloom Layer to mcFBO
			FBO.bindFramebufferTexture();
			BlackboxShaderManager.getInstance().renderFullImageInFramebuffer(mcFBO, ShaderLoaders.IMAGE_F, null);
			// Reset Transparent Layer Render State and Render
			OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, mcFBO.framebufferObject);
			GlStateManager.enableBlend();
			minecraft.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			GlStateManager.shadeModel(7425);
			int result = renderGlobal.renderBlockLayer(translucentLayer, partialTicks, pass, entity);
			minecraft.profiler.endStartSection("bloom");
			mcFBO.bindFramebufferTexture();
			GlStateManager.blendFunc(GL11.GL_DST_ALPHA, GL11.GL_ZERO);
			BlackboxShaderManager.getInstance().renderFullImageInFramebuffer(FBO, ShaderLoaders.IMAGE_F, null);
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			/*
			BloomEffect.strength = (float) ConfigHolder.client.shader.strength;
			BloomEffect.baseBrightness = (float) ConfigHolder.client.shader.baseBrightness;
			BloomEffect.highBrightnessThreshold = (float) ConfigHolder.client.shader.highBrightnessThreshold;
			BloomEffect.lowBrightnessThreshold = (float) ConfigHolder.client.shader.lowBrightnessThreshold;
			BloomEffect.step = (float) ConfigHolder.client.shader.step;
			switch (ConfigHolder.client.shader.bloomStyle) {
				case 0:
					BloomEffect.renderLOG(BLOOM_FBO, fbo);
					break;
				case 1:
					BloomEffect.renderUnity(BLOOM_FBO, fbo);
					break;
				case 2:
					BloomEffect.renderUnreal(BLOOM_FBO, fbo);
					break;
				default:
					GlStateManager.depthMask(false);
					GlStateManager.disableBlend();
					return result;
			}
			 */
			// BloomEffect.getInstance().renderLOG(FBO, mcFBO);
			// BloomEffect.getInstance().renderUnity(FBO, mcFBO);
			BloomEffect.getInstance().renderUnreal(FBO, mcFBO);
			GlStateManager.depthMask(false);
			// Render Bloom Blend result to mcFBO
			GlStateManager.disableBlend();
			BlackboxShaderManager.getInstance().renderFullImageInFramebuffer(mcFBO, ShaderLoaders.IMAGE_F, null);
			return result;
		}

	}

}
