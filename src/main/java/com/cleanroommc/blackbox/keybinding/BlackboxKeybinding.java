package com.cleanroommc.blackbox.keybinding;

import com.cleanroommc.blackbox.Blackbox;
import com.cleanroommc.blackbox.keybinding.handler.ZoomHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

import java.util.Locale;

public enum BlackboxKeybinding {

    ZOOM(Keyboard.KEY_V, KeyConflictContext.IN_GAME, ZoomHandler.INSTANCE);

    public static void init() {
        Blackbox.LOGGER.info("Registering Blackbox Keybinds.");
    }

    private final int keyCode;
    private final KeyBinding instance;

    BlackboxKeybinding(int keyCode, IKeyConflictContext ctx) {
        this.keyCode = keyCode;
        this.instance = new KeyBinding(Blackbox.ID + "." + this.name().toLowerCase(Locale.ENGLISH), ctx, keyCode, "blackbox.keys");
        ClientRegistry.registerKeyBinding(this.instance);
    }

    BlackboxKeybinding(int keyCode, IKeyConflictContext ctx, IKeybindHandler keybindHandler) {
        this(keyCode, ctx);
        MinecraftForge.EVENT_BUS.register(keybindHandler);
    }

    public int getKeyCode() {
        return keyCode;
    }

    public KeyBinding getInstance() {
        return instance;
    }

    public boolean isKeyDown() {
        return instance.isKeyDown();
    }

}
