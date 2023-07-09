package xfacthd.framedblocks.common.block.stairs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.FramedVerticalDoubleStairsBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

public class FramedVerticalDoubleStairsBlock extends AbstractFramedDoubleBlock
{
    public FramedVerticalDoubleStairsBlock()
    {
        super(BlockType.FRAMED_VERTICAL_DOUBLE_STAIRS);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return withCornerFacing(
                defaultBlockState(),
                context.getClickedFace(),
                context.getHorizontalDirection(),
                context.getClickLocation()
        );
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorCornerBlock(state, mirror);
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);

        return new Tuple<>(
                FBContent.BLOCK_FRAMED_VERTICAL_STAIRS.get()
                        .defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, facing),
                FBContent.BLOCK_FRAMED_CORNER_PILLAR.get()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedVerticalDoubleStairsBlockEntity(pos, state);
    }



    public static BlockState itemModelSource()
    {
        return FBContent.BLOCK_FRAMED_VERTICAL_DOUBLE_STAIRS.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
