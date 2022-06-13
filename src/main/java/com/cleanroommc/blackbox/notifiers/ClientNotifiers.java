package com.cleanroommc.blackbox.notifiers;

import com.cleanroommc.blackbox.Blackbox;
import com.cleanroommc.blackbox.optimization.signs.HadEnoughSigns;
import com.cleanroommc.blackbox.pipeline.lighting.DynamicLightingHandler;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public enum ClientNotifiers {

    INSTANCE;

    private final List<IConfigChangedNotifier> configChangedNotifiers = new ArrayList<>();
    private final List<IEntityStatusNotifier> entityStatusNotifiers = new ArrayList<>();
    private final List<IModelBakeEventNotifier> modelBakeEventNotifiers = new ArrayList<>();
    private final List<ISetupTerrainNotifier> setupTerrainNotifiers = new ArrayList<>();

    ClientNotifiers() {
        this.configChangedNotifiers.add(HadEnoughSigns.INSTANCE);
        this.modelBakeEventNotifiers.add(HadEnoughSigns.INSTANCE);
        this.entityStatusNotifiers.add(DynamicLightingHandler.INSTANCE);
        this.setupTerrainNotifiers.add(DynamicLightingHandler.INSTANCE);
    }

    public List<IConfigChangedNotifier> getConfigChangedNotifiers() {
        return configChangedNotifiers;
    }

    public List<IEntityStatusNotifier> getEntityStatusNotifiers() {
        return entityStatusNotifiers;
    }

    public List<IModelBakeEventNotifier> getModelBakeEventNotifiers() {
        return modelBakeEventNotifiers;
    }

    public List<ISetupTerrainNotifier> getSetupTerrainNotifiers() {
        return setupTerrainNotifiers;
    }

    public void registerEventListener() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (Blackbox.ID.equals(event.getModID())) {
            ConfigManager.sync(Blackbox.ID, Type.INSTANCE);
            this.configChangedNotifiers.forEach(notifier -> notifier.onConfigChanged(event));
        }
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        this.modelBakeEventNotifiers.forEach(notifier -> notifier.onModelBake(event));
    }

}
