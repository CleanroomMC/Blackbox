package com.cleanroommc.blackbox.pipeline.core.mixins;

import com.cleanroommc.blackbox.pipeline.core.IRenderGlobalExpansion;
import com.cleanroommc.blackbox.notifiers.ClientNotifier;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public class RenderGlobalMixin implements IRenderGlobalExpansion {

    @Shadow private ViewFrustum viewFrustum;

    @Override
    public RenderChunk getRenderChunk(BlockPos pos) {
        return ((ViewFrustumAccessor) this.viewFrustum).invokeGetRenderChunk(pos);
    }

    @Inject(method = "setupTerrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", ordinal = 3))
    private void beforeUpdateSection(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator, CallbackInfo ci) {
        ClientNotifier.SETUP_TERRAIN.forEachListener(notifier -> notifier.renderTerrainUpdate(viewEntity, partialTicks, camera, frameCount, playerSpectator));
    }

    /**
     * @author Rongmario
     */
    @Overwrite
    public void onEntityAdded(Entity entity) {
        ClientNotifier.ENTITY_STATUS.forEachListener(notifier -> notifier.onEntityAdded(entity));
    }

    /**
     * @author Rongmario
     */
    @Overwrite
    public void onEntityRemoved(Entity entity) {
        ClientNotifier.ENTITY_STATUS.forEachListener(notifier -> notifier.onEntityRemoved(entity));
    }

}
