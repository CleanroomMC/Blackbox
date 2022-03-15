package com.cleanroommc.blackbox.model.renderer.mesh.material;

import net.minecraft.util.BlockRenderLayer;

public enum BlendMode {

    DEFAULT(null),
    SOLID(BlockRenderLayer.SOLID),
    CUTOUT_MIPPED(BlockRenderLayer.CUTOUT_MIPPED),
    CUTOUT(BlockRenderLayer.CUTOUT),
    TRANSLUCENT(BlockRenderLayer.TRANSLUCENT);

    private static final BlendMode[] MODES = values();

    public static BlendMode fromRenderLayer(BlockRenderLayer blockRenderLayer) {
        for (BlendMode mode : MODES) {
            if (mode.blockRenderLayer == blockRenderLayer) {
                return mode;
            }
        }
        return DEFAULT;
    }

    public final BlockRenderLayer blockRenderLayer;

    BlendMode(BlockRenderLayer blockRenderLayer) {
        this.blockRenderLayer = blockRenderLayer;
    }

}
