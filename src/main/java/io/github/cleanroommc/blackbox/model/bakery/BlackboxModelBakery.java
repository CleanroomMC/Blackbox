package io.github.cleanroommc.blackbox.model.bakery;

import com.google.common.collect.ImmutableMap;
import io.github.cleanroommc.blackbox.model.ModelTemplate;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.model.IModelState;

import java.util.Map;
import java.util.function.UnaryOperator;

public class BlackboxModelBakery {

	private static final FaceBakery FACE_BAKERY = new FaceBakery();

	private final ModelTemplate template;
	private final Map<String, String> sprites;

	private IModelState state;
	private VertexFormat format;
	private UnaryOperator<IModel> mutation;

	public BlackboxModelBakery(ModelTemplate template) {
		this.template = template;
		this.sprites = new Object2ObjectOpenHashMap<>();
		this.state = template.getModel().getDefaultState();
		this.format = DefaultVertexFormats.BLOCK;
	}

	public BlackboxModelBakery addSprite(String element, ResourceLocation textureLocation) {
		return addSprite(element, textureLocation.toString());
	}

	public BlackboxModelBakery addSprite(String element, String textureLocation) {
		this.sprites.put(element, textureLocation);
		return this;
	}

	public BlackboxModelBakery addParticleSprite(ResourceLocation textureLocation) {
		return addParticleSprite(textureLocation.toString());
	}

	public BlackboxModelBakery addParticleSprite(String textureLocation) {
		this.sprites.put("particle", textureLocation);
		return this;
	}

	public BlackboxModelBakery changeFormat(VertexFormat format) {
		this.format = format;
		return this;
	}

	public BlackboxModelBakery mutateModel(UnaryOperator<IModel> mutate) {
		this.mutation = mutate;
		return this;
	}

	public IBakedModel bake() {
		IModel mapped = template.getModel().retexture(ImmutableMap.copyOf(sprites));
		if (mutation != null) {
			mutation.apply(mapped);
		}
		return mapped.bake(mapped.getDefaultState(), format, ModelLoader.defaultTextureGetter());
	}

}
