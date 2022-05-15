package com.cleanroommc.blackbox.pipeline.core.mixins;

import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GLSync;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    private final ObjectArrayFIFOQueue<GLSync> fences = new ObjectArrayFIFOQueue<>();

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V", ordinal = 0))
    private void beforeRender(CallbackInfo ci) {
        while (this.fences.size() > 5) {
            GLSync fence = this.fences.dequeue();
            GL32.glClientWaitSync(fence, GL32.GL_SYNC_FLUSH_COMMANDS_BIT, Long.MAX_VALUE);
            GL32.glDeleteSync(fence);
        }
    }

    @Inject(method = "runGameLoop", at = @At("RETURN"))
    private void afterRender(CallbackInfo cii) {
        GLSync fence = GL32.glFenceSync(GL32.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
        if (fence.getPointer() == 0) {
            throw new RuntimeException("Failed to create fence sync object");
        }
        this.fences.enqueue(fence);
    }

}
