package com.cleanroommc.blackbox.pipeline.notifiers;

import com.cleanroommc.blackbox.pipeline.lighting.DynamicLightingHandler;

import java.util.ArrayList;
import java.util.List;

public enum ClientNotifiers {

    INSTANCE;

    private final List<IEntityStatusNotifier> entityStatusNotifiers = new ArrayList<>();
    private final List<ISetupTerrainNotifier> setupTerrainNotifiers = new ArrayList<>();

    ClientNotifiers() {
        this.entityStatusNotifiers.add(DynamicLightingHandler.INSTANCE);
        this.setupTerrainNotifiers.add(DynamicLightingHandler.INSTANCE);
    }

    public List<IEntityStatusNotifier> getEntityStatusNotifiers() {
        return entityStatusNotifiers;
    }

    public List<ISetupTerrainNotifier> getSetupTerrainNotifiers() {
        return setupTerrainNotifiers;
    }

    public void addEntityStatusNotifier(IEntityStatusNotifier notifier) {
        this.entityStatusNotifiers.add(notifier);
    }

}
