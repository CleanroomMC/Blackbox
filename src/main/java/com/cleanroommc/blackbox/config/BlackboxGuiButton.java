package com.cleanroommc.blackbox.config;

import com.cleanroommc.blackbox.util.I18nHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class BlackboxGuiButton extends GuiButton {

    public BlackboxGuiButton(int buttonId, int x, int y) {
        super(buttonId, x, y, 150, 20, I18nHelper.getTranslatedString("options", "blackbox"));
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            if (this.hovered) {
                GlStateManager.color(0.75F, 0.0F, 0.75F, 1.0F);
            } else {
                GlStateManager.color(0.0F, 0.0F, 0.0F, 1.0F);
            }
            this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mouseDragged(mc, mouseX, mouseY);
            int colour = 14737632;
            if (packedFGColour != 0) {
                colour = packedFGColour;
            } else if (!this.enabled) {
                colour = 10526880;
            } else if (this.hovered) {
                colour = 16777120;
            }
            this.drawCenteredString(mc.fontRenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, colour);
        }
    }

}
