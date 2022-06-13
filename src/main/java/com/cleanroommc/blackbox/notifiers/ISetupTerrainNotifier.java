package com.cleanroommc.blackbox.notifiers;

import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;

public interface ISetupTerrainNotifier {

    default void renderTerrainUpdate(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator) {

    }

}
