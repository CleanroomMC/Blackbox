package com.cleanroommc.blackbox.shaders.uniform;

@FunctionalInterface
public interface IUniformCallback {

	void apply(UniformCache cache);

	default IUniformCallback with(IUniformCallback callback) {
		return cache -> {
			apply(cache);
			callback.apply(cache);
		};
	}


}
