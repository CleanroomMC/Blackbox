package com.cleanroommc.blackbox.util;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class ModelHelper {

    public static IBakedModel bakeDefaultModel(IModel model, VertexFormat format) {
        return model.bake(model.getDefaultState(), format, ModelLoader.defaultTextureGetter());
    }

    public static IModel findModel(String namespace, String path) throws Exception {
        return findModel(new ResourceLocation(namespace, path));
    }

    public static IModel findModelOrLog(String namespace, String path, String log) {
        return findModelOrLog(new ResourceLocation(namespace, path), log);
    }

    public static IModel findModelUnsafe(String namespace, String path) {
        return findModelUnsafe(new ResourceLocation(namespace, path));
    }

    public static IModel findModel(ResourceLocation location) throws Exception {
        return ModelLoaderRegistry.getModel(location);
    }

    public static IModel findModelOrLog(ResourceLocation location, String log) {
        return ModelLoaderRegistry.getModelOrLogError(location, log);
    }

    public static IModel findModelUnsafe(ResourceLocation location) {
        return ModelLoaderRegistry.getModelOrMissing(location);
    }

    private ModelHelper() { }

}
