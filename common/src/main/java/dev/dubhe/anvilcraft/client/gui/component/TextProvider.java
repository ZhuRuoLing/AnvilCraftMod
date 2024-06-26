package dev.dubhe.anvilcraft.client.gui.component;

import net.minecraft.network.chat.Component;

/**
 * 获取Widget文字
 */
@FunctionalInterface
public interface TextProvider {
    Component get();
}