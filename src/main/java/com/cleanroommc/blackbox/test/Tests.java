package com.cleanroommc.blackbox.test;

import com.cleanroommc.blackbox.Blackbox;
import com.cleanroommc.blackbox.model.ModelTemplate;
import com.cleanroommc.blackbox.model.bakery.BlackboxModelBakery;
import com.cleanroommc.blackbox.shaders.ShaderRenderLayer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Make sure everything in Blackbox is working.
 */
public class Tests {

	static TestBlock layered;
	static TestBlock notLayered;

	@SubscribeEvent
	public static void onBlocksRegister(RegistryEvent.Register<Block> event) {
		event.getRegistry().register(layered = new TestBlock("layered"));
		event.getRegistry().register(notLayered = new TestBlock("not_layered"));
	}

	@SubscribeEvent
	public static void onItemsRegister(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemBlock(layered).setRegistryName(layered.getRegistryName()));
		event.getRegistry().register(new ItemBlock(notLayered).setRegistryName(notLayered.getRegistryName()));
	}

	@SubscribeEvent
	public static void onTextureStitch(TextureStitchEvent.Pre event) {
		event.getMap().registerSprite(new ResourceLocation(Blackbox.ID, "blocks/diamond_ore_overlay"));
	}

	public static void onPostInit() {
		IBakedModel first = new BlackboxModelBakery(ModelTemplate.LAYERED_BLOCK_TINTED)
				.addSprite("underlay", "blocks/stone")
				.addSprite("overlay", "blackbox:blocks/diamond_ore_overlay")
				.addParticleSprite("blocks/stone")
				.bake();
		IBakedModel second = new BlackboxModelBakery(ModelTemplate.BLOCK)
				.addSprite("all", "blocks/stone")
				.addParticleSprite("blocks/stone")
				.bake();
		BlockModelShapes shapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
		try {
			Field bakedModelStoreField = BlockModelShapes.class.getDeclaredField("bakedModelStore");
			bakedModelStoreField.setAccessible(true);
			Map<IBlockState, IBakedModel> bakedModelStore = (Map<IBlockState, IBakedModel>) bakedModelStoreField.get(shapes);
			bakedModelStore.put(layered.getDefaultState(), new DiamondOre(first));
			bakedModelStore.put(notLayered.getDefaultState(), new FlatBloom(second));
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	public static class TestBlock extends Block {

		public TestBlock(String name) {
			super(Material.ROCK);
			setRegistryName(Blackbox.ID, name);
		}

		@Override
		public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
			return layer == BlockRenderLayer.CUTOUT_MIPPED || layer == ShaderRenderLayer.Bloom.LAYER;
		}

	}

	public static class DiamondOre implements IBakedModel {

		final IBakedModel model;

		public DiamondOre(IBakedModel model) {
			this.model = model;
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
			List<BakedQuad> quads = new ArrayList<>();
			for (BakedQuad quad : model.getQuads(state, side, rand)) {
				if (quad.getTintIndex() == 1) {
					VertexFormat newVertexFormat = new VertexFormat(quad.getFormat()).addElement(DefaultVertexFormats.TEX_2S);
					UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(newVertexFormat);
					VertexLighterFlat trans = new VertexLighterFlat(Minecraft.getMinecraft().getBlockColors()) {
						@Override
						protected void updateLightmap(float[] normal, float[] lightmap, float x, float y, float z) {
							float lv = (float) (((255 >> 0x04) & 0xF) * 0x20) / 0xFFFF; // Light 255 (max)
							lightmap[0] = lv;
							lightmap[1] = lv;
						}
						@Override
						public void setQuadTint(int tint) {
							// NO OP
						}
					};
					trans.setParent(builder);
					quad.pipe(trans);
					builder.setQuadTint(1);
					builder.setQuadOrientation(quad.getFace());
					builder.setTexture(quad.getSprite());
					builder.setApplyDiffuseLighting(false);
					BakedQuad newQuad = builder.build();
					quads.add(newQuad);
				} else {
					quads.add(quad);
				}
			}
			return quads;
		}

		@Override
		public boolean isAmbientOcclusion() {
			return true;
		}

		@Override
		public boolean isGui3d() {
			return true;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return model.getParticleTexture();
		}

		@Override
		public ItemOverrideList getOverrides() {
			return model.getOverrides();
		}

	}

	static class FlatBloom implements IBakedModel {

		final IBakedModel model;

		public FlatBloom(IBakedModel model) {
			this.model = model;
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
			List<BakedQuad> quads = new ArrayList<>();
			for (BakedQuad quad : model.getQuads(state, side, rand)) {
				VertexFormat newVertexFormat = new VertexFormat(quad.getFormat()).addElement(DefaultVertexFormats.TEX_2S);
				UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(newVertexFormat);
				VertexLighterFlat trans = new VertexLighterFlat(Minecraft.getMinecraft().getBlockColors()) {
					@Override
					protected void updateLightmap(float[] normal, float[] lightmap, float x, float y, float z) {
						float lv = (float) (((255 >> 0x04) & 0xF) * 0x20) / 0xFFFF; // Light 255 (max)
						lightmap[0] = lv;
						lightmap[1] = lv;
					}
					@Override
					public void setQuadTint(int tint) {
						// NO OP
					}
				};
				trans.setParent(builder);
				quad.pipe(trans);
				builder.setQuadOrientation(quad.getFace());
				builder.setTexture(quad.getSprite());
				builder.setApplyDiffuseLighting(false);
				BakedQuad newQuad = builder.build();
				quads.add(newQuad);
			}
			return quads;
		}

		@Override
		public boolean isAmbientOcclusion() {
			return false;
		}

		@Override
		public boolean isGui3d() {
			return true;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return model.getParticleTexture();
		}

		@Override
		public ItemOverrideList getOverrides() {
			return model.getOverrides();
		}

	}

}
