package com.cleanroommc.blackbox.notifiers;

import net.minecraft.entity.Entity;

public interface IEntityStatusNotifier {

    default void onEntityAdded(Entity entity) {

    }

    default void onEntityRemoved(Entity entity) {

    }

}
