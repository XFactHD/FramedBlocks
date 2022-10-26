package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.util.CtmPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.blockentity.FramedDoublePrismBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedDoublePrismBlock extends AbstractFramedDoubleBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) ->
    {
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        return side != null && side.getAxis() != axis;
    };

    public FramedDoublePrismBlock() { super(BlockType.FRAMED_DOUBLE_PRISM); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING, BlockStateProperties.AXIS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return FramedPrismBlock.getStateForPlacement(context, defaultBlockState(), getBlockType());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        if (rot == Rotation.NONE) { return state; }

        Direction dir = state.getValue(BlockStateProperties.FACING);
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);

        if (Utils.isY(dir))
        {
            if (rot == Rotation.CLOCKWISE_180)
            {
                return state;
            }

            return state.setValue(
                    BlockStateProperties.AXIS,
                    Utils.nextAxisNotEqualTo(axis, dir.getAxis())
            );
        }
        else
        {
            if (!axis.isVertical())
            {
                state = state.setValue(
                        BlockStateProperties.AXIS,
                        Utils.nextAxisNotEqualTo(axis, Direction.Axis.Y)
                );
            }
            return state.setValue(BlockStateProperties.FACING, rot.rotate(dir));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorFaceBlock(state, BlockStateProperties.FACING, mirror);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoublePrismBlockEntity(pos, state);
    }
}
