package com.cleanroommc.blackbox.shaders.management;

import com.cleanroommc.blackbox.shaders.core.mixins.ShaderLoaderExposer;
import com.cleanroommc.blackbox.shaders.uniform.IUniformCallback;
import com.cleanroommc.blackbox.shaders.uniform.UniformCache;
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
		this.uniformCache.invalidate();
		GL20.glLinkProgram(programId);
		GL20.glUseProgram(programId);
		callback.apply(uniformCache);
	}

	public void release() {
		GL20.glUseProgram(0);
	}

}
