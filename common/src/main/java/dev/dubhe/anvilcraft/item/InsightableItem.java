package dev.dubhe.anvilcraft.item;

import dev.dubhe.anvilcraft.init.ModItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class InsightableItem extends Item {
    public InsightableItem(Properties properties) {
        super(properties);
    }

    /**
     * 可寻思的物品/方块
     */
    public static boolean isItemInsightable(ItemStack stack) {
        return stack.getItem() instanceof InsightableItem
                || stack.getItem() instanceof InsightableBlockItem
                || (stack.is(ModItemTags.INSIGHTABLE));
    }
}
