package dev.dubhe.anvilcraft.client.renderer.blockentity;

import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.block.entity.CreativeGeneratorBlockEntity;
import dev.dubhe.anvilcraft.block.entity.ThermoelectricCollectorBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class ThermoelectricCollectorRenderer extends PowerProducerRenderer<ThermoelectricCollectorBlockEntity> {
    public static final ModelResourceLocation MODEL = ModelResourceLocation.standalone(
        AnvilCraft.of("block/thermoelectric_collector_cube")
    );

    /**
     * 创造发电机渲染
     */
    public ThermoelectricCollectorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected float elevation() {
        return 0.8f;
    }

    @Override
    protected ModelResourceLocation getModel() {
        return MODEL;
    }
}
