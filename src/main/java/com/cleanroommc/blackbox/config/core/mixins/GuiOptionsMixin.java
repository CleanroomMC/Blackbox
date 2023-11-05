package com.cleanroommc.blackbox.config.core.mixins;

import com.cleanroommc.blackbox.config.BlackboxConfigGui;
import com.cleanroommc.blackbox.config.BlackboxGuiButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiOptions.class)
public class GuiOptionsMixin extends GuiScreen {

    @Unique private static int blackboxConfigButtonId = -1;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void onInitGui(CallbackInfo ci) {
        if (blackboxConfigButtonId == -1) {
            blackboxConfigButtonId = this.buttonList.stream().mapToInt(button -> button.id).max().getAsInt() + 1;
        }
        this.buttonList.add(new BlackboxGuiButton(blackboxConfigButtonId, this.width / 2 - 77, this.height / 6 + 16));
    }

    @Inject(method = "actionPerformed", at = @At("TAIL"))
    private void onActionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.enabled && button.id == blackboxConfigButtonId) {
            this.mc.displayGuiScreen(new BlackboxConfigGui(this));
        }
    }

}
