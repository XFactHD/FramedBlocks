package io.github.xfacthd.framedblocks.client.model.baked;

import io.github.xfacthd.framedblocks.api.model.data.ModelDataEntry;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

record DataAppendingDelegateLevel(BlockAndTintGetter delegate, BlockPos pos, ModelDataEntry<?> data) implements BlockAndTintGetter {
    @Override
    public BlockState getBlockState(BlockPos pos) {
        return delegate.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return delegate.getFluidState(pos);
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return delegate.getBlockEntity(pos);
    }

    @Override
    public int getBlockTint(BlockPos pos, ColorResolver color) {
        return delegate.getBlockTint(pos, color);
    }

    @Override
    public ModelData getModelData(BlockPos pos) {
        ModelData modelData = delegate.getModelData(pos);
        if (pos.equals(this.pos)) {
            ModelData.Builder builder = modelData.derive();
            data.apply(builder);
            modelData = builder.build();
        }
        return modelData;
    }

    @Override
    public CardinalLighting cardinalLighting() {
        return delegate.cardinalLighting();
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return delegate.getLightEngine();
    }

    @Override
    public @Nullable AuxiliaryLightManager getAuxLightManager(ChunkPos pos) {
        return delegate.getAuxLightManager(pos);
    }

    @Override
    public int getHeight() {
        return delegate.getHeight();
    }

    @Override
    public int getMinY() {
        return delegate.getMinY();
    }
}
