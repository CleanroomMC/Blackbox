package com.cleanroommc.blackbox.pipeline.lighting;

import com.cleanroommc.blackbox.config.category.lighting.DynamicLightingConfig;
import com.cleanroommc.blackbox.pipeline.core.ILitEntity;
import com.cleanroommc.blackbox.pipeline.core.IRenderGlobalExpansion;
import com.cleanroommc.blackbox.notifiers.IEntityStatusNotifier;
import com.cleanroommc.blackbox.notifiers.IRenderGlobalNotifier;
import com.google.common.base.Predicates;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public enum DynamicLightingHandler implements IEntityStatusNotifier, IRenderGlobalNotifier {

    INSTANCE;

    private final Reference2IntMap<Item> itemMapping = new Reference2IntOpenHashMap<>(16);
    private final Set<LitInstance> instances = new ReferenceOpenHashSet<>(256);

    private long renderTime = 0L;
    private Map<Class<Entity>, Pair<Predicate<Entity>, ToIntFunction<Entity>>> customCriteria;

    DynamicLightingHandler() {
        this.itemMapping.defaultReturnValue(0);
    }

    public <T extends Entity> void addEntityCriteria(Class<T> clazz, Predicate<T> criteria, ToIntFunction<T> light) {
        if (this.customCriteria == null) {
            this.customCriteria = new Reference2ObjectOpenHashMap<>();
        }
        this.customCriteria.put((Class<Entity>) clazz, Pair.of((Predicate<Entity>) criteria, (ToIntFunction<Entity>) light));
    }

    public <T extends Entity> LitInstance addLitInstance(T entity, int lighting, Predicate<T> validityCheck) {
        LitInstance instance = new LitInstance(entity, lighting, (Predicate<Entity>) validityCheck);
        ((ILitEntity) entity).setLitInstance(instance);
        this.instances.add(instance);
        return instance;
    }

    public void addLitInstance(Entity entity, int lighting) {
        addLitInstance(entity, lighting, Predicates.alwaysTrue());
    }

    public void updateTick(Entity entity) {
        LitInstance instance = ((ILitEntity) entity).getLitInstance();
        if (instance == null) {
            if (this.customCriteria != null) {
                Pair<Predicate<Entity>, ToIntFunction<Entity>> outcome = this.customCriteria.get(entity.getClass());
                if (outcome != null && outcome.getLeft().test(entity)) {
                    this.addLitInstance(entity, outcome.getRight().applyAsInt(entity), outcome.getLeft());
                    return;
                }
            }
            if (entity.isBurning()) {
                this.addLitInstance(entity, 15, Entity::isBurning);
            } else if (entity instanceof EntityLiving) {
                EntityLiving livingEntity = (EntityLiving) entity;
                if (livingEntity instanceof EntityEnderman) {
                    this.addLitInstance(entity, 6);
                } else if (livingEntity instanceof EntityMagmaCube) {
                    this.addLitInstance(entity, 12);
                }
            } else if (entity instanceof EntityItem) {
                EntityItem entityItem = (EntityItem) entity;
                ItemStack stack = entityItem.getItem();
                int light = this.getItemLight(stack);
                if (light > 0) {
                    this.addLitInstance(entity, light);
                }
            }
        } else if (entity.isDead || !instance.validityCheck.test(entity)) {
            this.onEntityRemoved(entity);
        }
    }

    @Override
    public void onEntityRemoved(Entity entity) {
        LitInstance instance = ((ILitEntity) entity).setLitInstance(null);
        if (instance != null) {
            this.instances.remove(instance);
            instance.refreshRenderChunks();
        }
    }

    // Prevent flickering, also a fast track
    public int getEntityDynamicCombinedLight(BlockPos pos, int combinedLight) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            LitInstance instance = ((ILitEntity) player).getLitInstance();
            if (instance != null && player.getDistanceSq(pos) < 4D) {
                return instance.lighting * 16;
            }
        }
        return getDynamicCombinedLight(pos, combinedLight);
    }

    public int getDynamicCombinedLight(BlockPos pos, int combinedLight) {
        if (this.instances.isEmpty()) {
            return combinedLight;
        }
        Set<LitInstance> instances = this.instances;
            int dynamicLight = 0;
            for (LitInstance instance : instances) {
                double distanceSq = pos.distanceSq(instance.lastPosX, instance.lastPosY, instance.lastPosZ);
                if (distanceSq <= 60.0625D) {
                    double light = (1D - Math.sqrt(distanceSq) / 7.75D) * instance.lighting;
                    if (light > dynamicLight) {
                        dynamicLight = (int) light;
                    }
                }
            }
            if (dynamicLight > 0) {
                dynamicLight *= 16;
                if (dynamicLight > (combinedLight & 255)) {
                    combinedLight &= -256;
                    combinedLight |= dynamicLight;
                }
            }
        return combinedLight;
    }

    @Override
    public void renderTerrainUpdate(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator) {
        EntityPlayer self = Minecraft.getMinecraft().player;
        if (self != null) {
            long time = System.currentTimeMillis();
            if (time > this.renderTime + DynamicLightingConfig.updateFrequency) {
                this.renderTime = time;
                ILitEntity litSelf = (ILitEntity) self;
                LitInstance selfLitInstance = litSelf.getLitInstance();
                if (selfLitInstance == null) {
                    ItemStack stack = self.getHeldItemMainhand();
                    if (!stack.isEmpty()) {
                        int light = this.getItemLight(stack);
                        if (light > 0) {
                            this.addLitInstance(self, light, e -> this.getItemLight(e.getHeldItemMainhand()) == light);
                        }
                    } else {
                        stack = self.getHeldItemOffhand();
                        int light = this.getItemLight(stack);
                        if (light > 0) {
                            this.addLitInstance(self, light, e -> this.getItemLight(e.getHeldItemOffhand()) == light);
                        }
                    }
                } else if (!selfLitInstance.validityCheck.test(self)) {
                    this.onEntityRemoved(self);
                }
                for (LitInstance instance : this.instances) {
                    Entity entity = instance.entity;
                    double currentX = entity.posX;
                    double currentY = entity.posY + 0.5D + entity.getEyeHeight();
                    double currentZ = entity.posZ;
                    if (Math.abs(instance.lastPosX - currentX) > 0.1D || Math.abs(instance.lastPosY - currentY) > 0.1D || Math.abs(instance.lastPosZ - currentZ) > 0.1D) {
                        instance.refreshRenderChunks();
                        instance.lastPosX = currentX;
                        instance.lastPosY = currentY;
                        instance.lastPosZ = currentZ;
                        instance.refreshRenderChunks();
                    }
                }
            }
        }
    }

    private int getItemLight(ItemStack stack) {
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            int mapping = itemMapping.getInt(item);
            if (mapping > 0) {
                return mapping;
            } else if (item instanceof ItemBlock) {
                ItemBlock itemBlock = (ItemBlock) item;
                return itemBlock.getBlock().getLightValue(itemBlock.getBlock().getStateFromMeta(stack.getMetadata()));
            }
        }
        return 0;
    }

    public static class LitInstance {

        private final Entity entity;
        private final int lighting;
        private final Predicate<Entity> validityCheck;

        private double lastPosX, lastPosY, lastPosZ;

        private LitInstance(Entity entity, int lighting, Predicate<Entity> validityCheck) {
            this.entity = entity;
            this.lighting = MathHelper.clamp(lighting, 1, 15);
            this.validityCheck = validityCheck;
            this.lastPosX = entity.posX;
            this.lastPosY = entity.posY + 0.5D + entity.getEyeHeight();
            this.lastPosZ = entity.posZ;
            refreshRenderChunks();
        }

        private void refreshRenderChunks() {
            EnumFacing xFacing = (((int) this.lastPosX) & 15) < 8 ? EnumFacing.WEST : EnumFacing.EAST;
            EnumFacing yFacing = (((int) this.lastPosY) & 15) < 8 ? EnumFacing.DOWN : EnumFacing.UP;
            EnumFacing zFacing = (((int) this.lastPosZ) & 15) < 8 ? EnumFacing.NORTH : EnumFacing.SOUTH;
            IRenderGlobalExpansion renderGlobal = (IRenderGlobalExpansion) Minecraft.getMinecraft().renderGlobal;

            BlockPos pos = new BlockPos(this.lastPosX, this.lastPosY, this.lastPosZ);
            RenderChunk renderChunk = renderGlobal.getRenderChunk(pos);
            updateRenderChunk(renderChunk);

            BlockPos chunkPosX = getChunkPos(renderChunk, pos, xFacing);
            RenderChunk renderChunkX = renderGlobal.getRenderChunk(chunkPosX);
            updateRenderChunk(renderChunkX);

            BlockPos chunkPosY = getChunkPos(renderChunk, pos, yFacing);
            RenderChunk renderChunkY = renderGlobal.getRenderChunk(chunkPosY);
            updateRenderChunk(renderChunkY);

            BlockPos chunkPosZ = getChunkPos(renderChunk, pos, zFacing);
            RenderChunk renderChunkZ = renderGlobal.getRenderChunk(chunkPosZ);
            updateRenderChunk(renderChunkZ);

            BlockPos chunkPosXZ = getChunkPos(renderChunkX, chunkPosX, zFacing);
            RenderChunk renderChunkXZ = renderGlobal.getRenderChunk(chunkPosXZ);
            updateRenderChunk(renderChunkXZ);

            BlockPos chunkPosYX = getChunkPos(renderChunkY, chunkPosY, xFacing);
            RenderChunk renderChunkYX = renderGlobal.getRenderChunk(chunkPosYX);
            updateRenderChunk(renderChunkYX);

            BlockPos chunkPosYZ = getChunkPos(renderChunkY, chunkPosY, zFacing);
            RenderChunk renderChunkYZ = renderGlobal.getRenderChunk(chunkPosYZ);
            updateRenderChunk(renderChunkYZ);

            BlockPos chunkPosYXZ = getChunkPos(renderChunkYX, chunkPosYX, zFacing);
            RenderChunk renderChunkYXZ = renderGlobal.getRenderChunk(chunkPosYXZ);
            updateRenderChunk(renderChunkYXZ);

        }

        // getBlockPosOffset16 returns mutable pos
        private BlockPos getChunkPos(RenderChunk renderChunk, BlockPos pos, EnumFacing facing) {
            return renderChunk != null ? renderChunk.getBlockPosOffset16(facing) : pos.offset(facing, 16);
        }

        private boolean updateRenderChunk(RenderChunk renderChunk) {
            if (renderChunk != null) {
                CompiledChunk compiledChunk = renderChunk.getCompiledChunk();
                if (!compiledChunk.isEmpty()) {
                    renderChunk.setNeedsUpdate(false);
                    return true;
                }
            }
            return false;
        }

    }

}
