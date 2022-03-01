package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.*;
import net.minecraft.util.math.vector.Vector3d;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.*;

public class FramedCornerSlopeBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (type == CornerType.TOP)
        {
            return dir == Direction.UP;
        }
        else if (type == CornerType.BOTTOM)
        {
            return dir == Direction.DOWN;
        }
        return state.getValue(PropertyHolder.FACING_HOR) == dir;
    };

    public static final CtmPredicate CTM_PREDICATE_INNER = (state, dir) ->
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if ((type == CornerType.TOP || (type.isHorizontal() && type.isTop())) && dir == Direction.UP)
        {
            return true;
        }
        else if ((type == CornerType.BOTTOM || (type.isHorizontal() && !type.isTop())) && dir == Direction.DOWN)
        {
            return true;
        }

        Direction facing = state.getValue(PropertyHolder.FACING_HOR);
        if (type.isHorizontal())
        {
            return facing == dir || (type.isRight() && facing.getClockWise() == dir) || (!type.isRight() && facing.getCounterClockWise() == dir);
        }
        else
        {
            return facing == dir || facing.getClockWise() == dir;
        }
    };

    public FramedCornerSlopeBlock(BlockType type) { super(type); }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.CORNER_TYPE, BlockStateProperties.WATERLOGGED, PropertyHolder.SOLID);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = defaultBlockState();

        Direction side = context.getClickedFace();
        Vector3d hitPoint = Utils.fraction(context.getClickLocation());
        if (side.getAxis() != Direction.Axis.Y)
        {
            if (hitPoint.y() < (3D / 16D))
            {
                side = Direction.UP;
            }
            else if (hitPoint.y() > (13D / 16D))
            {
                side = Direction.DOWN;
            }
        }

        Direction facing = context.getHorizontalDirection();
        if (getBlockType() == BlockType.FRAMED_INNER_CORNER_SLOPE && side.getAxis() == Direction.Axis.Y)
        {
            facing = facing.getCounterClockWise();
        }
        state = state.setValue(PropertyHolder.FACING_HOR, facing);

        if (side == Direction.DOWN)
        {
            state = state.setValue(PropertyHolder.CORNER_TYPE, CornerType.TOP);
        }
        else if (side == Direction.UP)
        {
            state = state.setValue(PropertyHolder.CORNER_TYPE, CornerType.BOTTOM);
        }
        else
        {
            boolean xAxis = context.getClickedFace().getAxis() == Direction.Axis.X;
            boolean positive = context.getClickedFace().getCounterClockWise().getAxisDirection() == Direction.AxisDirection.POSITIVE;
            double xz = xAxis ? hitPoint.z() : hitPoint.x();
            double y = hitPoint.y();

            CornerType type;
            if ((xz > .5D) == positive)
            {
                type = (y > .5D) ? CornerType.HORIZONTAL_TOP_RIGHT : CornerType.HORIZONTAL_BOTTOM_RIGHT;
            }
            else
            {
                type = (y > .5D) ? CornerType.HORIZONTAL_TOP_LEFT : CornerType.HORIZONTAL_BOTTOM_LEFT;
            }
            state = state.setValue(PropertyHolder.CORNER_TYPE, type);
        }

        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    public static ImmutableMap<BlockState, VoxelShape> generateCornerShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeTop = VoxelShapes.join(
                FramedSlopeBlock.SHAPE_TOP,
                Utils.rotateShape(Direction.NORTH, Direction.WEST, FramedSlopeBlock.SHAPE_TOP),
                IBooleanFunction.AND
        );

        VoxelShape shapeBottom = VoxelShapes.join(
                FramedSlopeBlock.SHAPE_BOTTOM,
                Utils.rotateShape(Direction.NORTH, Direction.WEST, FramedSlopeBlock.SHAPE_BOTTOM),
                IBooleanFunction.AND
        );

        VoxelShape shapeBottomLeft = VoxelShapes.join(
                FramedSlopeBlock.SHAPE_BOTTOM,
                FramedSlopeBlock.SHAPE_HORIZONTAL,
                IBooleanFunction.AND
        );

        VoxelShape shapeBottomRight = VoxelShapes.join(
                FramedSlopeBlock.SHAPE_BOTTOM,
                Utils.rotateShape(Direction.NORTH, Direction.EAST, FramedSlopeBlock.SHAPE_HORIZONTAL),
                IBooleanFunction.AND
        );

        VoxelShape shapeTopLeft = VoxelShapes.join(
                FramedSlopeBlock.SHAPE_TOP,
                FramedSlopeBlock.SHAPE_HORIZONTAL,
                IBooleanFunction.AND
        );

        VoxelShape shapeTopRight = VoxelShapes.join(
                FramedSlopeBlock.SHAPE_TOP,
                Utils.rotateShape(Direction.NORTH, Direction.EAST, FramedSlopeBlock.SHAPE_HORIZONTAL),
                IBooleanFunction.AND
        );

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
            Direction dir = state.getValue(PropertyHolder.FACING_HOR);

            if (type.isHorizontal())
            {
                VoxelShape shape = VoxelShapes.block();
                switch (type)
                {
                    case HORIZONTAL_BOTTOM_LEFT:
                        shape = shapeBottomLeft;
                        break;
                    case HORIZONTAL_BOTTOM_RIGHT:
                        shape = shapeBottomRight;
                        break;
                    case HORIZONTAL_TOP_LEFT:
                        shape = shapeTopLeft;
                        break;
                    case HORIZONTAL_TOP_RIGHT:
                        shape = shapeTopRight;
                        break;
                }
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shape));
            }
            else
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, type.isTop() ? shapeTop : shapeBottom));
            }
        }

        return builder.build();
    }

    public static ImmutableMap<BlockState, VoxelShape> generateInnerCornerShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeTop = VoxelShapes.or(
                FramedSlopeBlock.SHAPE_TOP,
                Utils.rotateShape(Direction.NORTH, Direction.WEST, FramedSlopeBlock.SHAPE_TOP)
        );

        VoxelShape shapeBottom = VoxelShapes.or(
                FramedSlopeBlock.SHAPE_BOTTOM,
                Utils.rotateShape(Direction.NORTH, Direction.WEST, FramedSlopeBlock.SHAPE_BOTTOM)
        );

        VoxelShape shapeBottomLeft = VoxelShapes.or(
                FramedSlopeBlock.SHAPE_BOTTOM,
                FramedSlopeBlock.SHAPE_HORIZONTAL
        );

        VoxelShape shapeBottomRight = VoxelShapes.or(
                FramedSlopeBlock.SHAPE_BOTTOM,
                Utils.rotateShape(Direction.NORTH, Direction.EAST, FramedSlopeBlock.SHAPE_HORIZONTAL)
        );

        VoxelShape shapeTopLeft = VoxelShapes.or(
                FramedSlopeBlock.SHAPE_TOP,
                FramedSlopeBlock.SHAPE_HORIZONTAL
        );

        VoxelShape shapeTopRight = VoxelShapes.or(
                FramedSlopeBlock.SHAPE_TOP,
                Utils.rotateShape(Direction.NORTH, Direction.EAST, FramedSlopeBlock.SHAPE_HORIZONTAL)
        );

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
            Direction dir = state.getValue(PropertyHolder.FACING_HOR);

            if (type.isHorizontal())
            {
                VoxelShape shape = VoxelShapes.block();
                switch (type)
                {
                    case HORIZONTAL_BOTTOM_LEFT:
                        shape = shapeBottomLeft;
                        break;
                    case HORIZONTAL_BOTTOM_RIGHT:
                        shape = shapeBottomRight;
                        break;
                    case HORIZONTAL_TOP_LEFT:
                        shape = shapeTopLeft;
                        break;
                    case HORIZONTAL_TOP_RIGHT:
                        shape = shapeTopRight;
                        break;
                }
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shape));
            }
            else
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, type.isTop() ? shapeTop : shapeBottom));
            }
        }

        return builder.build();
    }
}