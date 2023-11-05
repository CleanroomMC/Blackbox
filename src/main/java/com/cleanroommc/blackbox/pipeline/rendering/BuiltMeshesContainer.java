package com.cleanroommc.blackbox.pipeline.rendering;

import com.cleanroommc.blackbox.pipeline.rendering.meshing.ChunkMesh;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;

// TODO: support circular chunk loading rather than square
public class BuiltMeshesContainer {

    private final RenderGlobal renderGlobal;
    private final World world;
    private final int xzDiameter;
    private final int yDiameter;
    private final ChunkMesh[] meshes;

    public BuiltMeshesContainer(RenderGlobal renderGlobal, World world, int renderDistanceChunks) {
        this.renderGlobal = renderGlobal;
        this.world = world;
        this.xzDiameter = renderDistanceChunks * 2 + 1;
        this.yDiameter = Math.min(this.world.getHeight() / 16, this.xzDiameter);
        this.meshes = new ChunkMesh[this.xzDiameter * this.xzDiameter * this.yDiameter];
        for (int x = 0; x < this.xzDiameter; x++) {
            for (int y = 0; y < this.yDiameter; y++) {
                for (int z = 0; z < this.xzDiameter; z++) {
                    int index = (z * this.yDiameter + y) * this.xzDiameter + x;
                    this.meshes[index] = ChunkMesh.create(x, y, z);
                }
            }
        }
    }

    public ChunkMesh[] getMeshes() {
        return this.meshes;
    }

    public void updateChunkPositions(double viewEntityX, double viewEntityZ) {
        int i = MathHelper.floor(viewEntityX) - 8;
        int j = MathHelper.floor(viewEntityZ) - 8;
        int k = this.xzDiameter * 16;
        for (int l = 0; l < this.xzDiameter; l++) {
            int i1 = this.getBaseCoordinate(i, k, l);
            for (int j1 = 0; j1 < this.xzDiameter; j1++) {
                int k1 = this.getBaseCoordinate(j, k, j1);
                for (int l1 = 0; l1 < this.yDiameter; l1++) {
                    int i2 = l1 * 16;
                    ChunkMesh mesh = this.meshes[(j1 * this.yDiameter + l1) * this.xzDiameter + l];
                    mesh.setPosition(i1, i2, k1);
                }
            }
        }
    }

    private int getBaseCoordinate(int x, int y, int z) {
        int i = z * 16;
        int j = i - x + y / 2;
        if (j < 0) {
            j -= y - 1;
        }
        return i - j / y * y;
    }

    public void markBlocksForUpdate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean updateImmediately) {
        int i = MathHelper.intFloorDiv(minX, 16);
        int j = MathHelper.intFloorDiv(minY, 16);
        int k = MathHelper.intFloorDiv(minZ, 16);
        int l = MathHelper.intFloorDiv(maxX, 16);
        int i1 = MathHelper.intFloorDiv(maxY, 16);
        int j1 = MathHelper.intFloorDiv(maxZ, 16);
        for (int k1 = i; k1 <= l; ++k1) {
            int l1 = k1 % this.xzDiameter;
            if (l1 < 0) {
                l1 += this.xzDiameter;
            }
            for (int i2 = j; i2 <= i1; ++i2) {
                int j2 = i2 % this.yDiameter;
                if (j2 < 0) {
                    j2 += this.yDiameter;
                }
                for (int k2 = k; k2 <= j1; ++k2) {
                    int l2 = k2 % this.xzDiameter;
                    if (l2 < 0) {
                        l2 += this.xzDiameter;
                    }
                    int i3 = (l2 * this.yDiameter + j2) * this.xzDiameter + l1;
                    ChunkMesh mesh = this.meshes[i3];
                    // mesh.setDirty(updateImmediately);
                    mesh.setDirty();
                }
            }
        }
    }

    @Nullable
    protected ChunkMesh getChunkMesh(BlockPos pos) {
        int i = MathHelper.intFloorDiv(pos.getX(), 16);
        int j = MathHelper.intFloorDiv(pos.getY(), 16);
        int k = MathHelper.intFloorDiv(pos.getZ(), 16);
        if (j >= 0 && j < this.yDiameter) {
            i = i % this.xzDiameter;
            if (i < 0) {
                i += this.xzDiameter;
            }
            k = k % this.xzDiameter;
            if (k < 0) {
                k += this.xzDiameter;
            }
            int l = (k * this.yDiameter + j) * this.xzDiameter + i;
            return this.meshes[l];
        }
        return null;
    }

}
