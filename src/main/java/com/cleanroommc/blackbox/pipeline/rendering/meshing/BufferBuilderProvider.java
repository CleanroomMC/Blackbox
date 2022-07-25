package com.cleanroommc.blackbox.pipeline.rendering.meshing;

import com.cleanroommc.blackbox.pipeline.core.mixins.BufferBuilderAccessor;
import it.unimi.dsi.fastutil.objects.Reference2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.BlockRenderLayer;

public class BufferBuilderProvider {

    private static final Reference2IntMap<BlockRenderLayer> bufferSizes = new Reference2IntArrayMap<>(4);

    static {
        bufferSizes.defaultReturnValue(0x20000);
    }

    static void registerBufferProviderInitialSize(BlockRenderLayer layer, int size) {
        bufferSizes.put(layer, size);
    }

    private final BufferBuilder[] builders = new BufferBuilder[BlockRenderLayer.values().length];

    public BufferBuilderProvider() {
        for (Reference2IntMap.Entry<BlockRenderLayer> buffers : bufferSizes.reference2IntEntrySet()) {
            this.builders[buffers.getKey().ordinal()] = new BufferBuilder(buffers.getIntValue());
        }
    }

    public BufferBuilder get(BlockRenderLayer layer) {
        return this.builders[layer.ordinal()];
    }

    public void reset() {
        for (BufferBuilder builder : builders) {
            if (((BufferBuilderAccessor) builder).getIsDrawing()) {
                builder.finishDrawing();
            }
            builder.reset();
            builder.setTranslation(0D, 0D, 0D);
        }
    }

}
