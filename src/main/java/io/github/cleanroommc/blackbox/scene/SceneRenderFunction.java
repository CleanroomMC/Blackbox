package io.github.cleanroommc.blackbox.scene;

import net.minecraft.util.BlockRenderLayer;

@FunctionalInterface
public interface SceneRenderFunction {

	void apply(boolean isTESR, int pass, BlockRenderLayer layer);

}
