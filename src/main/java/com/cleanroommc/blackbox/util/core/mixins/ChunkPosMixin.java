package com.cleanroommc.blackbox.util.core.mixins;

import com.cleanroommc.blackbox.util.MutableChunkPos;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkPos.class)
public class ChunkPosMixin {

    @Shadow @Final public int x;
    @Shadow @Final public int z;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof MutableChunkPos) {
            MutableChunkPos mutableChunkPos = (MutableChunkPos) o;
            return this.x == mutableChunkPos.x && this.z == mutableChunkPos.z;
        } else if (o instanceof ChunkPos) {
            ChunkPos chunkpos = (ChunkPos) o;
            return this.x == chunkpos.x && this.z == chunkpos.z;
        }
        return false;
    }

}
