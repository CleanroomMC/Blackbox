package com.cleanroommc.blackbox.util.core.mixins;

import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ResourcePackRepository.class)
public interface ResourcePackRepositoryAccessor {

    @Accessor("repositoryEntries")
    List<Entry> getRepoEntries();

}
