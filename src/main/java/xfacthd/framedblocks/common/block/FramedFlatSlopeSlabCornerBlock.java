package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedFlatSlopeSlabCornerBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) ->
    {
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
        if (state.getValue(FramedProperties.TOP))
        {
            return topHalf && side == Direction.UP;
        }
        else
        {
            return !topHalf && side == Direction.DOWN;
        }
    };

    public FramedFlatSlopeSlabCornerBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(PropertyHolder.TOP_HALF, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FramedProperties.FACING_HOR, FramedProperties.TOP, PropertyHolder.TOP_HALF, FramedProperties.SOLID, FramedProperties.GLOWING, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction face = context.getClickedFace();
        Direction facing = Utils.isY(face) ? context.getHorizontalDirection() : face.getOpposite();

        BlockState state = defaultBlockState().setValue(FramedProperties.FACING_HOR, facing);

        state = withTop(state, PropertyHolder.TOP_HALF, context.getClickedFace(), context.getClickLocation());
        state = state.setValue(FramedProperties.TOP, context.getPlayer() != null && context.getPlayer().isShiftKeyDown());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }



    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);

            VoxelShape shape = state.getValue(FramedProperties.TOP) ? FramedSlopeSlabBlock.SHAPE_TOP : FramedSlopeSlabBlock.SHAPE_BOTTOM;
            if (state.getValue(PropertyHolder.TOP_HALF))
            {
                shape = shape.move(0, .5, 0);
            }

            builder.put(
                    state,
                    Shapes.join(
                            Utils.rotateShape(Direction.NORTH, facing, shape),
                            Utils.rotateShape(Direction.NORTH, facing.getCounterClockWise(), shape),
                            BooleanOp.AND
                    )
            );
        }

        return builder.build();
    }

    public static ImmutableMap<BlockState, VoxelShape> generateInnerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);

            VoxelShape shape = state.getValue(FramedProperties.TOP) ? FramedSlopeSlabBlock.SHAPE_TOP : FramedSlopeSlabBlock.SHAPE_BOTTOM;
            if (state.getValue(PropertyHolder.TOP_HALF))
            {
                shape = shape.move(0, .5, 0);
            }

            builder.put(
                    state,
                    Shapes.or(
                            Utils.rotateShape(Direction.NORTH, facing, shape),
                            Utils.rotateShape(Direction.NORTH, facing.getCounterClockWise(), shape)
                    )
            );
        }

        return builder.build();
    }
}
