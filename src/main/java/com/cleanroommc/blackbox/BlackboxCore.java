package com.cleanroommc.blackbox;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.Name("Blackbox Rendering Core")
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class BlackboxCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

	@Override
	public String[] getASMTransformerClass() {
		return new String[0];
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Nullable
	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) { }

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	@Override
	public List<String> getMixinConfigs() {
		return Lists.newArrayList(
				"mixins.blackbox_config.json",
				"mixins.blackbox_fixes.json",
				"mixins.blackbox_misc.json",
				"mixins.blackbox_pipeline.json",
				"mixins.blackbox_resource_injection.json",
				"mixins.blackbox_shaders.json",
				"mixins.blackbox_text.json",
				"mixins.blackbox_util.json"
				/*,"mixins.blackbox_threaded.json"*/);
	}
}
