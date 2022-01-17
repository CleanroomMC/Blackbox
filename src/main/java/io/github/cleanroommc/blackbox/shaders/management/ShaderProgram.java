package io.github.cleanroommc.blackbox.shaders.management;

import io.github.cleanroommc.blackbox.shaders.core.mixins.ShaderLoaderExposer;
import io.github.cleanroommc.blackbox.shaders.uniform.IUniformCallback;
import io.github.cleanroommc.blackbox.shaders.uniform.UniformCache;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.client.shader.ShaderLoader;
import org.lwjgl.opengl.GL20;

import java.util.Set;

public class ShaderProgram {

	private final int programId;
	private final Set<ShaderLoader> loaders;
	private final UniformCache uniformCache;

	public ShaderProgram() {
		this.programId = GL20.glCreateProgram();
		this.loaders = new ReferenceOpenHashSet<>();
		if (this.programId == 0) {
			throw new IllegalStateException("Unable to create ShaderProgram.");
		}
		this.uniformCache = new UniformCache(this.programId);
	}

	public ShaderProgram attach(ShaderLoader loader) {
		if (this.loaders.contains(loader)) {
			throw new IllegalStateException(String.format("Unable to attach %s ShaderLoader as it is already attached.", loader.getShaderFilename()));
		}
		this.loaders.add(loader);
		GL20.glAttachShader(programId, ((ShaderLoaderExposer) loader).blackbox$getShaderId());
		return this;
	}

	public void use(IUniformCallback callback) {
		GL20.glUseProgram(programId);
		callback.apply(uniformCache);
	}

	public void release() {
		GL20.glUseProgram(0);
	}

}
