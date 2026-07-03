package io.github.xfacthd.framedblocks.api.render.fakelevel;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

/// Root interface of fake levels used for rendering block models outside of chunk geometry.
public sealed interface BlockRenderFakeLevel extends BlockAndTintGetter permits DelegatingBlockRenderFakeLevel, FreestandingBlockRenderFakeLevel {
    /// {@return the anchor position at which the state and model data should "appear"}
    BlockPos pos();

    /// {@return the blockstate to provide at the anchor position}
    BlockState state();

    /// {@return the model data to provide at the anchor position}
    ModelData modelData();

    @Override
    default BlockState getBlockState(BlockPos pos) {
        if (pos.equals(pos())) {
            return state();
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    default @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    default FluidState getFluidState(BlockPos pos) {
        return getBlockState(pos).getFluidState();
    }

    @Override
    default int getBrightness(LightLayer layer, BlockPos pos) {
        return LightEngine.MAX_LEVEL;
    }

    @Override
    default ModelData getModelData(BlockPos pos) {
        if (pos.equals(pos())) {
            return modelData();
        }
        return ModelData.EMPTY;
    }
}
