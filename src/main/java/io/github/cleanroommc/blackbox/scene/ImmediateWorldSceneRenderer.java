package io.github.cleanroommc.blackbox.scene;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

public class ImmediateWorldSceneRenderer extends WorldSceneRenderer {

	public ImmediateWorldSceneRenderer(World world) {
		super(world);
	}

	@Override
	protected Rectangle getPositionedRectangle(int x, int y, int width, int height) {
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution resolution = new ScaledResolution(mc);
		// Compute window size from scaled width & height
		int windowWidth = (int) (width / (resolution.getScaledWidth() * 1.0) * mc.displayWidth);
		int windowHeight = (int) (height / (resolution.getScaledHeight() * 1.0) * mc.displayHeight);
		// Translate gui coordinates to window's ones (y is inverted)
		int windowX = (int) (x / (resolution.getScaledWidth() * 1.0) * mc.displayWidth);
		int windowY = mc.displayHeight - (int) (y / (resolution.getScaledHeight() * 1.0) * mc.displayHeight) - windowHeight;
		return super.getPositionedRectangle(windowX, windowY, windowWidth, windowHeight);
	}


	@Override
	protected void clearView(int x, int y, int width, int height) {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(x, y, width, height);
		super.clearView(x, y, width, height);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

}
