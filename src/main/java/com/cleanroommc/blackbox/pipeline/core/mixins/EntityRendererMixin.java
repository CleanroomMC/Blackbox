package com.cleanroommc.blackbox.pipeline.core.mixins;

import com.cleanroommc.blackbox.pipeline.lighting.DynamicDarknessHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Shadow @Final private Minecraft mc;
    @Shadow @Final private int[] lightmapColors;

    @Shadow private boolean lightmapUpdateNeeded;
    @Shadow private float bossColorModifier;
    @Shadow private float bossColorModifierPrev;

    @Shadow private float torchFlickerX;

    @Shadow @Final private DynamicTexture lightmapTexture;

    /**
     * @author Rongmario
     * @reason Rewrite updateLightmap routine
     */
    @Overwrite
    private void updateLightmap(float partialTicks) {
        if (this.lightmapUpdateNeeded) {
            World world = this.mc.world;
            if (world != null) {
                this.mc.profiler.startSection("lightTex");
                DynamicDarknessHandler.INSTANCE.updateLightmap(
                        (EntityRendererAccessor) this, mc, partialTicks, this.lightmapColors, this.bossColorModifier, this.bossColorModifierPrev, this.torchFlickerX);
                this.lightmapTexture.updateDynamicTexture();
                this.lightmapUpdateNeeded = false;
                this.mc.profiler.endSection();
            }
        }
    }

}
