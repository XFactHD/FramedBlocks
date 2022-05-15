package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

//TODO: move from two Direction properties to one Direction property and the Rotation property (needs to be adapted to handle y as rotation axis)
//      to eliminate the possibility of invalid state in 1.19
public class FramedSlopedPrismBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) -> side == state.getValue(BlockStateProperties.FACING).getOpposite();

    public static final SideSkipPredicate SKIP_PREDICATE = (level, pos, state, adjState, side) ->
    {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        Direction orientation = state.getValue(PropertyHolder.ORIENTATION);
        if (side != orientation)
        {
            return false;
        }

        if (adjState.is(FBContent.blockFramedSlopedPrism.get()))
        {
            Direction adjFacing = adjState.getValue(BlockStateProperties.FACING);
            Direction adjOrientation = adjState.getValue(PropertyHolder.ORIENTATION);

            return adjFacing == facing && adjOrientation == orientation.getOpposite() && SideSkipPredicate.compareState(level, pos, side);
        }
        else if (adjState.is(FBContent.blockFramedPrism.get()))
        {
            Direction.Axis adjAxis = adjState.getValue(BlockStateProperties.AXIS);
            Direction adjFacing = adjState.getValue(BlockStateProperties.FACING);

            return adjFacing == facing && adjAxis == orientation.getAxis() && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    };

    public FramedSlopedPrismBlock()
    {
        super(BlockType.FRAMED_SLOPED_PRISM);
        registerDefaultState(defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.NORTH)
                .setValue(BlockStateProperties.WATERLOGGED, false)
                .setValue(FramedProperties.SOLID, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.FACING, PropertyHolder.ORIENTATION, BlockStateProperties.WATERLOGGED, FramedProperties.SOLID, FramedProperties.GLOWING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState();

        Direction face = context.getClickedFace();
        state = state.setValue(BlockStateProperties.FACING, face);

        Direction orientation;
        if (Utils.isY(face))
        {
            orientation = context.getHorizontalDirection();
        }
        else
        {
            Vec3 subHit = Utils.fraction(context.getClickLocation());

            double xz = (Utils.isX(face) ? subHit.z() : subHit.x()) - .5;
            double y = subHit.y() - .5;

            if (Math.max(Math.abs(xz), Math.abs(y)) == Math.abs(xz))
            {
                if (Utils.isX(face))
                {
                    orientation = xz < 0 ? Direction.SOUTH : Direction.NORTH;
                }
                else
                {
                    orientation = xz < 0 ? Direction.EAST : Direction.WEST;
                }
            }
            else
            {
                orientation = y < 0 ? Direction.UP : Direction.DOWN;
            }
        }
        state = state.setValue(PropertyHolder.ORIENTATION, orientation);

        return withWater(state, context.getLevel(), context.getClickedPos());
    }



    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeBottom = Shapes.or(
                box( 0, 0, 0,   16, .5,   16),
                box(.5, 0, 0, 15.5,  4, 15.5),
                box( 4, 0, 0,   12,  8,   12)
        ).optimize();

        VoxelShape shapeTop = Shapes.or(
                box( 0, 15.5, 0,   16, 16,   16),
                box(.5,   12, 0, 15.5, 16, 15.5),
                box( 4,    8, 0,   12, 16,   12)
        ).optimize();

        VoxelShape shapeRight = Shapes.or(
                box(0,  0, 15.5,   16,   16, 16),
                box(0, .5,   12, 15.5, 15.5, 16),
                box(0,  4,    8,   12,   12, 16)
        ).optimize();

        VoxelShape shapeLeft = Shapes.or(
                box( 0,  0, 15.5, 16,   16, 16),
                box(.5, .5,   12, 16, 15.5, 16),
                box( 4,  4,    8, 16,   12, 16)
        ).optimize();

        VoxelShape shapeUp = Shapes.or(
                box( 0,  0, 15.5,   16, 16, 16),
                box(.5, .5,   12, 15.5, 16, 16),
                box( 4, 4,    8,   12,   16, 16)
        ).optimize();

        VoxelShape shapeDown = Shapes.or(
                box( 0, 0, 15.5,   16,   16, 16),
                box(.5, 0,   12, 15.5, 15.5, 16),
                box( 4,  0,    8,   12, 12, 16)
        ).optimize();

        for (BlockState state : states)
        {
            Direction facing = state.getValue(BlockStateProperties.FACING);
            Direction orientation = state.getValue(PropertyHolder.ORIENTATION);

            if (orientation == facing || orientation == facing.getOpposite())
            {
                builder.put(state, Shapes.block());
                continue;
            }

            if (Utils.isY(facing))
            {
                builder.put(
                        state,
                        Utils.rotateShape(
                                Direction.NORTH,
                                orientation,
                                facing == Direction.UP ? shapeBottom : shapeTop
                        )
                );
            }
            else
            {
                VoxelShape shape;
                if (orientation == Direction.UP) { shape = shapeUp; }
                else if (orientation == Direction.DOWN) { shape = shapeDown; }
                else if (orientation == facing.getClockWise()) { shape = shapeLeft; }
                else if (orientation == facing.getCounterClockWise()) { shape = shapeRight; }
                else { throw new IllegalArgumentException("Invalid orientation for direction!"); }

                builder.put(
                        state,
                        Utils.rotateShape(Direction.NORTH, facing, shape)
                );
            }
        }

        return builder.build();
    }
}
