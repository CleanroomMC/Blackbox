package com.cleanroommc.blackbox.pipeline.rendering.meshing;

import com.cleanroommc.blackbox.notifiers.IRenderGlobalNotifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;

import javax.annotation.Nullable;
import java.util.concurrent.LinkedBlockingQueue;

public enum RenderThreadWorkDispatcher implements IRenderGlobalNotifier {

    INSTANCE;

    private final LinkedBlockingQueue<Runnable> queuedWorks;

    RenderThreadWorkDispatcher() {
        this.queuedWorks = new LinkedBlockingQueue<>();
    }

    public void queue(Runnable work) {
        if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) { // TODO: change this check to check for the presence of an OpenGL context instead
            work.run();
        } else {
            this.queuedWorks.add(work);
        }
    }

    @Override
    public void refresh(@Nullable WorldClient world) {
        this.queuedWorks.clear();
    }

    @Override
    public void renderTerrainUpdate(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator) {
        this.queuedWorks.forEach(Runnable::run);
    }

}
