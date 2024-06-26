package dev.dubhe.anvilcraft.client.gui.screen.inventory;

import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.client.gui.component.button.ShapeModifiableButton;
import dev.dubhe.anvilcraft.client.gui.component.button.TexturedButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InsightScreen extends Screen {

    private static final ResourceLocation CONTAINER_LOCATION =
            AnvilCraft.of("textures/gui/container/insight/insight.png");

    private final int imageWidth = 308;
    private final int imageHeight = 166;
    private int leftPos;
    private int topPos;
    private Mode currentMode = Mode.LIST;
    private ShapeModifiableButton<Mode>[] buttons = new ShapeModifiableButton[8];

    public InsightScreen() {
        super(Component.translatable("screen.anvilcraft.insight.title"));
    }

    public void setInitialSearch(ItemStack itemStack) {

    }

    @Override
    protected void init() {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        addRenderableWidget(new TexturedButton(
                6,
                6,
                16,
                16,
                AnvilCraft.of("textures/gui/container/insight/insight_list.png"),
                (b) -> {
                    this.currentMode = Mode.LIST;
                    update();
                }
        ));
        addRenderableWidget(new TexturedButton(
                26,
                6,
                16,
                16,
                AnvilCraft.of("textures/gui/container/insight/insight_thumbnail.png"),
                (b) -> {
                    this.currentMode = Mode.THUMBNAIL;
                    update();
                }
        ));
        addRenderableWidget(new TexturedButton(
                239,
                9,
                10,
                10,
                AnvilCraft.of("textures/gui/container/insight/insight_layer_switch.png"),
                (b) -> {

                })
        );
        addRenderableWidget(new TexturedButton(
                255,
                9,
                10,
                10,
                AnvilCraft.of("textures/gui/container/insight/insight_layer_up.png"),
                (b) -> {

                })
        );
        addRenderableWidget(new TexturedButton(
                292,
                9,
                10,
                10,
                AnvilCraft.of("textures/gui/container/insight/insight_layer_down.png"),
                (b) -> {

                })
        );
    }

    private void update(){

    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics) {
        super.renderBackground(guiGraphics);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(CONTAINER_LOCATION,
                i,
                j,
                0,
                0,
                this.imageWidth,
                this.imageHeight,
                512,
                256
        );
    }

    enum Mode {
        LIST, THUMBNAIL
    }
}
