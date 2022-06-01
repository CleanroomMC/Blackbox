package com.cleanroommc.blackbox.pipeline.meshing;

import com.cleanroommc.blackbox.pipeline.ClientObjects;
import com.cleanroommc.blackbox.pipeline.lighting.DynamicLightingHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;

public class BlackboxChunkCache extends ChunkCache {

    private static final TrackedChunkObjects NULL_TRACKER = new TrackedChunkObjects();
    private static final ThreadLocal<Biome[]> trackedBiomes = ThreadLocal.withInitial(() -> new Biome[48 * 48]);
    private static final ThreadLocal<TrackedChunkObjects[]> trackedObjs = ThreadLocal.withInitial(() -> new TrackedChunkObjects[48 * 48 * 48]);
    private static final IBlockState AIR = ClientObjects.AIR_STATE;

    static {
        NULL_TRACKER.state = AIR;
        NULL_TRACKER.skyLight = EnumSkyBlock.SKY.defaultLightValue;
        NULL_TRACKER.blockLight = EnumSkyBlock.BLOCK.defaultLightValue;
    }

    private final int posX, posY, posZ;

    private Thread cachedThread;
    private Biome[] cachedBiomes;
    private TrackedChunkObjects[] cachedTrackedObjs;

    public BlackboxChunkCache(World world, BlockPos posFrom, BlockPos posTo, int subtract) {
        super(world, posFrom, posTo, subtract);
        this.posX = ((posFrom.getX() - subtract) >> 4) << 4;
        this.posY = ((posFrom.getY() - subtract) >> 4) << 4;
        this.posZ = ((posFrom.getZ() - subtract) >> 4) << 4;
    }

    public void clearThreadCaches() {
        if (this.cachedThread != null) {
            this.cachedThread = null;
            if (this.cachedBiomes != null) {
                Arrays.fill(this.cachedBiomes, null);
                this.cachedBiomes = null;
            }
            if (this.cachedTrackedObjs != null) {
                Arrays.fill(this.cachedTrackedObjs, null);
                this.cachedTrackedObjs = null;
            }
        }
    }

    public World getWorld() {
        return this.world;
    }

    @Override
    @Nullable
    public TileEntity getTileEntity(BlockPos pos) {
        return getOrCreateTracker(pos).tile;
    }

