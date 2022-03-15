package com.cleanroommc.blackbox.dummyobj;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;
import java.util.Map;

public class DummyBlockAccess implements IBlockAccess {

	private static final IBlockState AIR = Blocks.AIR.getDefaultState();

	private final Map<BlockPos, IBlockState> loadedStates;
	private final Map<BlockPos, TileEntity> loadedTiles;

	public DummyBlockAccess() {
		this.loadedStates = new Object2ObjectOpenHashMap<>();
		this.loadedTiles = new Object2ObjectOpenHashMap<>();
	}

	@Nullable
	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		return this.loadedTiles.get(pos);
	}

	@Override
	public int getCombinedLight(BlockPos pos, int lightValue) {
		return 0; // TODO: DummyChunk
	}

	@Override
	public IBlockState getBlockState(BlockPos pos) {
		return this.loadedStates.getOrDefault(pos, AIR);
	}

	@Override
	public boolean isAirBlock(BlockPos pos) {
		IBlockState state = getBlockState(pos);
		return state.getBlock().isAir(state, this, pos);
	}

	@Override
	public Biome getBiome(BlockPos pos) {
		return Biomes.DEFAULT;
	}

	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction) {
		return 0;
	}

	@Override
	public WorldType getWorldType() {
		return WorldType.DEFAULT;
	}

	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
		IBlockState state = getBlockState(pos);
		return state == AIR ? _default : state.isSideSolid(this, pos, side);
	}

}
