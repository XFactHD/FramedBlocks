package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.*;

public class FramedSlopeBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        if (dir == Direction.UP && type == SlopeType.TOP)
        {
            return true;
        }
        else if (dir == Direction.DOWN && type == SlopeType.BOTTOM)
        {
            return true;
        }
        else if (type == SlopeType.HORIZONTAL)
        {
            Direction facing = state.getValue(PropertyHolder.FACING_HOR);
            return dir == facing || dir == facing.getCounterClockWise();
        }
        return state.getValue(PropertyHolder.FACING_HOR) == dir;
    };

    public FramedSlopeBlock() { super(BlockType.FRAMED_SLOPE); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.SLOPE_TYPE, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = withSlopeType(defaultBlockState(), context.getClickedFace(), context.getHorizontalDirection(), context.getClickLocation());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeBottom = Shapes.or(
                box(0,  0, 0, 16,  4, 16),
                box(0,  4, 0, 16,  8, 12),
                box(0,  8, 0, 16, 12,  8),
                box(0, 12, 0, 16, 16,  4)
        ).optimize();

        VoxelShape shapeTop = Shapes.or(
                box(0,  0, 0, 16,  4,  4),
                box(0,  4, 0, 16,  8,  8),
                box(0,  8, 0, 16, 12, 12),
                box(0, 12, 0, 16, 16, 16)
        ).optimize();

        VoxelShape shapeHorizontal = Shapes.or(
                box( 0, 0, 0,  4, 16, 16),
                box( 4, 0, 0,  8, 16, 12),
                box( 8, 0, 0, 12, 16,  8),
                box(12, 0, 0, 16, 16,  4)
        ).optimize();

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
            Direction dir = state.getValue(PropertyHolder.FACING_HOR);

            if (type == SlopeType.BOTTOM)
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeBottom));
            }
            else if (type == SlopeType.TOP)
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeHorizontal));
            }
        }

        return builder.build();
    }
}