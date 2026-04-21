package io.github.xfacthd.framedblocks.common.block.interactive.banner;

import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedBannerBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

abstract class AbstractFramedBannerBlock extends FramedBlock {
    protected AbstractFramedBannerBlock(BlockType blockType, Properties props) {
        super(blockType, props, modProps -> modProps
                .forceSolidOn()
                .noCollision()
                .strength(1)
        );
    }

    protected abstract Direction getAttachDir(BlockState state);

    @Override
    @SuppressWarnings("deprecation")
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction attachDir = getAttachDir(state);
        return level.getBlockState(pos.relative(attachDir)).isSolid();
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess ticks,
            BlockPos pos,
            Direction side,
            BlockPos adjPos,
            BlockState adjState,
            RandomSource random
    ) {
        if (side == getAttachDir(state) && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, ticks, pos, side, adjPos, adjState, random);
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState state) {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FramedBannerBlockEntity(pos, state);
    }

    @Override
    public @Nullable BlockState getItemModelSource() {
        return null;
    }

    @Override
    public boolean shouldRenderAsBlockInJadeTooltip() {
        return false;
    }
}
