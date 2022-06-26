package com.cleanroommc.blackbox.pipeline.rendering.quad.flags;

import com.cleanroommc.blackbox.pipeline.rendering.quad.BakedQuadExtension;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;

public class BakedQuadFlags {

    /**
     * Indicates that the quad is aligned to the block grid.
     */
    public static final int GRID_ALIGNED = 0b01;

    /**
     * Indicates that the quad does not fully cover the given face for the model.
     */
    public static final int PARTIALLY_COVER = 0b10;

    /**
     * @return True if the bit-flag of {@link BakedQuadFlags} contains the given flag
     */
    public static boolean contains(int flags, int mask) {
        return (flags & mask) != 0;
    }

    /**
     * Calculates the properties of the given quad. This data is used later by the light pipeline in order to make certain optimizations.
     */
    public static int getQuadFlags(BakedQuad bakedQuad) {
        BakedQuadExtension quad = (BakedQuadExtension) bakedQuad;
        EnumFacing face = bakedQuad.getFace();

        float minX = 32.0F;
        float minY = 32.0F;
        float minZ = 32.0F;

        float maxX = -32.0F;
        float maxY = -32.0F;
        float maxZ = -32.0F;

        for (int i = 0; i < 4; ++i) {
            float x = quad.getX(i);
            float y = quad.getY(i);
            float z = quad.getZ(i);

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
        }

        boolean aligned = false;
        boolean partial = false;

        switch (face) {
            case DOWN:
                aligned = minY == maxY && minY < 0.0001F;
                partial = minX >= 0.0001F || minZ >= 0.0001F || maxX <= 0.9999F || maxZ <= 0.9999F;
                break;
            case UP:
                aligned = minY == maxY && maxY > 0.9999F;
                partial = minX >= 0.0001F || minZ >= 0.0001F || maxX <= 0.9999F || maxZ <= 0.9999F;
                break;
            case NORTH:
                aligned = minZ == maxZ && minZ < 0.0001F;
                partial = minX >= 0.0001F || minY >= 0.0001F || maxX <= 0.9999F || maxY <= 0.9999F;
                break;
            case SOUTH:
                aligned = minZ == maxZ && maxZ > 0.9999F;
                partial = minX >= 0.0001F || minY >= 0.0001F || maxX <= 0.9999F || maxY <= 0.9999F;
                break;
            case WEST:
                aligned = minX == maxX && minX < 0.0001F;
                partial = minY >= 0.0001F || minZ >= 0.0001F || maxY <= 0.9999F || maxZ <= 0.9999F;
                break;
            case EAST:
                aligned = minX == maxX && maxX > 0.9999F;
                partial = minY >= 0.0001F || minZ >= 0.0001F || maxY <= 0.9999F || maxZ <= 0.9999F;
                break;
        }

        int flags = 0;
        if (partial) {
            flags |= PARTIALLY_COVER;
        }
        if (aligned) {
            flags |= GRID_ALIGNED;
        }
        return flags;
    }

}
