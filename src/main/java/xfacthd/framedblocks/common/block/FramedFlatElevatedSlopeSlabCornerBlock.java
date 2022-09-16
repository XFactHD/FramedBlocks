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

public class FramedFlatElevatedSlopeSlabCornerBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) ->
    {
        if (state.getValue(FramedProperties.TOP))
        {
            return side == Direction.UP;
        }
        else
        {
            return side == Direction.DOWN;
        }
    };

    public static final CtmPredicate CTM_PREDICATE_INNER = (state, side) ->
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (side == dir || side == dir.getCounterClockWise())
        {
            return true;
        }
        return CTM_PREDICATE.test(state, side);
    };

    public FramedFlatElevatedSlopeSlabCornerBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FramedProperties.FACING_HOR, FramedProperties.TOP, FramedProperties.SOLID, FramedProperties.GLOWING, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction face = context.getClickedFace();
        Direction facing = Utils.isY(face) ? context.getHorizontalDirection() : face.getOpposite();

        BlockState state = defaultBlockState().setValue(FramedProperties.FACING_HOR, facing);

        state = withTop(state, context.getClickedFace(), context.getClickLocation());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }



    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);

            VoxelShape shape = top ? FramedSlopeSlabBlock.SHAPE_TOP : FramedSlopeSlabBlock.SHAPE_BOTTOM.move(0, .5, 0);
            shape = Shapes.or(shape, box(0, top ? 8 : 0, 0, 16, top ? 16 : 8, 16));

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
            boolean top = state.getValue(FramedProperties.TOP);

            VoxelShape shape = top ? FramedSlopeSlabBlock.SHAPE_TOP : FramedSlopeSlabBlock.SHAPE_BOTTOM.move(0, .5, 0);
            shape = Shapes.or(shape, box(0, top ? 8 : 0, 0, 16, top ? 16 : 8, 16));

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
