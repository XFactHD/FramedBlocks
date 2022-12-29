package xfacthd.framedblocks.common.block.slopeslab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedSlopeSlabBlock extends FramedBlock //TODO: check why states with top != topHalf can't occlude light
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

    public FramedSlopeSlabBlock()
    {
        super(BlockType.FRAMED_SLOPE_SLAB);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(PropertyHolder.TOP_HALF, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FramedProperties.FACING_HOR, FramedProperties.TOP, PropertyHolder.TOP_HALF, BlockStateProperties.WATERLOGGED, FramedProperties.SOLID, FramedProperties.GLOWING);
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

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (Utils.isY(face) || face == dir.getOpposite())
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if (rot != Rotation.NONE)
        {
            return state.cycle(PropertyHolder.TOP_HALF);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot) { return rotate(state, Direction.UP, rot); }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorFaceBlock(state, mirror);
    }



    public static final VoxelShape SHAPE_BOTTOM = Shapes.or(
            box(0, 0, 0, 16,   .5, 16),
            box(0, 0, 0, 16,    2, 15),
            box(0, 2, 0, 16,    4, 12),
            box(0, 4, 0, 16,    6,  8),
            box(0, 6, 0, 16, 7.75,  4),
            box(0, 6, 0, 16,    8, .5)
    ).optimize();

    public static final VoxelShape SHAPE_TOP = Shapes.or(
            box(0,   0, 0, 16, 2, .5),
            box(0, .25, 0, 16, 2,  4),
            box(0,   2, 0, 16, 4,  8),
            box(0,   4, 0, 16, 6, 12),
            box(0,   6, 0, 16, 8, 15),
            box(0, 7.5, 0, 16, 8, 16)
    ).optimize();

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            VoxelShape shape = state.getValue(FramedProperties.TOP) ? SHAPE_TOP : SHAPE_BOTTOM;
            if (state.getValue(PropertyHolder.TOP_HALF))
            {
                shape = shape.move(0, .5, 0);
            }

            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            builder.put(
                    state,
                    Utils.rotateShape(Direction.NORTH, facing, shape)
            );
        }

        return builder.build();
    }
}
