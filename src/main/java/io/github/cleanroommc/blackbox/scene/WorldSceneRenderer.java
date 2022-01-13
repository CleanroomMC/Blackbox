package io.github.cleanroommc.blackbox.scene;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.lwjgl.util.Rectangle;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import java.lang.ref.WeakReference;
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

	private final WeakReference<World> world;
	private final Map<Collection<BlockPos>, SceneRenderFunction> renderedBlocksMap;

	private Consumer<WorldSceneRenderer> beforeRender;
	private Consumer<WorldSceneRenderer> afterRender;
	private Consumer<RayTraceResult> onLookingAt;
	private int clearColor;
	private RayTraceResult lastTraceResult;
	private Vector3f eyePos = new Vector3f(0, 0, 10f);
	private Vector3f lookAtPos = new Vector3f(0, 0, 0);
	private Vector3f worldUpPos = new Vector3f(0, 1, 0);

	public WorldSceneRenderer(World world) {
		this.world = new WeakReference<>(world);
		this.renderedBlocksMap = new Object2ObjectArrayMap<>();
	}

	public void render(float x, float y, float width, float height, int mouseX, int mouseY) {
		Rectangle rect = new Rectangle((int) x, (int) y, (int) width, (int) height);
		Rectangle mouse = new Rectangle(mouseX, mouseY, 0, 0);
		// setupCamera
		// drawWorld
		this.lastTraceResult = null;
		// Check lookAt
		if (onLookingAt != null && rect.contains(mouse)) {
			// unProject mouse
			// rayTrace above result
			// onLookingAt callback
		}
		// resetCamera
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
		return world.get();
	}

	public Map<Collection<BlockPos>, SceneRenderFunction> getRenderedBlocksMap() {
		return renderedBlocksMap;
	}

	public RayTraceResult getLastTraceResult() {
		return lastTraceResult;
	}

}
