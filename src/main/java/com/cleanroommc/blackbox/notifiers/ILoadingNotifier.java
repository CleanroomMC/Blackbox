package com.cleanroommc.blackbox.notifiers;

import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public interface ILoadingNotifier {

    @SubscribeEvent
    static void onChunkLoadListener(ChunkEvent.Load event) {
        ClientNotifier.LOADING.forEachListener(notifier -> notifier.onChunkLoad(event));
    }

    @SubscribeEvent
    static void onChunkUnloadListener(ChunkEvent.Unload event) {
        ClientNotifier.LOADING.forEachListener(notifier -> notifier.onChunkUnload(event));
    }

    @SubscribeEvent
    static void onWorldLoadListener(WorldEvent.Load event) {
        ClientNotifier.LOADING.forEachListener(notifier -> notifier.onWorldLoad(event));
    }

    @SubscribeEvent
    static void onWorldUnloadListener(WorldEvent.Unload event) {
        ClientNotifier.LOADING.forEachListener(notifier -> notifier.onWorldUnload(event));
    }

    default void onChunkLoad(ChunkEvent.Load event) {

    }

    default void onChunkUnload(ChunkEvent.Unload event) {

    }

    default void onWorldLoad(WorldEvent.Load event) {

    }

    default void onWorldUnload(WorldEvent.Unload event) {

    }

}
