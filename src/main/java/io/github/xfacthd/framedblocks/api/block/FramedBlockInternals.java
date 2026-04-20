package io.github.xfacthd.framedblocks.api.block;

import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.util.ConfigView;
import net.minecraft.core.BlockPos;
import net.minecraft.util.TriState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

final class FramedBlockInternals {
    static boolean isEmissiveRendering(@SuppressWarnings("unused") BlockState state, BlockGetter level, BlockPos pos) {
        AbstractFramedBlockData fbData = level.getModelData(pos).get(AbstractFramedBlockData.PROPERTY);
        return fbData != null && fbData.isCamoEmissive();
    }

    static boolean isViewBlocking(BlockState state, BlockGetter level, BlockPos pos) {
        AbstractFramedBlockData fbData = level.getModelData(pos).get(AbstractFramedBlockData.PROPERTY);
        TriState viewBlocking;
        if (fbData != null && (viewBlocking = fbData.isViewBlocking()) != TriState.DEFAULT) {
            return viewBlocking.isTrue();
        }
        return isSuffocatingDefault(state, level, pos);
    }

    static boolean isSuffocating(BlockState state, BlockGetter level, BlockPos pos) {
        if (!isSuffocatingDefault(state, level, pos)) {
            return false;
        }

        IFramedBlock block = (IFramedBlock) state.getBlock();
        if (ConfigView.Server.INSTANCE.enableIntangibility() && block.getBlockType().allowMakingIntangible()) {
            return !block.isIntangible(state, level, pos, null);
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    private static boolean isSuffocatingDefault(BlockState state, BlockGetter level, BlockPos pos) {
        // Copy of the default suffocation check
        return state.blocksMotion() && state.isCollisionShapeFullBlock(level, pos);
    }

    private FramedBlockInternals() { }
}
