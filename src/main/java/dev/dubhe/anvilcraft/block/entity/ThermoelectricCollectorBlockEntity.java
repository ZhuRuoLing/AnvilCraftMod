package dev.dubhe.anvilcraft.block.entity;

import dev.dubhe.anvilcraft.api.chargecollector.ThermoManager;
import dev.dubhe.anvilcraft.api.power.IPowerProducer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ThermoelectricCollectorBlockEntity extends BlockEntity implements IPowerProducer {

    private int power = 0;
    @Getter
    @Setter
    private PowerGrid grid;
    @Getter
    private int time = 0;
    private boolean previousSyncFailed = false;

    public ThermoelectricCollectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    /**
     * tick
     */
    public void tick() {
        if (level instanceof ServerLevel) {
            if (previousSyncFailed && grid != null) {
                previousSyncFailed = false;
                grid.markChanged();
            }
            BlockPos blockPos = getBlockPos();
            BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    for (int dz = -2; dz <= 2; dz++) {
                        mutableBlockPos.set(
                            blockPos.getX() + dx,
                            blockPos.getY() + dy,
                            blockPos.getZ() + dz
                        );
                        BlockState blockState = level.getBlockState(mutableBlockPos);
                        ThermoManager.getInstance(level)
                            .addThermoBlock(
                                mutableBlockPos.immutable(),
                                blockState,
                                this
                            );
                    }
                }
            }
        }
        time++;
    }

    @Override
    public Level getCurrentLevel() {
        return level;
    }

    public void setPower(int power) {
        this.power = power;
        if (level instanceof ServerLevel) {
            if (grid != null) {
                this.grid.markChanged();
                return;
            }
            previousSyncFailed = true;
        }
    }

    @Override
    public int getOutputPower() {
        return power;
    }

    @Override
    public @NotNull BlockPos getPos() {
        return getBlockPos();
    }
}
