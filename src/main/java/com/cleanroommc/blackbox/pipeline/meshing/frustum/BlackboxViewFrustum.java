package com.cleanroommc.blackbox.pipeline.meshing.frustum;

import com.cleanroommc.blackbox.pipeline.meshing.ChunkMesh;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.world.World;

public class BlackboxViewFrustum {

    private final RenderGlobal renderGlobal;
    private final World world;
    private final int xzDiameter;
    private final int yDiameter;
    private final ChunkMesh[] renderChunks;

    public BlackboxViewFrustum(RenderGlobal renderGlobal, World world, int renderDistanceChunks) {
        this.renderGlobal = renderGlobal;
        this.world = world;
        this.xzDiameter = renderDistanceChunks * 2 + 1;
        this.yDiameter = Math.min(this.world.getHeight() / 16, this.xzDiameter);
        this.renderChunks = new ChunkMesh[this.xzDiameter * this.xzDiameter * this.yDiameter];
        for (int x = 0; x < this.xzDiameter; x++) {
            for (int y = 0; y < this.yDiameter; y++) {
                for (int z = 0; z < this.xzDiameter; z++) {
                    int index = (z * this.yDiameter + y) * this.xzDiameter + x;
                    this.renderChunks[index] = new ChunkMesh(x, y, z);
                }
            }
        }
    }

}
