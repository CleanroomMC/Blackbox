package com.cleanroommc.blackbox.notifiers;

import com.cleanroommc.blackbox.Blackbox;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public interface IConfigChangedNotifier {

    @SubscribeEvent
    static void onConfigChangedListener(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (Blackbox.ID.equals(event.getModID())) {
            ConfigManager.sync(Blackbox.ID, Type.INSTANCE);
            ClientNotifier.CONFIG_CHANGED.forEachListener(notifier -> notifier.onConfigChanged(event));
        }
    }

    default void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {

    }

}
