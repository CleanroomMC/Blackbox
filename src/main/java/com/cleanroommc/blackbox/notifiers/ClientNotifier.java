package com.cleanroommc.blackbox.notifiers;

import com.cleanroommc.blackbox.optimization.signs.HadEnoughSigns;
import com.cleanroommc.blackbox.pipeline.lighting.DynamicLightingHandler;
import com.cleanroommc.blackbox.resource.BlackboxResourceLoader;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClientNotifier<T> {

    private static final List<ClientNotifier<?>> allNotifiers = new ArrayList<>();

    public static final ClientNotifier<IFMLLifecycleNotifier> FML_LIFECYCLE = new ClientNotifier<>(IFMLLifecycleNotifier.class, false);
    public static final ClientNotifier<IConfigChangedNotifier> CONFIG_CHANGED = new ClientNotifier<>(IConfigChangedNotifier.class, true);
    public static final ClientNotifier<IModelRelatedNotifier> MODEL_RELATED = new ClientNotifier<>(IModelRelatedNotifier.class, true);
    public static final ClientNotifier<IEntityStatusNotifier> ENTITY_STATUS = new ClientNotifier<>(IEntityStatusNotifier.class, false);
    public static final ClientNotifier<ILoadingNotifier> LOADING = new ClientNotifier<>(ILoadingNotifier.class, true);
    public static final ClientNotifier<IRenderGlobalNotifier> RENDER_GLOBAL = new ClientNotifier<>(IRenderGlobalNotifier.class, false);

    public static void init() {
        distribute(BlackboxResourceLoader.INSTANCE);
        distribute(DynamicLightingHandler.INSTANCE);
        distribute(HadEnoughSigns.INSTANCE);
    }

    private static <T> void distribute(Object listener) {
        for (ClientNotifier<?> notifier : allNotifiers) {
            ClientNotifier<T> castedNotifier = (ClientNotifier<T>) notifier;
            if (notifier.clazz.isInstance(listener)) {
                castedNotifier.addListener((T) listener);
            }
        }
    }

    private final Class<T> clazz;
    private final List<T> listeners;

    ClientNotifier(Class<T> clazz, boolean subscribeToEventBus) {
        this.clazz = clazz;
        if (subscribeToEventBus) {
            MinecraftForge.EVENT_BUS.register(clazz);
        }
        this.listeners = new ArrayList<>();
        allNotifiers.add(this);
    }

    public void addListener(T listener) {
        this.listeners.add(listener);
    }

    public void forEachListener(Consumer<T> consumer) {
        this.listeners.forEach(consumer);
    }

}
