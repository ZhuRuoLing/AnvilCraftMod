package dev.dubhe.anvilcraft.network;

import dev.dubhe.anvilcraft.api.network.Packet;
import dev.dubhe.anvilcraft.client.gui.screen.inventory.CraftingMachineScreen;
import dev.dubhe.anvilcraft.init.ModNetworks;
import dev.dubhe.anvilcraft.inventory.CraftingMachineMenu;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.NotNull;

@Getter
public class MachineRecordMaterialPack implements Packet {
    private final boolean recordMaterial;

    public MachineRecordMaterialPack(boolean recordMaterial) {
        this.recordMaterial = recordMaterial;
    }

    public MachineRecordMaterialPack(@NotNull FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public void write(@NotNull FriendlyByteBuf buf) {
        buf.writeBoolean(this.isRecordMaterial());
    }

    @Override
    public PacketType<?> getType() {
        return ModNetworks.MATERIAL_PACKET_TYPE;
    }

    @Override
    public void receive(@NotNull MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, PacketSender sender) {
        server.execute(() -> {
            if (!player.hasContainerOpen()) return;
            if (!(player.containerMenu instanceof CraftingMachineMenu menu)) return;
            menu.setRecordMaterial(this.isRecordMaterial());
            this.send(player);
        });
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void receive(@NotNull Minecraft client, ClientPacketListener handler, PacketSender responseSender) {
        client.execute(() -> {
            if (!(client.screen instanceof CraftingMachineScreen screen)) return;
            if (screen.getRecordButton() == null) return;
            screen.getRecordButton().setRecord(this.isRecordMaterial());
        });
    }
}
