package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.*;

public class FramedThreewayCornerBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        boolean top = state.getValue(FramedProperties.TOP);
        if (top && dir == Direction.UP)
        {
            return true;
        }
        else if (!top && dir == Direction.DOWN)
        {
            return true;
        }

        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (facing == dir) { return true; }

        return facing.getCounterClockWise() == dir;
    };

    public FramedThreewayCornerBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FramedProperties.FACING_HOR, FramedProperties.TOP, BlockStateProperties.WATERLOGGED, FramedProperties.SOLID, FramedProperties.GLOWING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState();

        Direction facing = context.getHorizontalDirection();
        state = state.setValue(FramedProperties.FACING_HOR, facing);

        state = withWater(state, context.getLevel(), context.getClickedPos());
        return withTop(state, context.getClickedFace(), context.getClickLocation());
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (Utils.isY(face) || face == dir.getOpposite() || face == dir.getClockWise())
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if ((face == dir || face == dir.getCounterClockWise()) && rot != Rotation.NONE)
        {
            return state.cycle(FramedProperties.TOP);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot) { return rotate(state, Direction.UP, rot); }



    public static ImmutableMap<BlockState, VoxelShape> generateThreewayShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);

            if (state.getValue(FramedProperties.TOP))
            {
                VoxelShape shapeTop = Shapes.or(
                        box( 0, 15.5, 0,   .5, 16,   16),
                        box( 0,   12, 0,    4, 16, 15.5),
                        box( 0,    8, 0,    4, 12,   12),
                        box( 0,    4, 0,    8,  8,    8),
                        box( 0,   .5, 0,    4,  4,    4),
                        box( 0,    0, 0,   .5,  4,   .5),
                        box( 4,   12, 0,    8, 16,   12),
                        box( 4,    8, 0,    8, 12,   12),
                        box( 8,   12, 0,   12, 16,    8),
                        box( 8,    8, 0,   12, 12,    8),
                        box(12,   12, 0, 15.5, 16,    4),
                        box(12, 15.5, 0,   16, 16,   .5)
                ).optimize();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                VoxelShape shapeBottom = Shapes.or(
                        box( 0,  0, 0,   .5,   .5,   16),
                        box( 0,  0, 0,    4,    4, 15.5),
                        box( 0,  4, 0,    4,    8,   12),
                        box( 0,  8, 0,    8,   12,    8),
                        box( 0, 12, 0,   .5,   16,   .5),
                        box( 0, 12, 0,    4, 15.5,    4),
                        box( 4,  0, 0,    8,    4,   12),
                        box( 4,  4, 0,    8,    8,   12),
                        box( 8,  0, 0,   12,    4,    8),
                        box( 8,  4, 0,   12,    8,    8),
                        box(12,  0, 0, 15.5,    4,    4),
                        box(12,  0, 0,   16,   .5,   .5)
                ).optimize();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeBottom));
            }
        }

        return builder.build();
    }

    public static ImmutableMap<BlockState, VoxelShape> generateInnerThreewayShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);

            if (state.getValue(FramedProperties.TOP))
            {
                VoxelShape shapeTop = Shapes.or(
                        box(   0, 15.5,    0,   16, 16,   16),
                        box(   0,   12,    0,   16, 16, 15.5),
                        box(   0,   12, 15.5, 15.5, 16,   16),
                        box(   0,    8,    0,   12, 12,   16),
                        box(  12,    8,    0,   16, 12,   12),
                        box(   0,    4,    0,   16,  8,    8),
                        box(   0,    4,    8,    8,  8,   16),
                        box(   0,   .5,    0,   16,  4,    4),
                        box(   0,    0,    0, 15.5, .5,    4),
                        box(15.5,    0,    0,   16, .5,   .5),
                        box(   0,   .5,    4,    4,  4,   16),
                        box(   0,    0,    4,    4, .5, 15.5),
                        box(   0,    0, 15.5,   .5, .5,   16),
                        box(   4,    0,    4,    8,  4,   12),
                        box(   8,    0,    4,   12,  4,    8)
                ).optimize();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                VoxelShape shapeBottom = Shapes.or(
                        box(   0,    0,    0,   16,   .5,   16),
                        box(   0,    0,    0,   16,    4, 15.5),
                        box(   0,    0, 15.5, 15.5,    4,   16),
                        box(   0,    4,    0,   12,    8,   16),
                        box(  12,    4,    0,   16,    8,   12),
                        box(   0,    8,    0,   16,   12,    8),
                        box(   0,    8,    8,    8,   12,   16),
                        box(   0,   12,    0,   16, 15.5,    4),
                        box(   0, 15.5,    0, 15.5,   16,    4),
                        box(15.5, 15.5,    0,   16,   16,   .5),
                        box(   0,   12,    4,    4, 15.5,   16),
                        box(   0, 15.5,    4,    4,   16, 15.5),
                        box(   0, 15.5, 15.5,   .5,   16,   16),
                        box(   4,   12,    4,    8,   16,   12),
                        box(   8,   12,    4,   12,   16,    8)
                ).optimize();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeBottom));
            }
        }

        return builder.build();
    }
}