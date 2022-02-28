package com.cleanroommc.blackbox.model.statemapper;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;

import java.util.Map;

public class SimpleStateMapper implements IStateMapper {

	public static SimpleStateMapper of(ModelResourceLocation mrl) {
		return new SimpleStateMapper(mrl);
	}

	private final ModelResourceLocation mrl;

	protected SimpleStateMapper(ModelResourceLocation mrl) {
		this.mrl = mrl;
	}

	@Override
	public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block) {
		return block.getBlockState().getValidStates().stream().collect(Object2ObjectArrayMap::new, (m, s) -> m.put(s, mrl), Object2ObjectArrayMap::putAll);
	}

}
