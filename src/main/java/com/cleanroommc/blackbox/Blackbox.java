package com.cleanroommc.blackbox;

import com.cleanroommc.blackbox.keybinding.BlackboxKeybinding;
import com.cleanroommc.blackbox.notifiers.ClientNotifier;
import com.cleanroommc.blackbox.shaders.ShaderTypes;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Blackbox.ID, name = Blackbox.NAME, version = Blackbox.VERSION, guiFactory = "com.cleanroommc.blackbox.config.BlackboxConfigGuiFactory")
public class Blackbox {

	public static final String ID = "blackbox";
	public static final String NAME = "Blackbox Rendering API";
	public static final String VERSION = "1.0";

	public static final Logger LOGGER = LogManager.getLogger(NAME);

	public static final FaceBakery FACE_BAKERY = new FaceBakery(); // TODO: relocate

	@Mod.EventHandler
	public void construct(FMLConstructionEvent event) {
		BlackboxTest.construct();
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			ClientNotifier.init();
			ShaderTypes.init();
		}
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		BlackboxTest.preInit();
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			BlackboxKeybinding.init();
			ClientNotifier.FML_LIFECYCLE.forEachListener(notifier -> notifier.onPreInitializing(event));
		}
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			ClientNotifier.FML_LIFECYCLE.forEachListener(notifier -> notifier.onInitializing(event));
		}
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		BlackboxTest.postInit();
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			ClientNotifier.FML_LIFECYCLE.forEachListener(notifier -> notifier.onPostInitializing(event));
		}
	}

}
