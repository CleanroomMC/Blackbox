package com.cleanroommc.blackbox.pipeline.rendering.quad.lighting;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.IBlockAccess;

public abstract class LightDataAccess {

    public static long packOP(boolean opaque) {
        return (opaque ? 1L : 0L) << 56;
    }

    public static boolean unpackOP(long word) {
        return ((word >>> 56) & 0b1) != 0;
    }

    public static long packFO(boolean opaque) {
        return (opaque ? 1L : 0L) << 57;
    }

    public static boolean unpackFO(long word) {
        return ((word >>> 57) & 0b1) != 0;
    }

    public static long packLM(int lm) {
        return (long) lm & 0xFFFFFFFFL;
    }

    public static int unpackLM(long word) {
        return (int) (word & 0xFFFFFFFFL);
    }

    public static long packAO(float ao) {
        int aoi = (int) (ao * 4096.0f);
        return ((long) aoi & 0xFFFFL) << 32;
    }

    public static float unpackAO(long word) {
        int aoi = (int) (word >>> 32 & 0xFFFFL);
        return aoi * (1.0f / 4096.0f);
    }

    private final MutableBlockPos pos = new MutableBlockPos();

    protected IBlockAccess world;

    public IBlockAccess getWorld() {
        return this.world;
    }

    protected long compute(int x, int y, int z) {
        BlockPos pos = this.pos.setPos(x, y, z);
        IBlockAccess world = this.world;
        IBlockState state = world.getBlockState(pos);

        float ao = state.getLightValue(world, pos) == 0 ? state.getBlock().getAmbientOcclusionLightValue(state) : 1.0F;

        // FIX: Fluids are always non-translucent despite blocking light, so we need a special check here in order to
        // solve lighting issues underwater.
        // TODO: Fluidlogged API Integration - as we rewrite the fluid renderer
        boolean op = /*state.getFluidState() != EMPTY_FLUID_STATE || */ state.getBlock().getLightOpacity(state, world, pos) == 0;
        boolean fo = state.isOpaqueCube() && state.isFullCube(); // TODO: clarify
        // boolean em = state.hasEmissiveLighting(world, pos); // TODO: gather data for if the block is emissive or not here? Or special case it later down the pipeline

        // OPTIMIZE: Do not calculate lightmap data if the block is full and opaque.
        // FIX: Calculate lightmap data for emissive blocks (currently only magma), even though they are full and opaque.
        int lm = (fo/* && !em*/) ? 0 : state.getPackedLightmapCoords(world, pos);

        return packAO(ao) | packLM(lm) | packOP(op) | packFO(fo) | (1L << 60);
    }

    /**
     * Returns the light data for the block at the given position. The property fields can then be accessed using
     * the various unpack methods below.
     */
    public abstract long get(int x, int y, int z);

    public long get(int x, int y, int z, EnumFacing dir1, EnumFacing dir2) {
        return this.get(x + dir1.getXOffset() + dir2.getXOffset(), y + dir1.getYOffset() + dir2.getYOffset(), z + dir1.getZOffset() + dir2.getZOffset());
    }

    public long get(int x, int y, int z, EnumFacing dir) {
        return this.get(x + dir.getXOffset(), y + dir.getYOffset(), z + dir.getZOffset());
    }

    public long get(BlockPos pos, EnumFacing dir) {
        return this.get(pos.getX(), pos.getY(), pos.getZ(), dir);
    }

    public long get(BlockPos pos) {
        return this.get(pos.getX(), pos.getY(), pos.getZ());
    }

}
