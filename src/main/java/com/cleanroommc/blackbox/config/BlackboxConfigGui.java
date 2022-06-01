package com.cleanroommc.blackbox.config;

import com.cleanroommc.blackbox.Blackbox;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;

public class BlackboxConfigGui extends GuiConfig {

    public BlackboxConfigGui(GuiScreen parentScreen) {
        super(parentScreen, Blackbox.ID, Blackbox.NAME);
    }

}
