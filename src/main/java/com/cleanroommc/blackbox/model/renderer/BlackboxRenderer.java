package com.cleanroommc.blackbox.model.renderer;

import com.cleanroommc.blackbox.model.renderer.mesh.material.RenderMaterial;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class BlackboxRenderer {

    public static final BlackboxRenderer INSTANCE = new BlackboxRenderer();

    private BlackboxRenderer() { }

    private final Map<ResourceLocation, RenderMaterial> renderMaterials = new Object2ObjectOpenHashMap<>();

    public boolean registerRenderMaterial(ResourceLocation location, RenderMaterial renderMaterial) {
        if (renderMaterials.containsKey(location)) {
            return false;
        }
        renderMaterials.put(location, renderMaterial);
        return true;
    }

}
