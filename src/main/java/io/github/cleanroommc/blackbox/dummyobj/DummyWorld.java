package io.github.cleanroommc.blackbox.dummyobj;

import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;

public class DummyWorld extends World {

	public DummyWorld(WorldProvider providerIn, Profiler profilerIn, boolean client) {
		super(null, null, providerIn, profilerIn, client);
		this.perWorldStorage = null;
	}

	@Override
	public void initialize(WorldSettings settings) {
		// NO-OP
	}

	@Override
	public void checkSessionLock() {
		// NO-OP
	}

	@Override
	protected void initCapabilities() {
		// NO-OP
	}

	@Override
	public ISaveHandler getSaveHandler() {
		throw new UnsupportedOperationException("No ISaveHandler implementations in a dummy world.");
	}

	@Override
	public MapStorage getPerWorldStorage() {
		throw new UnsupportedOperationException("No MapStorage implementations in a dummy world.");
	}

	@Override
	protected IChunkProvider createChunkProvider() {
		return null;
	}

	@Override
	protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
		return false;
	}

}
