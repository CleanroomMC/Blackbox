package com.cleanroommc.blackbox.pipeline.core.mixins;

import com.cleanroommc.blackbox.config.category.lighting.DynamicLightingConfig;
import com.cleanroommc.blackbox.pipeline.lighting.DynamicLightingHandler;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldClient.class)
public abstract class WorldClientMixin extends World {

    protected WorldClientMixin(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
        super(saveHandlerIn, info, providerIn, profilerIn, client);
        throw new AssertionError();
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return DynamicLightingConfig.enabled ?
                DynamicLightingHandler.INSTANCE.getEntityDynamicCombinedLight(pos, super.getCombinedLight(pos, lightValue)) :
                super.getCombinedLight(pos, lightValue);
    }

}
