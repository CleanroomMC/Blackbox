package com.cleanroommc.blackbox.resource.format;

import com.cleanroommc.blackbox.config.category.custom.EmissivityConfig;
import com.cleanroommc.blackbox.resource.IResourceFormat;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.VanillaResourceType;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;

public enum EmissiveResourceFormat implements IResourceFormat {

    INSTANCE;

    private String suffix = "";

    @Override
    public List<String> getFileNames() {
        return Collections.singletonList("optifine/emissive.properties");
    }

    @Override
    public boolean accepts(Predicate<IResourceType> resourceTypePredicate) {
        return resourceTypePredicate.test(VanillaResourceType.TEXTURES);
    }

    @Override
    public void read(IResourceManager manager, IResource file, boolean isReloading) throws IOException {
        Properties properties = new Properties();
        properties.load(file.getInputStream());
        this.suffix = properties.getProperty("suffix.emissive", "_e");
    }

    @Override
    public boolean isApplicable() {
        return !this.suffix.isEmpty() && EmissivityConfig.enabled;
    }

    public String getSuffix() {
        return suffix;
    }

}
