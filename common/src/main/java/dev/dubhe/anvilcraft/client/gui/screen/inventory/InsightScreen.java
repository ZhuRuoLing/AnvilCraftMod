package dev.dubhe.anvilcraft.client.gui.screen.inventory;

import com.mojang.logging.LogUtils;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.client.gui.component.button.ShapeModifiableButton;
import dev.dubhe.anvilcraft.client.gui.component.button.TexturedButton;
import dev.dubhe.anvilcraft.client.insight.InsightData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class InsightScreen extends Screen {

    private static final ResourceLocation CONTAINER_LOCATION =
            AnvilCraft.of("textures/gui/container/insight/insight.png");

    private final int imageWidth = 308;
    private final int imageHeight = 166;
    private int leftPos;
    private int topPos;
    private Mode currentMode = Mode.LIST;
    private ShapeModifiableButton<Mode>[] buttons = new ShapeModifiableButton[8];
    private final Logger logger = LogUtils.getLogger();
    private final List<InsightData> insightDatas = new ArrayList<>();
    private List<InsightData> filteredInsightDatas = new ArrayList<>(insightDatas);
    private String searchText = "";
    private int scrollAmount = 0;
    private InsightData selectedData = null;

    public InsightScreen() {
        super(Component.translatable("screen.anvilcraft.insight.title"));
        this.minecraft = Minecraft.getInstance();
    }

    /**
     * 设置搜索
     */
    public void setInitialSearch(ItemStack itemStack) {
        ResourceLocation lo = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        searchText = "#" + lo;
        onSearchUpdate(searchText);
    }

    private void onSearchUpdate(String text) {
        scrollAmount = 0;
        searchText = text;
        if (text.startsWith("#")) {
            filteredInsightDatas = insightDatas.stream()
                    .filter(it -> it.getFeature().stream()
                            .anyMatch(it1 -> it1.contains(text))
                    )
                    .toList();
        } else {
            filteredInsightDatas = insightDatas.stream()
                    .filter(it -> it.getFormattedTitle().contains(text))
                    .toList();
        }
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        EditBox editBox = new EditBox(
                this.minecraft.font,
                leftPos + 6,
                topPos + 25,
                70,
                12,
                Component.translatable("screen.anvilcraft.insight.search")
        );
        editBox.setResponder(this::onSearchUpdate);
        editBox.setValue(searchText);
        addRenderableWidget(
                editBox
        );
        addRenderableWidget(new TexturedButton(
                leftPos + 6,
                topPos + 6,
                16,
                16,
                AnvilCraft.of("textures/gui/container/insight/insight_list.png"),
                (b) -> {
                    this.currentMode = Mode.LIST;
                    update();
                }
        ));
        addRenderableWidget(new TexturedButton(
                leftPos + 26,
                topPos + 6,
                16,
                16,
                AnvilCraft.of("textures/gui/container/insight/insight_thumbnail.png"),
                (b) -> {
                    this.currentMode = Mode.THUMBNAIL;
                    update();
                }
        ));
        addRenderableWidget(new TexturedButton(
                leftPos + 239,
                topPos + 9,
                10,
                10,
                AnvilCraft.of("textures/gui/container/insight/insight_layer_switch.png"),
                (b) -> {

                })
        );
        addRenderableWidget(new TexturedButton(
                leftPos + 255,
                topPos + 9,
                10,
                10,
                AnvilCraft.of("textures/gui/container/insight/insight_layer_up.png"),
                (b) -> {

                })
        );
        addRenderableWidget(new TexturedButton(
                leftPos + 292,
                topPos + 9,
                10,
                10,
                AnvilCraft.of("textures/gui/container/insight/insight_layer_down.png"),
                (b) -> {

                })
        );

        for (int i = 0; i < 4; i++) {
            for (int j = i; j < i + 2; j++) {
                int index = i + j;
                ShapeModifiableButton<Mode> button = new ShapeModifiableButton<>(index);
                buttons[index] = button;
                addRenderableWidget(button);
                button.createShapeDef(
                        Mode.LIST,
                        new ShapeModifiableButton.ShapeDef(
                                leftPos + 6,
                                topPos + 40 + index * 15,
                                64,
                                15,
                                AnvilCraft.of("textures/gui/container/insight/insight_list_button.png"),
                                0,
                                0,
                                0,
                                15,
                                64,
                                30,
                                0,
                                0,
                                () -> Component.empty(),
                                () -> null,
                                () -> null,
                                (b) -> {

                                }
                        )
                );
                button.createShapeDef(
                        Mode.THUMBNAIL,
                        new ShapeModifiableButton.ShapeDef(
                                leftPos + 6 + (j - i) * 32,
                                topPos + 40 + i * 30,
                                32,
                                30,
                                AnvilCraft.of("textures/gui/container/insight/insight_thumbnail_button.png"),
                                0,
                                0,
                                0,
                                30,
                                32,
                                60,
                                8,
                                6,
                                () -> null,
                                () -> AnvilCraft.of("magnet_block"),
                                () -> null,
                                (b) -> {

                                }
                        )
                );
                button.setCurrentShape(currentMode);
            }
        }
    }

    private void update() {
        scrollAmount = 0;
        for (ShapeModifiableButton<Mode> button : buttons) {
            button.setCurrentShape(currentMode);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        renderBackground(guiGraphics);
        if (selectedData != null) {
            guiGraphics.drawString(
                    minecraft.font,
                    Component.translatable(selectedData.getTitle()).getVisualOrderText(),
                    leftPos + 84,
                    topPos + 10,
                    16777215,
                    true
            );
        } else {
            guiGraphics.drawString(
                    minecraft.font,
                    this.title.getVisualOrderText(),
                    leftPos + 84,
                    topPos + 10,
                    16777215,
                    true
            );
        }
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
        LIST(1), THUMBNAIL(2);

        private final int scrollUnit;

        Mode(int scrollUnit) {
            this.scrollUnit = scrollUnit;
        }
    }
}
