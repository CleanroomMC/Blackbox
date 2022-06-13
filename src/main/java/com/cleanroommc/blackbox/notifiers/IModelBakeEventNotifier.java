package com.cleanroommc.blackbox.notifiers;

import net.minecraftforge.client.event.ModelBakeEvent;

public interface IModelBakeEventNotifier {

    default void onModelBake(ModelBakeEvent event) {

    }

}
