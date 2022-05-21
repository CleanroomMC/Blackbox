package com.cleanroommc.blackbox.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class MutableChunkPos extends ChunkPos {

    public int x, z;

    public MutableChunkPos(int x, int z) {
        super(-1, -1);
        this.x = x;
        this.z = z;
    }

    public MutableChunkPos(BlockPos pos) {
        super(-1, -1);
        this.x = pos.getX() >> 4;
        this.z = pos.getZ() >> 4;
    }

    public MutableChunkPos setPos(int x, int z) {
        this.x = x;
        this.z = z;
        return this;
    }

    public MutableChunkPos setPos(BlockPos pos) {
        this.x = pos.getX() >> 4;
        this.z = pos.getZ() >> 4;
        return this;
    }

    @Override
    public double getDistanceSq(Entity entity) {
        double d0 = this.x * 16 + 8;
        double d1 = this.z * 16 + 8;
        double d2 = d0 - entity.posX;
        double d3 = d1 - entity.posZ;
        return d2 * d2 + d3 * d3;
    }

    @Override
    public int getXStart() {
        return this.x << 4;
    }

    @Override
    public int getZStart() {
        return this.z << 4;
    }

    @Override
    public int getXEnd() {
        return (this.x << 4) + 15;
    }

    @Override
    public int getZEnd() {
        return (this.z << 4) + 15;
    }

    @Override
    public BlockPos getBlock(int x, int y, int z) {
        return new BlockPos((this.x << 4) + x, y, (this.z << 4) + z);
    }

    @Override
    public int hashCode() {
        return (1664525 * this.x + 1013904223) ^ (1664525 * (this.z ^ -559038737) + 1013904223);
    }

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

    @Override
    public String toString() {
        return "[" + this.x + ", " + this.z + "]";
    }

}
