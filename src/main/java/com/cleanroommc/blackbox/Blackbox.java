package com.cleanroommc.blackbox;

import com.cleanroommc.blackbox.shaders.ShaderLoaders;
import com.cleanroommc.blackbox.shaders.ShaderRenderLayer;
import com.cleanroommc.blackbox.shaders.ShaderTypes;
import com.cleanroommc.blackbox.test.Tests;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Blackbox.ID, name = Blackbox.NAME, version = Blackbox.VERSION)
public class Blackbox {

	public static final String ID = "blackbox";
	public static final String NAME = "Blackbox Rendering API";
	public static final String VERSION = "1.0";

	public static final Logger LOGGER = LogManager.getLogger(NAME);

	@Mod.EventHandler
	public void construct(FMLConstructionEvent event) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			ShaderTypes.init();
			ShaderRenderLayer.init();
		}
		if (FMLLaunchHandler.isDeobfuscatedEnvironment()) {
			MinecraftForge.EVENT_BUS.register(Tests.class);
		}
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			ShaderLoaders.init();
		}
		if (FMLLaunchHandler.isDeobfuscatedEnvironment()) {
			Tests.onPostInit();
		}
	}

}
