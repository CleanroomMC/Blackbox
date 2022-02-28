package com.cleanroommc.blackbox.scene;

import net.minecraft.util.BlockRenderLayer;

@FunctionalInterface
public interface SceneRenderFunction {

	void apply(boolean renderingTESR, int pass, BlockRenderLayer layer);

}
