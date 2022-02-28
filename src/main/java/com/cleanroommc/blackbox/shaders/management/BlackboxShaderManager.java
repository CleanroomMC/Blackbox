package com.cleanroommc.blackbox.shaders.management;

import com.cleanroommc.blackbox.Blackbox;
import com.cleanroommc.blackbox.shaders.ShaderLoaders;
import com.cleanroommc.blackbox.shaders.uniform.UniformCache;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderLoader;

import java.lang.reflect.Field;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Blackbox's own rollout of a ShaderManager. Vanilla's ShaderManager is too restrictive.
 */
public class BlackboxShaderManager {

	private static final BooleanSupplier optifine$shaderPackLoaded;

	static {
		Field shaderPackLoadedField = null;
		try {
			Class<?> shadersClass = Class.forName("net.optifine.shaders.Shaders");
			shaderPackLoadedField = shadersClass.getDeclaredField("shaderPackLoaded");
		} catch (Exception ignored) {
			Blackbox.LOGGER.debug("Cannot detect Optifine, not going to do any specific compatibility patches.");
		}
		if (shaderPackLoadedField == null) {
			optifine$shaderPackLoaded = () -> false;
		} else {
			Field finalShaderPackLoadedField = shaderPackLoadedField;
			optifine$shaderPackLoaded = () -> {
				try {
					return finalShaderPackLoadedField.getBoolean(null);
				} catch (IllegalAccessException ignored) { }
				return false;
			};
		}
	}

	private static final BlackboxShaderManager INSTANCE = new BlackboxShaderManager();

	public static BlackboxShaderManager getInstance() {
		return INSTANCE;
	}

	public static boolean isShadersCompatible() {
		return OpenGlHelper.areShadersSupported() && !optifine$shaderPackLoaded.getAsBoolean();
	}

	private final Reference2ReferenceMap<ShaderLoader, ShaderProgram> programs;

	private BlackboxShaderManager() {
		this.programs = new Reference2ReferenceOpenHashMap<>();
	}

	public Framebuffer renderFullImageInFramebuffer(Framebuffer fbo, ShaderLoader frag, Consumer<UniformCache> consumeCache) {
		if (fbo == null || frag == null || !isShadersCompatible()) {
			return fbo;
		}
		// int lastID = glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
		fbo.bindFramebuffer(true);
		ShaderProgram program = programs.get(frag);
		if (program == null) {
			programs.put(frag, program = new ShaderProgram());
			program.attach(ShaderLoaders.IMAGE_V);
			program.attach(frag);
		}
		program.use(cache -> {
			cache.glUniform2F("u_resolution", fbo.framebufferWidth, fbo.framebufferHeight);
			if (consumeCache != null) {
				consumeCache.accept(cache);
			}
		});
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(-1, 1, 0).tex(0, 0).endVertex();
		buffer.pos(-1, -1, 0).tex(0, 1).endVertex();
		buffer.pos(1, -1, 0).tex(1, 1).endVertex();
		buffer.pos(1, 1, 0).tex(1, 0).endVertex();
		tessellator.draw();
		program.release();
		// GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);
		// OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, lastID);
		return fbo;
	}

}
