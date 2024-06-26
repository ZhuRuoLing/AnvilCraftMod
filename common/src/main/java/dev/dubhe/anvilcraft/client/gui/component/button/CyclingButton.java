package dev.dubhe.anvilcraft.client.gui.component.button;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.dubhe.anvilcraft.AnvilCraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CyclingButton extends Button {

    private final ResourceLocation texture;

    /**
     * 加减按钮/上下按钮
     */
    public CyclingButton(int x, int y, String base, String variant, OnPress onPress) {
        super(x,
                y,
                10,
                10,
                Component.literal(""),
                onPress,
                (var) -> Component.literal(variant)
        );
        texture = AnvilCraft.of("textures/gui/container/machine/%s_%s.png".formatted(base, variant));
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTexture(
                guiGraphics,
                texture,
                this.getX(),
                this.getY(),
                0,
                0,
                10,
                this.width,
                this.height,
                10,
                20
        );
    }

    @Override
    public void renderTexture(@NotNull GuiGraphics guiGraphics, @NotNull ResourceLocation texture,
                              int x, int y, int puOffset, int pvOffset, int textureDifference,
                              int width, int height, int textureWidth, int textureHeight) {
        int i = pvOffset;
        if (this.isHovered()) {
            i += textureDifference;
        }
        RenderSystem.enableDepthTest();
        guiGraphics.blit(texture, x, y, puOffset, i, width, height, textureWidth, textureHeight);
    }
}
