package com.cleanroommc.blackbox.pipeline.rendering.meshing;

import com.cleanroommc.blackbox.pipeline.rendering.meshing.thread.LocalMeshObject;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;

import javax.annotation.Nullable;

public class BlackboxChunkCache implements IBlockAccess {

    private static final IBlockState AIR = Blocks.AIR.getDefaultState();
    private static final LocalMeshObject<MutableBlockPos> MUTABLE_POS = LocalMeshObject.with(MutableBlockPos::new);

    private final World world;
    private final int chunkX, chunkY, chunkZ;
    private final Chunk[] chunks;

    public BlackboxChunkCache(World world, int chunkX, int chunkY, int chunkZ) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.chunkZ = chunkZ;
        this.chunks = new Chunk[9];
        for (int x = chunkX - 1; x <= chunkX + 1; x++) {
            for (int z = chunkZ - 1; z <= chunkZ + 1; z++) {
                Chunk chunk = world.getChunkProvider().getLoadedChunk(x, z);
                if (chunk != null) {
                    this.chunks[getIndex(x, z)] = chunk;
                }
            }
        }
    }

    @Nullable
    public Chunk getChunk(BlockPos pos) {
        return this.chunks[this.getIndex(pos)];
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        if (this.withinBounds(pos)) {
            Chunk chunk = this.getChunk(pos);
            if (chunk != null) {
                return chunk.getTileEntity(pos, EnumCreateEntityType.CHECK);
            }
        }
        return null;
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return 0;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        if (this.withinBounds(pos)) {
            Chunk chunk = this.getChunk(pos);
            if (chunk != null) {
                return chunk.getBlockState(pos);
            }
        }
        return AIR;
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        IBlockState state = this.getBlockState(pos);
        return state == AIR || state.getBlock().isAir(state, this, pos);
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        if (this.withinBounds(pos)) {
            Chunk chunk = this.getChunk(pos);
            if (chunk != null) {
                return chunk.getBiome(pos, this.world.getBiomeProvider());
            }
        }
        return Biomes.PLAINS;
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return this.getBlockState(pos).getStrongPower(this, pos, direction);
    }

    @Override
    public WorldType getWorldType() {
        return this.world.getWorldType();
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean defaultValue) {
        if (this.withinBounds(pos)) {
            return this.getBlockState(pos).isSideSolid(this, pos, side);
        }
        return defaultValue;
    }

    private int getLightFor(EnumSkyBlock type, BlockPos pos) {
        if (this.withinBounds(pos)) {
            Chunk chunk = this.getChunk(pos);
            if (chunk != null) {
                return chunk.getLightFor(type, pos);
            }
        }
        return type.defaultLightValue;
    }

    private int getLightForExt(EnumSkyBlock type, BlockPos pos) {
        if (type == EnumSkyBlock.SKY && !this.world.provider.hasSkyLight()) {
            return 0;
        } else if (this.withinBounds(pos)) {
            if (this.getBlockState(pos).useNeighborBrightness()) {
                int l = 0;
                MutableBlockPos mutablePos = MUTABLE_POS.get();
                for (EnumFacing enumfacing : EnumFacing.values()) {
                    int k = this.getLightFor(type, mutablePos.setPos(pos).move(enumfacing));
                    if (k > l) {
                        l = k;
                    }
                    if (l >= 15) {
                        return l;
                    }
                }
                return l;
            } else {
                Chunk chunk = this.getChunk(pos);
                if (chunk != null) {
                    return chunk.getLightFor(type, pos);
                }
            }
        }
        return type.defaultLightValue;
    }

    private int getIndex(BlockPos pos) {
        return getIndex(pos.getX() >> 4, pos.getZ() >> 4);
    }

    private int getIndex(int chunkX, int chunkZ) {
        return (chunkZ - this.chunkZ + 1) * 9 + (chunkX - this.chunkX + 1);
    }

    private boolean withinBounds(BlockPos pos) {
        return withinBounds(pos.getX(), pos.getY(), pos.getZ());
    }

    private boolean withinBounds(int x, int y, int z) {
        return ((this.chunkX - 1) << 4) <= x && (((this.chunkX + 1) << 4) + 15) <= x &&
                ((this.chunkY - 1) << 4) <= y && (((this.chunkY + 1) << 4) + 15) <= y &&
                ((this.chunkZ - 1) << 4) <= z && (((this.chunkZ + 1) << 4) + 15) <= z;
    }

}