    @Override
    @Nullable
    public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType createType) {
        return getOrCreateTracker(pos).tile;
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        TrackedChunkObjects tracker = getOrCreateTracker(pos);
        int blockLight = tracker.blockLight;
        if (blockLight < lightValue) {
            blockLight = lightValue;
        }
        return DynamicLightingHandler.INSTANCE.getDynamicCombinedLight(pos, tracker.skyLight << 20 | blockLight << 4);
    }
    
    @Override
    public IBlockState getBlockState(BlockPos pos) {
        return getOrCreateTracker(pos).state;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Biome getBiome(BlockPos pos) {
        int i = (pos.getX() >> 4) - this.chunkX;
        int j = (pos.getZ() >> 4) - this.chunkZ;
        if (!withinBounds(i, j)) {
            return Biomes.PLAINS;
        }
        int index = getBiomeArrayIndex(pos);
        if (index > -1) {
            Biome[] biomeTracker = this.getBiomeArray();
            Biome trackedBiome = biomeTracker[index];
            if (trackedBiome == null) {
                biomeTracker[index] = trackedBiome = this.chunkArray[i][j].getBiome(pos, this.world.getBiomeProvider());
            }
            return trackedBiome;
        }
        return this.chunkArray[i][j].getBiome(pos, this.world.getBiomeProvider());
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return getOrCreateTracker(pos).state.getStrongPower(this.world, pos, direction);
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        TrackedChunkObjects tracker = getOrCreateTracker(pos);
        if (tracker == NULL_TRACKER) {
            return _default;
        }
        return tracker.state.getBlock().isSideSolid(tracker.state, this, pos, side);
    }

    private TrackedChunkObjects getOrCreateTracker(BlockPos pos) {
        int index = this.getArrayIndex(pos);
        if (index > -1) {
            TrackedChunkObjects[] trackedObjects = this.getTrackedObjsArray();
            TrackedChunkObjects tracker = trackedObjects[index];
            if (tracker != null && tracker.state != null) {
                return tracker;
            }
            int i = (pos.getX() >> 4) - this.chunkX;
            int j = (pos.getZ() >> 4) - this.chunkZ;
            if (withinBounds(i, j)) {
                Chunk chunk = this.chunkArray[i][j];
                if (chunk != null) {
                    tracker = new TrackedChunkObjects();
                    IBlockState state = chunk.getBlockState(pos);
                    tracker.state = state;
                    tracker.tile = state == AIR ? null : chunk.getTileEntity(pos, EnumCreateEntityType.CHECK);
                    if (!tracker.initLight) {
                        boolean skylight = this.world.provider.hasSkyLight();
                        if (!skylight) {
                            tracker.skyLight = 0;
                        }
                        if (pos.getY() > 0 && pos.getY() < 256) {
                            if (state.useNeighborBrightness()) {
                                int totalSkyLight = 0, totalBlockLight = 0;
                                for (EnumFacing facing : EnumFacing.VALUES) {
                                    TrackedChunkObjects faceObj = getOrCreateLightTrackedObject(pos.offset(facing));
                                    if (totalBlockLight < 15) {
                                        if (faceObj.blockLight > totalBlockLight) {
                                            totalBlockLight = faceObj.blockLight;
                                        }
                                    }
                                    if (totalSkyLight < 15) {
                                        if (faceObj.skyLight > totalSkyLight) {
                                            totalSkyLight = faceObj.skyLight;
                                        }
                                    }
                                }
                                tracker.skyLight = totalSkyLight;
                                tracker.blockLight = totalBlockLight;
                            } else {
                                tracker.skyLight = chunk.getLightFor(EnumSkyBlock.SKY, pos);
                                tracker.blockLight = chunk.getLightFor(EnumSkyBlock.BLOCK, pos);
                            }
                        } else if (skylight) {
                            tracker.skyLight = EnumSkyBlock.SKY.defaultLightValue;
                            tracker.blockLight = EnumSkyBlock.BLOCK.defaultLightValue;
                        } else {
                            tracker.blockLight = EnumSkyBlock.BLOCK.defaultLightValue;
                        }
                        trackedObjects[index] = tracker;
                    }
                    return tracker;
                }
            }
        }
        return NULL_TRACKER;
    }

    private TrackedChunkObjects getOrCreateLightTrackedObject(BlockPos pos) {
        int index = this.getArrayIndex(pos);
        if (index > -1) {
            TrackedChunkObjects[] trackedObjects = this.getTrackedObjsArray();
            TrackedChunkObjects tracker = trackedObjects[index];
            if (tracker != null) {
                return tracker;
            }
            tracker = new TrackedChunkObjects();
            int i = (pos.getX() >> 4) - this.chunkX;
            int j = (pos.getZ() >> 4) - this.chunkZ;
            tracker.initLight = true;
            if (withinBounds(i, j)) {
                Chunk chunk = this.chunkArray[i][j];
                if (chunk != null) {
                    tracker.skyLight = chunk.getLightFor(EnumSkyBlock.SKY, pos);
                    tracker.blockLight = chunk.getLightFor(EnumSkyBlock.BLOCK, pos);
                    return tracker;
                }
            }
            tracker.skyLight = EnumSkyBlock.SKY.defaultLightValue;
            tracker.blockLight = EnumSkyBlock.BLOCK.defaultLightValue;
            trackedObjects[index] = tracker;
            return tracker;
        }
        return NULL_TRACKER;
    }

    private int getArrayIndex(BlockPos pos) {
        int x = pos.getX() - this.posX;
        if (x >= 0 && x < 48) {
            int y = pos.getY() - this.posY;
            if (y >= 0 && y < 48) {
                int z = pos.getZ() - this.posZ;
                if (z >= 0 && z < 48) {
                    return x + (y * 48) + (z * 2304);
                }
            }
        }
        return -1;
    }

    private int getBiomeArrayIndex(BlockPos pos) {
        int x = pos.getX() - this.posX;
        if (x >= 0 && x < 48) {
            int z = pos.getZ() - this.posZ;
            if (z >= 0 && z < 48) {
                return x + (z * 48);
            }
        }
        return -1;
    }

    private TrackedChunkObjects[] getTrackedObjsArray() {
        if (this.cachedThread == null) {
            this.cachedThread = Thread.currentThread();
        }
        if (this.cachedTrackedObjs == null) {
            return this.cachedTrackedObjs = trackedObjs.get();
        }
        Thread currentThread = Thread.currentThread();
        if (this.cachedThread == currentThread) {
            return this.cachedTrackedObjs;
        }
        this.cachedThread = currentThread;
        return this.cachedTrackedObjs = trackedObjs.get();
    }

    private Biome[] getBiomeArray() {
        if (this.cachedThread == null) {
            this.cachedThread = Thread.currentThread();
        }
        if (this.cachedBiomes == null) {
            return this.cachedBiomes = trackedBiomes.get();
        }
        Thread currentThread = Thread.currentThread();
        if (this.cachedThread == currentThread) {
            return this.cachedBiomes;
        }
        this.cachedThread = currentThread;
        return this.cachedBiomes = trackedBiomes.get();
    }

    private boolean withinBounds(int x, int z) {
        return x >= 0 && x < this.chunkArray.length && z >= 0 && z < this.chunkArray[x].length && this.chunkArray[x][z] != null;
    }

    private static class TrackedChunkObjects {

        private IBlockState state;
        private TileEntity tile;
        private boolean initLight;
        private int skyLight, blockLight;

    }

}
