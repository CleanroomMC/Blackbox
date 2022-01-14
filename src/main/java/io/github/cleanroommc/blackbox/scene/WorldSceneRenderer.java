package io.github.cleanroommc.blackbox.scene;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;
import org.lwjgl.util.glu.GLU;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

public class WorldSceneRenderer {

	protected static final FloatBuffer MODEL_VIEW_MATRIX_BUFFER = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
	protected static final FloatBuffer PROJECTION_MATRIX_BUFFER = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
	protected static final IntBuffer VIEWPORT_BUFFER = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
	protected static final FloatBuffer PIXEL_DEPTH_BUFFER = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asFloatBuffer();
	protected static final FloatBuffer OBJECT_POS_BUFFER = ByteBuffer.allocateDirect(3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

	protected static final IBlockState AIR = Blocks.AIR.getDefaultState();

	private final World world;
	private final Map<Collection<BlockPos>, SceneRenderFunction> renderedBlocksMap;

	private Consumer<WorldSceneRenderer> beforeRender;
	private Consumer<WorldSceneRenderer> afterRender;
	private Consumer<RayTraceResult> onLookingAt;
	private int clearColor;
	private RayTraceResult lastTraceResult;
	private Vector3f eyePos = new Vector3f(0, 0, 10F);
	private Vector3f lookAtPos = new Vector3f(0, 0, 0);
	private Vector3f worldUpPos = new Vector3f(0, 1, 0);

	public WorldSceneRenderer(World world) {
		this.world = world;
		this.renderedBlocksMap = new Object2ObjectArrayMap<>();
	}

	public void render(float x, float y, float width, float height, int mouseX, int mouseY) {
		Rectangle rect = new Rectangle((int) x, (int) y, (int) width, (int) height);
		setupCamera(rect);
		if (beforeRender != null) {
			beforeRender.accept(this);
		}
		drawWorld();
		if (afterRender != null) {
			afterRender.accept(this);
		}
		this.lastTraceResult = null;
		if (onLookingAt != null && rect.contains(mouseX, mouseY)) {
			onLookingAt.accept(rayTrace(unProject(mouseX, mouseY)));
		}
		resetCamera();
	}

	private void setupCamera(Rectangle rect) {
		setupCamera(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
	}

	protected void setupCamera(int x, int y, int width, int height) {
		GlStateManager.pushAttrib();
		Minecraft.getMinecraft().entityRenderer.disableLightmap();
		GlStateManager.disableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableBlend();
		// Setup viewport and clear GL buffers
		GlStateManager.viewport(x, y, width, height);
		clearView(x, y, width, height);
		// Setup projection matrix to perspective
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GLU.gluPerspective(60.0f, width / (height * 1.0f), 0.1f, 10000.0f);
		// Setup model view matrix
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GLU.gluLookAt(eyePos.x, eyePos.y, eyePos.z, lookAtPos.x, lookAtPos.y, lookAtPos.z, worldUpPos.x, worldUpPos.y, worldUpPos.z);
	}

	protected void clearView(int x, int y, int width, int height) {
		int r = (clearColor & 0xFF0000) >> 16;
		int g = (clearColor & 0xFF00) >> 8;
		int b = (clearColor & 0xFF);
		GlStateManager.clearColor(r / 255F, g / 255F, b / 255F, (clearColor >> 24) / 255F);
		GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	protected void resetCamera() {
		// Reset viewport
		GlStateManager.viewport(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		// Reset projection matrix
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		// Reset modelview matrix
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.popMatrix();
		GlStateManager.disableBlend();
		GlStateManager.disableDepth();
		// Reset attributes
		GlStateManager.popAttrib();
	}

	protected void drawWorld() {
		Minecraft mc = Minecraft.getMinecraft();
		GlStateManager.enableCull();
		GlStateManager.enableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		// mc.entityRenderer.disableLightmap();
		mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		BlockRenderLayer oldRenderLayer = MinecraftForgeClient.getRenderLayer();
		// GlStateManager.disableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();
		// Render block in each render layer
		try {
			Tessellator tessellator = Tessellator.getInstance();
			for (BlockRenderLayer layer : BlockRenderLayer.values()) {
				ForgeHooksClient.setRenderLayer(layer);
				int pass = layer == BlockRenderLayer.TRANSLUCENT ? 1 : 0;
				for (Map.Entry<Collection<BlockPos>, SceneRenderFunction> entry : renderedBlocksMap.entrySet()) {
					if (entry.getValue() != null) {
						entry.getValue().apply(false, pass, layer);
					} else {
						setDefaultPassRenderState(pass);
					}
					BufferBuilder buffer = Tessellator.getInstance().getBuffer();
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
					BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRendererDispatcher();
					for (BlockPos pos : entry.getKey()) {
						IBlockState state = world.getBlockState(pos);
						EnumBlockRenderType type = state.getRenderType();
						// Do not render states with Invisible or TESR render type
						if (type != EnumBlockRenderType.INVISIBLE && type != EnumBlockRenderType.ENTITYBLOCK_ANIMATED) {
							state = state.getActualState(world, pos);
							if (state.getBlock().canRenderInLayer(state, layer)) {
								blockrendererdispatcher.renderBlock(state, pos, world, buffer);
							}
						}
					}
					tessellator.draw();
					tessellator.getBuffer().setTranslation(0, 0, 0);
				}
			}
		} finally {
			ForgeHooksClient.setRenderLayer(oldRenderLayer);
		}
		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableLighting();
		// Render TESR
		for (int pass = 0; pass < 2; pass++) {
			ForgeHooksClient.setRenderPass(pass);
			int finalPass = pass;
			for (Map.Entry<Collection<BlockPos>, SceneRenderFunction> entry : renderedBlocksMap.entrySet()) {
				if (entry.getValue() != null) {
					entry.getValue().apply(true, finalPass, null);
				} else {
					setDefaultPassRenderState(finalPass);
				}
				for (BlockPos pos : entry.getKey()) {
					TileEntity tile = world.getTileEntity(pos);
					if (tile != null) {
						if (tile.shouldRenderInPass(finalPass)) {
							TileEntityRendererDispatcher.instance.render(tile, pos.getX(), pos.getY(), pos.getZ(), 0);
						}
					}
				}
			}
		}
		ForgeHooksClient.setRenderPass(-1);
		GlStateManager.enableDepth();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}

	protected void setDefaultPassRenderState(int pass) {
		GlStateManager.color(1, 1, 1, 1);
		if (pass == 0) { // Solid
			GlStateManager.enableDepth();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
		} else { // Translucent
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.depthMask(false);
		}
	}

	protected Vector3f project(BlockPos pos) {
		// Read current rendering parameters
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODEL_VIEW_MATRIX_BUFFER);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION_MATRIX_BUFFER);
		GL11.glGetInteger(GL11.GL_VIEWPORT, VIEWPORT_BUFFER);
		// Rewind buffers after write by OpenGL glGet calls
		MODEL_VIEW_MATRIX_BUFFER.rewind();
		PROJECTION_MATRIX_BUFFER.rewind();
		VIEWPORT_BUFFER.rewind();
		// Call gluProject with retrieved parameters
		GLU.gluProject(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, MODEL_VIEW_MATRIX_BUFFER, PROJECTION_MATRIX_BUFFER, VIEWPORT_BUFFER, OBJECT_POS_BUFFER);
		// Rewind buffers after read by gluProject
		VIEWPORT_BUFFER.rewind();
		PROJECTION_MATRIX_BUFFER.rewind();
		MODEL_VIEW_MATRIX_BUFFER.rewind();
		// Rewind buffer after write by gluProject
		OBJECT_POS_BUFFER.rewind();
		// Obtain position in Screen
		float winX = OBJECT_POS_BUFFER.get();
		float winY = OBJECT_POS_BUFFER.get();
		float winZ = OBJECT_POS_BUFFER.get();
		// Rewind buffer after read
		OBJECT_POS_BUFFER.rewind();
		return new Vector3f(winX, winY, winZ);
	}

	protected Vector3f unProject(int mouseX, int mouseY) {
		// Read depth of pixel under mouse
		GL11.glReadPixels(mouseX, mouseY, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, PIXEL_DEPTH_BUFFER);
		// Rewind buffer after write by glReadPixels
		PIXEL_DEPTH_BUFFER.rewind();
		// Retrieve depth from buffer (0.0-1.0f)
		float pixelDepth = PIXEL_DEPTH_BUFFER.get();
		// Rewind buffer after read
		PIXEL_DEPTH_BUFFER.rewind();
		// Read current rendering parameters
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODEL_VIEW_MATRIX_BUFFER);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION_MATRIX_BUFFER);
		GL11.glGetInteger(GL11.GL_VIEWPORT, VIEWPORT_BUFFER);
		// Rewind buffers after write by OpenGL glGet calls
		MODEL_VIEW_MATRIX_BUFFER.rewind();
		PROJECTION_MATRIX_BUFFER.rewind();
		VIEWPORT_BUFFER.rewind();
		// Call gluUnProject with retrieved parameters
		GLU.gluUnProject(mouseX, mouseY, pixelDepth, MODEL_VIEW_MATRIX_BUFFER, PROJECTION_MATRIX_BUFFER, VIEWPORT_BUFFER, OBJECT_POS_BUFFER);
		// Rewind buffers after read by gluUnProject
		VIEWPORT_BUFFER.rewind();
		PROJECTION_MATRIX_BUFFER.rewind();
		MODEL_VIEW_MATRIX_BUFFER.rewind();
		// Rewind buffer after write by gluUnProject
		OBJECT_POS_BUFFER.rewind();
		// Obtain absolute position in world
		float posX = OBJECT_POS_BUFFER.get();
		float posY = OBJECT_POS_BUFFER.get();
		float posZ = OBJECT_POS_BUFFER.get();
		// Rewind buffer after read
		OBJECT_POS_BUFFER.rewind();
		return new Vector3f(posX, posY, posZ);
	}

