package com.cleanroommc.blackbox.resource;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.client.resource.IResourceType;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

public interface IResourceFormat {

    default boolean considerAllNamespaces() {
        return true;
    }

    List<String> getFileNames();

    boolean accepts(Predicate<IResourceType> resourceTypePredicate);

    void read(IResourceManager manager, IResource resource, boolean isReloading) throws IOException;

    boolean isApplicable();

}
