package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

//TODO: move from two Direction properties to one Direction property and the Rotation property (needs to be adapted to handle y as rotation axis)
//      to eliminate the possibility of invalid state in 1.19
public class FramedSlopedPrismBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE_INNER = (state, side) ->
    {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        Direction orientation = state.getValue(PropertyHolder.ORIENTATION);

        if (side != null && side.getAxis() != facing.getAxis() && side.getAxis() != orientation.getAxis())
        {
            return true;
        }
        return side == facing.getOpposite() || side == orientation.getOpposite();
    };

    public FramedSlopedPrismBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.NORTH)
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
        return getStateForPlacement(context, defaultBlockState(), getBlockType());
    }

    public static BlockState getStateForPlacement(BlockPlaceContext context, BlockState state, IBlockType blockType)
    {

        Direction face = context.getClickedFace();
        state = state.setValue(BlockStateProperties.FACING, face);

        Direction orientation;
        if (Utils.isY(face))
        {
            orientation = context.getHorizontalDirection();
            if (blockType == BlockType.FRAMED_INNER_SLOPED_PRISM || blockType == BlockType.FRAMED_DOUBLE_SLOPED_PRISM)
            {
                orientation = orientation.getOpposite();
            }
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

        if (blockType == BlockType.FRAMED_SLOPED_PRISM)
        {
            state = withWater(state, context.getLevel(), context.getClickedPos());
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        if (rot == Rotation.NONE) { return state; }

        Direction dir = state.getValue(BlockStateProperties.FACING);
        Direction orientation = state.getValue(PropertyHolder.ORIENTATION);

        Direction[] dirs = Direction.values();
        do
        {
            int idx;
            if (rot == Rotation.COUNTERCLOCKWISE_90)
            {
                idx = orientation.ordinal() - 1;
                if (idx < 0)
                {
                    idx = dirs.length - 1;
                }
            }
            else
            {
                idx = (orientation.ordinal() + 1) % dirs.length;
            }
            orientation = dirs[idx];
        }
        while (orientation.getAxis() == dir.getAxis());

        return state.setValue(PropertyHolder.ORIENTATION, orientation);
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

    public static ImmutableMap<BlockState, VoxelShape> generateInnerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeBottom = Shapes.or(
                box(   4, 0,    0,   12,   12,   16),
                box(15.5, 0,    0,   16,   16,   16),
                box(   0, 0,    0,   .5,   16,   16),
                box(  12, 0,    0, 15.5, 15.5,   16),
                box(  .5, 0,    0,    4, 15.5,   16),
                box(   0, 0, 15.5,   16,   16,   16),
                box(  .5, 0,   12, 15.5, 15.5, 15.5)
        );

        VoxelShape shapeTop = Shapes.or(
                box(   4,  4,    0,   12, 16,   16),
                box(15.5,  0,    0,   16, 16,   16),
                box(   0,  0,    0,   .5, 16,   16),
                box(  12, .5,    0, 15.5, 16,   16),
                box(  .5, .5,    0,    4, 16,   16),
                box(   0,  0, 15.5,   16, 16,   16),
                box(  .5, .5,   12, 15.5, 16, 15.5)
        );

        VoxelShape shapeRight = Shapes.or(
                box( 0,    4,  4, 16,   12, 16),
                box( 0,    0,  0, 16,   .5, 16),
                box( 0, 15.5,  0, 16,   16, 16),
                box( 0,   .5, .5, 16,    4, 16),
                box( 0,   12, .5, 16, 15.5, 16),
                box( 0,    0,  0, .5,   16, 16),
                box(.5,   .5, .5,  4, 15.5, 16)
        );

        VoxelShape shapeLeft = Shapes.or(
                box(0, 4, 4, 16, 12, 16),
                box(0, 15.5, 0, 16, 16, 16),
                box(0, 0, 0, 16, 0.5, 16),
                box(0, 12, 0.5, 16, 15.5, 16),
                box(0, 0.5, 0.5, 16, 4, 16),
                box(15.5, 0, 0, 16, 16, 16),
                box(12, 0.5, 0.5, 15.5, 15.5, 16)
        );

        VoxelShape shapeUp = Shapes.or(
                box(   4,  0,  4,   12, 16, 16),
                box(15.5,  0,  0,   16, 16, 16),
                box(   0,  0,  0,   .5, 16, 16),
                box(  12,  0, .5, 15.5, 16, 16),
                box(  .5,  0, .5,    4, 16, 16),
                box(   0,  0,  0,   16, .5, 16),
                box(  .5, .5, .5, 15.5,  4, 16)
        );

        VoxelShape shapeDown = Shapes.or(
                box(   4,    0,  4,   12,   16, 16),
                box(   0,    0,  0,   .5,   16, 16),
                box(15.5,    0,  0,   16,   16, 16),
                box(  .5,    0, .5,    4,   16, 16),
                box(  12,    0, .5, 15.5,   16, 16),
                box(   0, 15.5,  0,   16,   16, 16),
                box(  .5,   12, .5, 15.5, 15.5, 16)
        );

        for (BlockState state : states)
        {
            Direction facing = state.getValue(BlockStateProperties.FACING);
            Direction orientation = state.getValue(PropertyHolder.ORIENTATION);

            if (orientation.getAxis() == facing.getAxis())
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
                                orientation.getOpposite(),
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
