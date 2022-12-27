package xfacthd.framedblocks.common.block.slopeslab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.FramedBlock;
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

    @Override
    public BlockState rotate(BlockState state, BlockHitResult hit, Rotation rot)
    {
        Direction face = hit.getDirection();

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (face == dir.getOpposite() || face == dir.getClockWise())
        {
            if (getBlockType() == BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER)
            {
                face = Direction.UP;
            }
            else //FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER
            {
                Vec3 vec = Utils.fraction(hit.getLocation());

                Direction perpDir = face == dir.getClockWise() ? dir : dir.getCounterClockWise();
                double hor = Utils.isX(perpDir) ? vec.x() : vec.z();
                if (!Utils.isPositive(perpDir))
                {
                    hor = 1D - hor;
                }

                double y = vec.y();
                if (state.getValue(PropertyHolder.TOP_HALF))
                {
                    y -= .5;
                }
                if (state.getValue(FramedProperties.TOP))
                {
                    y = .5 - y;
                }
                if ((y * 2D) >= hor)
                {
                    face = Direction.UP;
                }
            }
        }

        return rotate(state, face, rot);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        if (Utils.isY(face))
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
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
        return Utils.mirrorCornerBlock(state, mirror);
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
