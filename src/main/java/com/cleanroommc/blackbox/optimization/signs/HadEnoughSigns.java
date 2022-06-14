package com.cleanroommc.blackbox.optimization.signs;

import com.cleanroommc.blackbox.notifiers.IModelRelatedNotifier;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.client.model.ModelLoader;

import java.util.Map;

public enum HadEnoughSigns implements IModelRelatedNotifier {

    INSTANCE;

    public final ModelResourceLocation STANDING_SIGN_MRL = new ModelResourceLocation("blackbox:standing_sign");
    public final ModelResourceLocation WALL_SIGN_MRL = new ModelResourceLocation("blackbox:wall_sign");
    public final ResourceLocation SIGN_POST_TEXTURE_RL = new ResourceLocation("blocks/log_oak");
    public final ResourceLocation SIGN_ENTITY_TEXTURE_RL = new ResourceLocation("blackbox", "blocks/sign");

    @Override
    public void onModelRegister() {
        ModelLoader.setCustomStateMapper(Blocks.STANDING_SIGN, block -> {
            Map<IBlockState, ModelResourceLocation> mrls = new Object2ObjectOpenHashMap<>();
            for (IBlockState state : block.getBlockState().getValidStates()) {
                mrls.put(state, STANDING_SIGN_MRL);
            }
            return mrls;
        });
        ModelLoader.setCustomStateMapper(Blocks.WALL_SIGN, block -> {
            Map<IBlockState, ModelResourceLocation> mrls = new Object2ObjectOpenHashMap<>();
            for (IBlockState state : block.getBlockState().getValidStates()) {
                mrls.put(state, WALL_SIGN_MRL);
            }
            return mrls;
        });
    }

    @Override
    public void onPreStitchTexture(Pre event) {
        event.getMap().registerSprite(SIGN_ENTITY_TEXTURE_RL);
    }

    @Override
    public void onModelBake(ModelBakeEvent event) {
        event.getModelRegistry().putObject(STANDING_SIGN_MRL, new SignBakedModel(true, SIGN_ENTITY_TEXTURE_RL.toString(), SIGN_POST_TEXTURE_RL.toString(), SIGN_ENTITY_TEXTURE_RL.toString()));
        event.getModelRegistry().putObject(WALL_SIGN_MRL, new SignBakedModel(false, SIGN_ENTITY_TEXTURE_RL.toString(), SIGN_POST_TEXTURE_RL.toString(), SIGN_ENTITY_TEXTURE_RL.toString()));
    }

}
