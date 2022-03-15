package com.cleanroommc.blackbox;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

public final class BlackboxAPI {

    private static final BlackboxAPI INSTANCE = new BlackboxAPI();

    public static BlackboxAPI getInstance() {
        return INSTANCE;
    }

    private BlackboxAPI() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private final List<ResourceLocation> queuedSprites = new ArrayList<>();

    public void registerSprite(ResourceLocation spriteLocation) {
        this.queuedSprites.add(spriteLocation);
    }

    public void beforeTextureStitch(TextureStitchEvent.Pre event) {
        this.queuedSprites.forEach(event.getMap()::registerSprite);
    }

}
