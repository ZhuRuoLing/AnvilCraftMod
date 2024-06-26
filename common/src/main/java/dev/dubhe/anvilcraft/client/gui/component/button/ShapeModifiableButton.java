package dev.dubhe.anvilcraft.client.gui.component.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ShapeModifiableButton<T> extends Button {

    private final Map<T, ShapeDef> shapeDefs = new HashMap<>();
    private ShapeDef currentShape = null;
    private final int order;

    public ShapeModifiableButton(int order) {
        super(
                0,
                0,
                0,
                0,
                Component.literal(""),
                (b) -> {
                },
                (i) -> Component.literal("")
        );
        this.order = order;
    }

    public void createShapeDef(T t, ShapeDef def) {
        shapeDefs.put(t, def);
    }

    /**
     * 更新按钮
     */
    public void setCurrentShape(T t) {
        ShapeDef shape = shapeDefs.get(t);
        if (shape == null) throw new RuntimeException("undefined shape %s".formatted(t));
        currentShape = shape;
        setX(shape.x);
        setY(shape.y);
        width = shape.width;
        height = shape.height;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(
                currentShape.texture,
                getX(),
                getY(),
                currentShape.offsetU + (isHovered() ? currentShape.hoveringOffsetU : 0),
                currentShape.offsetV + (isHovered() ? currentShape.hoveringOffsetV : 0),
                this.width,
                this.height,
                currentShape.textureWidth,
                currentShape.textureHeight
        );
        Component message = currentShape.text.get();
        if (message != null) {
            this.setMessage(message);
            this.renderString(
                    guiGraphics,
                    Minecraft.getInstance().font,
                    16777215 | Mth.ceil(this.alpha * 255.0F) << 24
            );
        }
        List<ResourceLocation> icon = currentShape.icon.get();
        if (icon != null && !icon.isEmpty()) {
            ResourceLocation iconItem = icon.get(0);
            if (iconItem != null) {
                Item item = BuiltInRegistries.ITEM.get(iconItem);
                guiGraphics.renderFakeItem(
                        item.getDefaultInstance(),
                        getX() + currentShape.iconPositionX,
                        getY() + currentShape.iconPositionY
                );
            }
        }
        if (this.isHovered()) {
            List<FormattedCharSequence> fcs = currentShape.tooltip.get();
            if (fcs == null) return;
            guiGraphics.renderTooltip(
                    Minecraft.getInstance().font,
                    fcs,
                    mouseX,
                    mouseY
            );
        }
    }

    @Override
    public void onPress() {
        this.currentShape.press.onPress(this);
    }

    public record ShapeDef(
            int x,
            int y,
            int width,
            int height,
            ResourceLocation texture,
            int offsetU,
            int offsetV,
            int hoveringOffsetU,
            int hoveringOffsetV,
            int textureWidth,
            int textureHeight,
            int iconPositionX,
            int iconPositionY,
            Supplier<Component> text,
            Supplier<List<ResourceLocation>> icon,
            Supplier<List<FormattedCharSequence>> tooltip,
            OnPress press
    ) {
    }
}
