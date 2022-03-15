package com.cleanroommc.blackbox.model.renderer.mesh.context;

import com.cleanroommc.blackbox.model.renderer.mesh.Mesh;
import com.cleanroommc.blackbox.model.renderer.mesh.quad.QuadEmitter;
import com.cleanroommc.blackbox.model.renderer.mesh.quad.QuadTransform;
import net.minecraft.client.renderer.block.model.IBakedModel;

import java.util.function.Consumer;

public interface RenderContext {

    Consumer<Mesh> getMeshConsumer();

    Consumer<IBakedModel> getFallbackConsumer();

    QuadEmitter getEmitter();

    void pushTransform(QuadTransform transform);

    void popTransform();

}
