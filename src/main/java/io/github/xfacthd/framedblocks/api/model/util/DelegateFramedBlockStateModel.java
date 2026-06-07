package io.github.xfacthd.framedblocks.api.model.util;

import io.github.xfacthd.framedblocks.api.model.AbstractFramedBlockStateModel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

final class DelegateFramedBlockStateModel extends AbstractFramedBlockStateModel {
    DelegateFramedBlockStateModel(BlockStateModel baseModel, BlockState state) {
        super(baseModel, state);
    }

    @Override
    public int collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts, int miscTintOffset) {
        delegate.collectParts(level, pos, state, random, parts);
        return miscTintOffset;
    }
}
