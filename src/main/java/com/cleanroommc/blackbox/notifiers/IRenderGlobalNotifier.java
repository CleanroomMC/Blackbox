package com.cleanroommc.blackbox.notifiers;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;

import javax.annotation.Nullable;

public interface IRenderGlobalNotifier {

    default void refresh(@Nullable WorldClient world) {

    }

    default void renderTerrainUpdate(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator) {

    }

}
