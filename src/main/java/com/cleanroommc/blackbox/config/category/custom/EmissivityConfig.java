package com.cleanroommc.blackbox.config.category.custom;

import com.cleanroommc.blackbox.Blackbox;
import com.cleanroommc.blackbox.config.core.RefreshResources;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;

@Config(modid = Blackbox.ID, name = "/blackbox/custom/emissivity", category = "emissivity")
@LangKey("blackbox.custom.emissivity")
public class EmissivityConfig {

    @RefreshResources(VanillaResourceType.MODELS)
    @LangKey("blackbox.custom.emissivity.enabled")
    public static boolean enabled = true;

}
