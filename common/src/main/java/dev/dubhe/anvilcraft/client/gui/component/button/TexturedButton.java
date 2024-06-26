package dev.dubhe.anvilcraft.client.gui.component.button;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TexturedButton extends Button {

    private final ResourceLocation texture;

    public TexturedButton(int x, int y, int width, int height, ResourceLocation texture, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, (n) -> Component.empty());
        this.texture = texture;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int v = isHovered() ? height : 0;
        guiGraphics.blit(
                texture,
                getX(),
                getY(),
                0,
                v,
                this.width,
                this.height,
                width,
                height * 2
        );
    }
}
