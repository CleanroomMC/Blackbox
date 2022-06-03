package com.cleanroommc.blackbox.config;

import com.cleanroommc.blackbox.Blackbox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Set;

public class BlackboxConfigGuiFactory implements IModGuiFactory {

    public static BlackboxConfigGuiFactory INSTANCE;

    public BlackboxConfigGuiFactory() {
        INSTANCE = this;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void initialize(Minecraft minecraftInstance) {

    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new BlackboxConfigGui(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (Blackbox.ID.equals(event.getModID())) {
            ConfigManager.sync(Blackbox.ID, Type.INSTANCE);
        }
    }

}
