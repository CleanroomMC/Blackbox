package com.cleanroommc.blackbox.model;

import com.cleanroommc.blackbox.Blackbox;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

/**
 * IModel Storage
 */
public class ModelTemplate {

	public static final ModelTemplate BLOCK = new ModelTemplate("block/cube_all");
	public static final ModelTemplate NORMAL_ITEM = new ModelTemplate("item/generated");
	public static final ModelTemplate HANDHELD_ITEM = new ModelTemplate("item/handheld");

	public static final ModelTemplate LAYERED_BLOCK_TINTED = new ModelTemplate(Blackbox.ID, "block/layered_block_tinted");
	public static final ModelTemplate LAYERED_BLOCK_UNTINTED = new ModelTemplate(Blackbox.ID,"block/layered_block_untinted");

	private final IModel model;

	public ModelTemplate(String locationDomain, String locationPath) {
		this.model = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation(locationDomain, locationPath));
	}

	public ModelTemplate(String location) {
		this.model = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation(location));
	}

	public IModel getModel() {
		return model;
	}

}
