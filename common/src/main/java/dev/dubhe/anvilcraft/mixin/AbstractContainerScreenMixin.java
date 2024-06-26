package dev.dubhe.anvilcraft.mixin;

import dev.dubhe.anvilcraft.client.gui.screen.inventory.InsightScreen;
import dev.dubhe.anvilcraft.item.InsightableItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin extends Screen {
    @Shadow
    @Nullable
    protected Slot hoveredSlot;
    @Unique
    private static final int PROGRESS_MAX = 20;
    @Unique
    private int anvilCraft$progress = 0;
    @Unique
    private boolean anvilCraft$pressed = false;

    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    void onKeyPress(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (GLFW.GLFW_KEY_X == keyCode) {
            anvilCraft$pressed = true;
        }
    }

    @Redirect(
            method = "renderTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;"
                            + "getTooltipFromContainerItem(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"
            )
    )
    List<Component> onRenderTooltip(AbstractContainerScreen<?> instance, ItemStack stack) {

        List<Component> result = stack.getTooltipLines(
                minecraft.player,
                minecraft.options.advancedItemTooltips
                        ? TooltipFlag.Default.ADVANCED
                        : TooltipFlag.Default.NORMAL
        );
        if (!InsightableItem.isItemInsightable(stack)) return result;
        MutableComponent line = Component.translatable("tooltip.anvilcraft.press_to_insight")
                .withStyle(ChatFormatting.GRAY);
        if (anvilCraft$pressed) {
            line.append(Component.literal(" [").withStyle(ChatFormatting.WHITE));
            int remaining = PROGRESS_MAX - anvilCraft$progress;
            Component remain = Component.literal("|".repeat(remaining)).withStyle(ChatFormatting.GRAY);
            Component progress = Component.literal("|".repeat(anvilCraft$progress)).withStyle(ChatFormatting.GREEN);
            line.append(progress).append(remain).append("]");
        }
        result.add(line);
        return result;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (GLFW.GLFW_KEY_X == keyCode) {
            anvilCraft$pressed = false;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    void onTick(CallbackInfo ci) {
        if (hoveredSlot != null) {
            Slot slot = hoveredSlot;
            ItemStack itemStack = slot.getItem();
            if (!InsightableItem.isItemInsightable(itemStack)) {
                anvilCraft$progress = 0;
            }
            if (anvilCraft$pressed) {
                if (anvilCraft$progress >= PROGRESS_MAX) {
                    anvilCraft$progress = 0;
                    openInsight(itemStack);
                } else {
                    anvilCraft$progress++;
                }
            } else {
                anvilCraft$progress = 0;
            }
        } else {
            anvilCraft$progress = 0;
        }
    }

    @Unique
    private void openInsight(ItemStack itemStack) {
        InsightScreen screen = new InsightScreen();
        screen.setInitialSearch(itemStack);
        Minecraft.getInstance().setScreen(screen);
    }
}
