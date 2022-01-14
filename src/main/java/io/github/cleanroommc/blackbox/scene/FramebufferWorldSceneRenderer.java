package io.github.cleanroommc.blackbox.scene;

import io.github.cleanroommc.blackbox.Blackbox;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector3f;

public class FramebufferWorldSceneRenderer extends WorldSceneRenderer {

	private int resolutionWidth = 1080;
	private int resolutionHeight = 1080;
	private Framebuffer framebufferObject;

	public FramebufferWorldSceneRenderer(World world, int resolutionWidth, int resolutionHeight) {
		super(world);
		setFramebufferSize(resolutionWidth, resolutionHeight);
	}

	public FramebufferWorldSceneRenderer(World world, Framebuffer framebufferObject) {
		super(world);
		this.framebufferObject = framebufferObject;
	}

	public int getResolutionWidth() {
		return resolutionWidth;
	}

	public int getResolutionHeight() {
		return resolutionHeight;
	}

	public void setFramebufferSize(int resolutionWidth, int resolutionHeight) {
		this.resolutionWidth = resolutionWidth;
		this.resolutionHeight = resolutionHeight;
		try {
			replaceFramebuffer(resolutionWidth, resolutionHeight, true);
		} catch (Exception e) {
			Blackbox.LOGGER.fatal(e);
		}
	}

	public void replaceFramebuffer(int resolutionWidth, int resolutionHeight, boolean depth) {
		if (framebufferObject != null) {
			framebufferObject.deleteFramebuffer();
		}
		framebufferObject = new Framebuffer(resolutionWidth, resolutionHeight, depth);
	}

	public void releaseFramebuffer() {
		if (framebufferObject != null) {
			framebufferObject.deleteFramebuffer();
		}
		framebufferObject = null;
	}

	@Override
	public void render(float x, float y, float width, float height, int mouseX, int mouseY) {
		// Bind to Framebuffer Object
		int lastID = bindFramebuffer();
		super.render(x, y, width, height, mouseX, mouseY);
		unbindFramebuffer(lastID);
		// Bind Framebuffer Object as Texture
		GlStateManager.enableTexture2D();
		GlStateManager.disableLighting();
		lastID = GL11.glGetInteger(GL11.GL_TEXTURE_2D);
		GlStateManager.bindTexture(framebufferObject.framebufferTexture);
		GlStateManager.color(1F, 1F, 1F, 1F);
		// Render rectangle with the Framebuffer Object (texture)
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x + width, y + height, 0).tex(1, 0).endVertex();
		bufferbuilder.pos(x + width, y, 0).tex(1, 1).endVertex();
		bufferbuilder.pos(x, y, 0).tex(0, 1).endVertex();
		bufferbuilder.pos(x, y + height, 0).tex(0, 0).endVertex();
		tessellator.draw();
		GlStateManager.bindTexture(lastID);
	}

	public RayTraceResult screenPosition2BlockPosition(int mouseX, int mouseY) {
		int lastID = bindFramebuffer();
		// Render a frame
		GlStateManager.enableDepth();
		setupCamera(0, 0, this.resolutionWidth, this.resolutionHeight);
		drawWorld();
		Vector3f hitPos = unProject(mouseX, mouseY);
		RayTraceResult looking = rayTrace(hitPos);
		resetCamera();
		unbindFramebuffer(lastID);
		return looking;
	}

	public Vector3f blockPosition2ScreenPosition(BlockPos pos, boolean depth) {
		int lastID = bindFramebuffer();
		// Render a frame
		GlStateManager.enableDepth();
		setupCamera(0, 0, this.resolutionWidth, this.resolutionHeight);
		drawWorld();
		Vector3f winPos = project(pos);
		resetCamera();
		unbindFramebuffer(lastID);
		return winPos;
	}

	protected int bindFramebuffer() {
		int lastID = GL11.glGetInteger(EXTFramebufferObject.GL_FRAMEBUFFER_BINDING_EXT);
		framebufferObject.setFramebufferColor(0F, 0F, 0F, 0F);
		framebufferObject.framebufferClear();
		framebufferObject.bindFramebuffer(true);
		GlStateManager.pushMatrix();
		return lastID;
	}

	protected void unbindFramebuffer(int lastID) {
		GlStateManager.popMatrix();
		framebufferObject.unbindFramebufferTexture();
		OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, lastID);
	}

}
