package io.github.xfacthd.framedblocks.common.block.slab;

import io.github.xfacthd.framedblocks.api.block.CopycatStyleBlock;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.block.cube.FramedCollapsibleCubeBlock;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slab.FramedAdjustableDoubleBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public abstract class FramedAdjustableDoubleBlock extends FramedDoubleBlock implements CopycatStyleBlock.StateDependent {
    private final Function<BlockState, Direction> facingGetter;

    protected FramedAdjustableDoubleBlock(BlockType type, Properties props, Function<BlockState, Direction> facingGetter) {
        super(type, props);
        this.facingGetter = facingGetter;
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player) {
        if (player.getMainHandItem().getItem() == FBContent.ITEM_FRAMED_HAMMER.value()) {
            if (level.getBlockEntity(pos) instanceof FramedAdjustableDoubleBlockEntity be) {
                return be.handleDeform(player);
            }
        }
        return super.handleBlockLeftClick(state, level, pos, player);
    }

    @Override
    public FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FramedAdjustableDoubleBlockEntity(pos, state);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState();
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state) {
        Direction facing = ((FramedAdjustableDoubleBlock) state.getBlock()).getFacing(state);
        BlockState defState = FBContent.BLOCK_FRAMED_COLLAPSIBLE_CUBE.value().defaultBlockState();
        defState = copyProperty(state, defState, FramedProperties.COPYCAT_STYLE);
        int solidFirst = ~(1 << facing.ordinal()) & FramedCollapsibleCubeBlock.ALL_SOLID;
        int solidSecond = ~(1 << facing.getOpposite().ordinal()) & FramedCollapsibleCubeBlock.ALL_SOLID;
        return new DoubleBlockParts(
                defState.setValue(PropertyHolder.SOLID_FACES, solidFirst),
                defState.setValue(PropertyHolder.SOLID_FACES, solidSecond)
        );
    }

    public Direction getFacing(BlockState state) {
        return facingGetter.apply(state);
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState();
    }
}
