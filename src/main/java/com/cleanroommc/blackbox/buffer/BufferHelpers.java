package com.cleanroommc.blackbox.buffer;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.Framebuffer;

public class BufferHelpers {

	/**
	 * Sets up the specified BufferBuilder to draw a cube face with designated min~max x, y, z sizes and r, g, b, a
	 *
	 * @param bufferBuilder BufferBuilder instance to draw with
	 * @param minX          minX starting point
	 * @param minY          minY starting point
	 * @param minZ          minZ starting point
	 * @param maxX          maxX ending point
	 * @param maxY          maxY ending point
	 * @param maxZ          maxZ ending point
	 * @param r             red of RGBA (range 0.0 ~ 1.0)
	 * @param g             green of RGBA (range 0.0 ~ 1.0)
	 * @param b             blue of RGBA (range 0.0 ~ 1.0)
	 * @param a             alpha of RGBA (range 0.0 ~ 1.0)
	 */
	public static void renderCubeFace(BufferBuilder bufferBuilder, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float a) {
		bufferBuilder.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
		bufferBuilder.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
		bufferBuilder.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
		bufferBuilder.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();

		bufferBuilder.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
		bufferBuilder.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
		bufferBuilder.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
		bufferBuilder.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();

		bufferBuilder.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
		bufferBuilder.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
		bufferBuilder.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
		bufferBuilder.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();

		bufferBuilder.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
		bufferBuilder.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
		bufferBuilder.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
		bufferBuilder.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();

		bufferBuilder.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
		bufferBuilder.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
		bufferBuilder.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
		bufferBuilder.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();

		bufferBuilder.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
		bufferBuilder.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
		bufferBuilder.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
		bufferBuilder.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
	}

	/**
	 * Sets up the specified BufferBuilder to draw a cube face with designated min~max x, y, z sizes and r, g, b and full alpha
	 *
	 * @param bufferBuilder BufferBuilder instance to draw with
	 * @param minX          minX starting point
	 * @param minY          minY starting point
	 * @param minZ          minZ starting point
	 * @param maxX          maxX ending point
	 * @param maxY          maxY ending point
	 * @param maxZ          maxZ ending point
	 * @param r             red of RGBA (range 0.0 ~ 1.0)
	 * @param g             green of RGBA (range 0.0 ~ 1.0)
	 * @param b             blue of RGBA (range 0.0 ~ 1.0)
	 */
	public static void renderCubeFace(BufferBuilder bufferBuilder, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b) {
		renderCubeFace(bufferBuilder, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, 1F);
	}

	/**
	 * Sets up the {@link Tessellator#getBuffer()} to draw a cube face with designated min~max x, y, z sizes and r, g, b, a
	 * @param minX          minX starting point
	 * @param minY          minY starting point
	 * @param minZ          minZ starting point
	 * @param maxX          maxX ending point
	 * @param maxY          maxY ending point
	 * @param maxZ          maxZ ending point
	 * @param r             red of RGBA (range 0.0 ~ 1.0)
	 * @param g             green of RGBA (range 0.0 ~ 1.0)
	 * @param b             blue of RGBA (range 0.0 ~ 1.0)
	 * @param a             alpha of RGBA (range 0.0 ~ 1.0)
	 */
	public static void renderCubeFace(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float a) {
		renderCubeFace(Tessellator.getInstance().getBuffer(), minX, minY, minZ, maxX, maxY, maxZ, r, g, b, a);
	}

	/**
	 * Sets up the {@link Tessellator#getBuffer()} to draw a cube face with designated min~max x, y, z sizes and r, g, b and full alpha
	 * @param minX          minX starting point
	 * @param minY          minY starting point
	 * @param minZ          minZ starting point
	 * @param maxX          maxX ending point
	 * @param maxY          maxY ending point
	 * @param maxZ          maxZ ending point
	 * @param r             red of RGBA (range 0.0 ~ 1.0)
	 * @param g             green of RGBA (range 0.0 ~ 1.0)
	 * @param b             blue of RGBA (range 0.0 ~ 1.0)
	 */
	public static void renderCubeFace(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b) {
		renderCubeFace(minX, minY, minZ, maxX, maxY, maxZ, r, g, b, 1F);
	}

	public static boolean updateFramebufferSize(Framebuffer fbo, int width, int height) {
		if (fbo.framebufferWidth != width || fbo.framebufferHeight != height) {
			fbo.createBindFramebuffer(width, height);
			return true;
		}
		return false;
	}

	private BufferHelpers() { }

}