	public RayTraceResult rayTrace(Vector3f hitPos) {
		Vec3d startPos = new Vec3d(this.eyePos.x, this.eyePos.y, this.eyePos.z);
		hitPos.scale(2); // Double view range to ensure pos can be seen.
		Vec3d endPos = new Vec3d((hitPos.x - startPos.x), (hitPos.y - startPos.y), (hitPos.z - startPos.z));
		return this.world.rayTraceBlocks(startPos, endPos);
	}

	public WorldSceneRenderer setBeforeWorldRender(Consumer<WorldSceneRenderer> callback) {
		this.beforeRender = callback;
		return this;
	}

	public WorldSceneRenderer setAfterWorldRender(Consumer<WorldSceneRenderer> callback) {
		this.afterRender = callback;
		return this;
	}

	public WorldSceneRenderer addRenderedBlocks(Collection<BlockPos> blocks, SceneRenderFunction renderFunc) {
		if (blocks != null && !blocks.isEmpty()) {
			this.renderedBlocksMap.put(blocks, renderFunc);
		}
		return this;
	}

	public WorldSceneRenderer setOnLookingAt(Consumer<RayTraceResult> onLookingAt) {
		this.onLookingAt = onLookingAt;
		return this;
	}

	public void setClearColor(int clearColor) {
		this.clearColor = clearColor;
	}

	@Nullable
	public World getWorld() {
		return world;
	}

	public Map<Collection<BlockPos>, SceneRenderFunction> getRenderedBlocksMap() {
		return renderedBlocksMap;
	}

	public RayTraceResult getLastTraceResult() {
		return lastTraceResult;
	}

}
