package com.cleanroommc.blackbox.notifiers;

import net.minecraftforge.fml.client.event.ConfigChangedEvent;

public interface IConfigChangedNotifier {

    default void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {

    }

}
