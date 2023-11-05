package com.cleanroommc.blackbox.notifiers;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public interface IModelRelatedNotifier {

    @SubscribeEvent
    static void onModelRegisterListener(ModelRegistryEvent event) {
        ClientNotifier.MODEL_RELATED.forEachListener(IModelRelatedNotifier::onModelRegister);
    }

    @SubscribeEvent
    static void onStitchTextureListener(TextureStitchEvent.Pre event) {
        ClientNotifier.MODEL_RELATED.forEachListener(notifier -> notifier.onPreStitchTexture(event));
    }

    @SubscribeEvent
    static void onModelBakeListener(ModelBakeEvent event) {
        ClientNotifier.MODEL_RELATED.forEachListener(notifier -> notifier.onModelBake(event));
    }

    default void onModelRegister() {

    }

    default void onPreStitchTexture(TextureStitchEvent.Pre event) {

    }

    default void onModelBake(ModelBakeEvent event) {

    }

}
