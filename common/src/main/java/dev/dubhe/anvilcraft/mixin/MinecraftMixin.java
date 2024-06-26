package dev.dubhe.anvilcraft.mixin;

import dev.dubhe.anvilcraft.client.insight.InsightContentResourceReloadListener;
import dev.dubhe.anvilcraft.client.insight.InsightStructureResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    @Final
    private ReloadableResourceManager resourceManager;

    @Inject(
            method = "<init>(Lnet/minecraft/client/main/GameConfig;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/ResourceLoadStateTracker;startReload"
                            + "(Lnet/minecraft/client/ResourceLoadStateTracker$ReloadReason;Ljava/util/List;)V",
                    ordinal = 0
            )
    )
    public void beforeInitialResourceReload(GameConfig gameConfig, CallbackInfo ci) {
        resourceManager.registerReloadListener(InsightContentResourceReloadListener.INSTANCE);
        resourceManager.registerReloadListener(InsightStructureResourceReloadListener.INSTANCE);
    }
}
