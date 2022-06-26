package com.cleanroommc.blackbox.resource;

import com.cleanroommc.blackbox.Blackbox;
import com.cleanroommc.blackbox.notifiers.IFMLLifecycleNotifier;
import com.cleanroommc.blackbox.resource.format.EmissiveResourceFormat;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.function.Predicate;

public enum BlackboxResourceLoader implements IFMLLifecycleNotifier {

    INSTANCE;

    private final Map<Class<? extends IResourceFormat>, IResourceFormat> formats = new Object2ObjectOpenHashMap<>();

    private boolean reloading = false;

    BlackboxResourceLoader() {
        registerResourceFormat(EmissiveResourceFormat.INSTANCE);
    }

    @Override
    public void onPreInitializing(FMLPreInitializationEvent event) {
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener((ISelectiveResourceReloadListener) INSTANCE::loadResources);
        reloading = true;
    }

    public void registerResourceFormat(IResourceFormat format) {
        this.formats.put(format.getClass(), format);
    }

    public void loadResources(IResourceManager manager, Predicate<IResourceType> resouceTypePredicate) {
        for (IResourceFormat format : this.formats.values()) {
            if (format.accepts(resouceTypePredicate)) {
                for (String fileName : format.getFileNames()) {
                    try {
                        if (format.considerAllNamespaces()) {
                            for (IResource resource : manager.getAllResources(new ResourceLocation(fileName))) {
                                format.read(manager, resource, this.reloading);
                                IOUtils.closeQuietly(resource);
                            }
                        } else {
                            try (IResource resource = manager.getResource(new ResourceLocation(fileName))) {
                                format.read(manager, resource, this.reloading);
                            }
                        }
                    } catch (FileNotFoundException e) {
                        Blackbox.LOGGER.debug("Blackbox[{}] cannot find {}. Skipping.", format.getClass().getSimpleName() + ".class", fileName);
                    } catch (Exception e) {
                        Blackbox.LOGGER.fatal("Blackbox[{}] could not load {}!", format.getClass().getSimpleName() + ".class", fileName, e);
                    }
                }
            }
        }
    }

}
