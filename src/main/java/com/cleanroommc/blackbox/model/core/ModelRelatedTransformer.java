package com.cleanroommc.blackbox.model.core;

import net.minecraft.launchwrapper.IClassTransformer;

public class ModelRelatedTransformer implements IClassTransformer {

	public static final ModelRelatedTransformer INSTANCE = new ModelRelatedTransformer();

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		return new byte[0];
	}

}
