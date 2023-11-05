package com.cleanroommc.blackbox.pipeline.core.mixins;

import com.cleanroommc.blackbox.config.category.lighting.DynamicLightingConfig;
import com.cleanroommc.blackbox.pipeline.core.ILitEntity;
import com.cleanroommc.blackbox.pipeline.lighting.DynamicLightingHandler;
import com.cleanroommc.blackbox.pipeline.lighting.DynamicLightingHandler.LitInstance;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Entity.class)
public class EntityMixin implements ILitEntity {

    @Shadow public World world;

    @Unique private LitInstance litInstance = null;

    @Inject(method = "onUpdate", at = @At("RETURN"))
    private void onUpdate(CallbackInfo ci) {
        if (this.world.isRemote && DynamicLightingConfig.enabled) {
            DynamicLightingHandler.INSTANCE.updateTick((Entity) (Object) this);
        }
    }

    @Override
    public LitInstance setLitInstance(LitInstance litInstance) {
        LitInstance previous = this.litInstance;
        this.litInstance = litInstance;
        return previous;
    }

    @Nullable
    @Override
    public LitInstance getLitInstance() {
        return litInstance;
    }

}
