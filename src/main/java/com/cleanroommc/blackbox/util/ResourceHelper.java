package com.cleanroommc.blackbox.util;

import com.cleanroommc.blackbox.util.core.mixins.ResourcePackRepositoryAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class ResourceHelper {

    public static boolean doesResourceExist(ResourceLocation location) {
        ResourcePackRepository repo = Minecraft.getMinecraft().getResourcePackRepository();
        if (repo.getServerResourcePack() != null && repo.getServerResourcePack().resourceExists(location)) {
            return true;
        }
        List<Entry> packs = ((ResourcePackRepositoryAccessor) repo).getRepoEntries();
        for (int i = packs.size() - 1; i >= 0; i--) {
            if (packs.get(i).getResourcePack().resourceExists(location)) {
                return true;
            }
        }
        return repo.rprDefaultResourcePack != null && repo.rprDefaultResourcePack.resourceExists(location);
    }

    private ResourceHelper() { }

}
