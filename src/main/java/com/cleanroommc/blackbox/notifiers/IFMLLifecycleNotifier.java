package com.cleanroommc.blackbox.notifiers;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IFMLLifecycleNotifier {

    default void onPreInitializing(FMLPreInitializationEvent event) {

    }

    default void onInitializing(FMLInitializationEvent event) {

    }

    default void onPostInitializing(FMLPostInitializationEvent event) {

    }

}
