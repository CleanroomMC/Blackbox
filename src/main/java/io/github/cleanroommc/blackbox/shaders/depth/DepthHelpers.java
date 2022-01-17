package io.github.cleanroommc.blackbox.shaders.depth;

import io.github.cleanroommc.blackbox.shaders.ShaderRenderLayer;
import io.github.cleanroommc.blackbox.shaders.management.BlackboxShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL11.glGetInteger;

public class DepthHelpers {

	public static int framebufferObject;
	public static int framebufferDepthTexture;
	private static boolean useDefaultFBO = true;
	private static boolean lastBind;
	private static int lastWidth, lastHeight;

	private static boolean shouldRenderDepthTexture() {
		return lastBind && BlackboxShaderManager.isShadersCompatible() && OpenGlHelper.isFramebufferEnabled();
	}

	public static void preWorldRender(Minecraft minecraft) {
		if (shouldRenderDepthTexture()) {
			// Kila: If we can't use the vanilla fbo.... okay, why not create our own fbo?
			if (useDefaultFBO && GL11.glGetError() != 0) {
				useDefaultFBO = false;
				if (framebufferDepthTexture != 0) {
					disposeDepthTexture();
					createDepthTexture();
				}
			}
			if (framebufferDepthTexture == 0) {
				createDepthTexture();
			} else if (lastWidth != minecraft.getFramebuffer().framebufferWidth || lastHeight != minecraft.getFramebuffer().framebufferHeight) {
				disposeDepthTexture();
				createDepthTexture();
			}
		} else {
			disposeDepthTexture();
		}
		lastBind = false;
	}

	public static void postWorldRender(Minecraft minecraft, RenderGlobal renderGlobal, float partialTicks) {
		Entity viewer = minecraft.getRenderViewEntity();
		if (!useDefaultFBO && framebufferDepthTexture != 0) {
			int lastFBO = GlStateManager.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
			OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, framebufferObject);
			GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
			GlStateManager.disableTexture2D();
			renderGlobal.renderBlockLayer(BlockRenderLayer.SOLID, partialTicks, 0, viewer);
			renderGlobal.renderBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, partialTicks, 0, viewer);
			GlStateManager.enableTexture2D();
			OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, lastFBO);
		}
	}

	public static void createDepthTexture() {
		int lastFBO = glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
		Framebuffer framebuffer = Minecraft.getMinecraft().getFramebuffer();
		boolean stencil = useDefaultFBO && framebuffer.isStencilEnabled();
		if (useDefaultFBO) {
			framebufferObject = framebuffer.framebufferObject;
		} else {
			framebufferObject = OpenGlHelper.glGenFramebuffers();
		}
		framebufferDepthTexture = TextureUtil.glGenTextures(); // Gen Texture
		GlStateManager.bindTexture(framebufferDepthTexture);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_DEPTH_TEXTURE_MODE, GL11.GL_LUMINANCE);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_FUNC, GL11.GL_LEQUAL);
		GlStateManager.glTexImage2D(GL11.GL_TEXTURE_2D, 0,
				stencil ? GL30.GL_DEPTH24_STENCIL8 : GL14.GL_DEPTH_COMPONENT24,
				framebuffer.framebufferTextureWidth,
				framebuffer.framebufferTextureHeight, 0,
				stencil ? GL30.GL_DEPTH_STENCIL : GL11.GL_DEPTH_COMPONENT,
				stencil ? GL30.GL_UNSIGNED_INT_24_8 : GL11.GL_UNSIGNED_INT, null);
		GlStateManager.bindTexture(0);
		lastWidth = framebuffer.framebufferTextureWidth;
		lastHeight = framebuffer.framebufferTextureHeight;
		OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, framebufferObject); // Bind Buffer then Bind Depth Texture
		OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER,
				stencil ? GL30.GL_DEPTH_STENCIL_ATTACHMENT : OpenGlHelper.GL_DEPTH_ATTACHMENT,
				GL11.GL_TEXTURE_2D,
				framebufferDepthTexture, 0);
		if (ShaderRenderLayer.Bloom.FBO != null && useDefaultFBO) {
			hookDepthTexture(ShaderRenderLayer.Bloom.FBO, framebufferDepthTexture);
		}
		OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, lastFBO);
	}

	public static void disposeDepthTexture() {
		if (framebufferDepthTexture != 0 || framebufferObject != 0) {
			if (useDefaultFBO) {
				Framebuffer framebuffer = Minecraft.getMinecraft().getFramebuffer();
				if (ShaderRenderLayer.Bloom.FBO != null) {
					hookDepthBuffer(ShaderRenderLayer.Bloom.FBO, framebuffer.depthBuffer);
				}
				hookDepthBuffer(framebuffer, framebuffer.depthBuffer);
			} else {
				OpenGlHelper.glDeleteFramebuffers(framebufferObject);
			}
			TextureUtil.deleteTexture(framebufferDepthTexture);
			framebufferObject = 0;
			framebufferDepthTexture = 0;
		}
	}

	public static void bindDepthTexture() {
		lastBind = true;
		if (useDefaultFBO && framebufferDepthTexture != 0) {
			Framebuffer framebuffer = Minecraft.getMinecraft().getFramebuffer();
			hookDepthBuffer(framebuffer, framebuffer.depthBuffer);
		}
		GlStateManager.bindTexture(framebufferDepthTexture);
	}

	public static void unBindDepthTexture() {
		GlStateManager.bindTexture(0);
		if (useDefaultFBO) {
			Framebuffer framebuffer = Minecraft.getMinecraft().getFramebuffer();
			hookDepthTexture(framebuffer, framebufferDepthTexture);
		}
	}

	public static void hookDepthBuffer(Framebuffer fbo, int depthBuffer) {
		// Hook DepthBuffer
		OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, fbo.framebufferObject);
		if (fbo.isStencilEnabled()) {
			OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, org.lwjgl.opengl.EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, OpenGlHelper.GL_RENDERBUFFER, depthBuffer);
			OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, org.lwjgl.opengl.EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, OpenGlHelper.GL_RENDERBUFFER, depthBuffer);
		}
		else {
			OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, OpenGlHelper.GL_RENDERBUFFER, depthBuffer);
		}
	}

	public static void hookDepthTexture(Framebuffer fbo, int depthTexture) {
		// Hook DepthTexture
		OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, fbo.framebufferObject);
		if (fbo.isStencilEnabled()) {
			OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0);
		} else {
			OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0);
		}
	}

	public static boolean isUseDefaultFBO() {
		return useDefaultFBO;
	}

	public static boolean isLastBind() {
		return framebufferObject != 0;
	}

}
