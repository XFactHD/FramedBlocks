package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.*;

public class FramedCornerSlopeBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        CornerType type = state.get(PropertyHolder.CORNER_TYPE);
        if (type == CornerType.TOP)
        {
            return dir == Direction.UP;
        }
        else if (type == CornerType.BOTTOM)
        {
            return dir == Direction.DOWN;
        }
        return state.get(PropertyHolder.FACING_HOR) == dir;
    };

    public static final CtmPredicate CTM_PREDICATE_INNER = (state, dir) ->
    {
        CornerType type = state.get(PropertyHolder.CORNER_TYPE);
        if ((type == CornerType.TOP || (type.isHorizontal() && type.isTop())) && dir == Direction.UP)
        {
            return true;
        }
        else if ((type == CornerType.BOTTOM || (type.isHorizontal() && !type.isTop())) && dir == Direction.DOWN)
        {
            return true;
        }

        Direction facing = state.get(PropertyHolder.FACING_HOR);
        if (type.isHorizontal())
        {
            return facing == dir || (type.isRight() && facing.rotateY() == dir) || (!type.isRight() && facing.rotateYCCW() == dir);
        }
        else
        {
            return facing == dir || facing.rotateY() == dir;
        }
    };

    public FramedCornerSlopeBlock(BlockType type) { super(type); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.CORNER_TYPE, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = getDefaultState();

        Direction side = context.getFace();
        Vector3d hitPoint = Utils.fraction(context.getHitVec());
        if (side.getAxis() != Direction.Axis.Y)
        {
            if (hitPoint.getY() < (3D / 16D))
            {
                side = Direction.UP;
            }
            else if (hitPoint.getY() > (13D / 16D))
            {
                side = Direction.DOWN;
            }
        }

        Direction facing = context.getPlacementHorizontalFacing();
        if (getBlockType() == BlockType.FRAMED_INNER_CORNER_SLOPE && side.getAxis() == Direction.Axis.Y)
        {
            facing = facing.rotateYCCW();
        }
        state = state.with(PropertyHolder.FACING_HOR, facing);

        if (side == Direction.DOWN)
        {
            state = state.with(PropertyHolder.CORNER_TYPE, CornerType.TOP);
        }
        else if (side == Direction.UP)
        {
            state = state.with(PropertyHolder.CORNER_TYPE, CornerType.BOTTOM);
        }
        else
        {
            boolean xAxis = context.getFace().getAxis() == Direction.Axis.X;
            boolean positive = context.getFace().rotateYCCW().getAxisDirection() == Direction.AxisDirection.POSITIVE;
            double xz = xAxis ? hitPoint.getZ() : hitPoint.getX();
            double y = hitPoint.getY();

            CornerType type;
            if ((xz > .5D) == positive)
            {
                type = (y > .5D) ? CornerType.HORIZONTAL_TOP_RIGHT : CornerType.HORIZONTAL_BOTTOM_RIGHT;
            }
            else
            {
                type = (y > .5D) ? CornerType.HORIZONTAL_TOP_LEFT : CornerType.HORIZONTAL_BOTTOM_LEFT;
            }
            state = state.with(PropertyHolder.CORNER_TYPE, type);
        }

        return withWater(state, context.getWorld(), context.getPos());
    }

    public static ImmutableMap<BlockState, VoxelShape> generateCornerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            CornerType type = state.get(PropertyHolder.CORNER_TYPE);
            Direction dir = state.get(PropertyHolder.FACING_HOR);

            if (type.isHorizontal())
            {
                VoxelShape shapeBottomLeft = VoxelShapes.or(
                        makeCuboidShape(0, 0, 16,  4,  4, 0),
                        makeCuboidShape(0, 0, 12,  8,  8, 0),
                        makeCuboidShape(0, 0,  8, 12, 12, 0),
                        makeCuboidShape(0, 0,  4, 16, 16, 0)
                ).simplify();

                VoxelShape shapeBottomRight = VoxelShapes.or(
                        makeCuboidShape( 0, 0, 0, 16, 16,  4),
                        makeCuboidShape( 4, 0, 0, 16, 12,  8),
                        makeCuboidShape( 8, 0, 0, 16,  8, 12),
                        makeCuboidShape(12, 0, 0, 16,  4, 16)
                ).simplify();

                VoxelShape shapeTopLeft = VoxelShapes.or(
                        makeCuboidShape(0,  0, 0, 16, 16,  4),
                        makeCuboidShape(0,  4, 0, 12, 16,  8),
                        makeCuboidShape(0,  8, 0,  8, 16, 12),
                        makeCuboidShape(0, 12, 0,  4, 16, 16)
                ).simplify();

                VoxelShape shapeTopRight = VoxelShapes.or(
                        makeCuboidShape( 0,  0, 0, 16, 16,  4),
                        makeCuboidShape( 4,  4, 0, 16, 16,  8),
                        makeCuboidShape( 8,  8, 0, 16, 16, 12),
                        makeCuboidShape(12, 12, 0, 16, 16, 16)
                ).simplify();

                VoxelShape shape = VoxelShapes.fullCube();
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
            else if (type.isTop())
            {
                VoxelShape shapeTop = VoxelShapes.or(
                        makeCuboidShape(0,  0, 0,  4,  4,  4),
                        makeCuboidShape(0,  4, 0,  8,  8,  8),
                        makeCuboidShape(0,  8, 0, 12, 12, 12),
                        makeCuboidShape(0, 12, 0, 16, 16, 16)
                ).simplify();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                VoxelShape shapeBottom = VoxelShapes.or(
                        makeCuboidShape(0,  0, 0, 16,  4, 16),
                        makeCuboidShape(0,  4, 0, 12,  8, 12),
                        makeCuboidShape(0,  8, 0,  8, 12,  8),
                        makeCuboidShape(0, 12, 0,  4, 16,  4)
                ).simplify();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeBottom));
            }
        }

        return builder.build();
    }

    public static ImmutableMap<BlockState, VoxelShape> generateInnerCornerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            CornerType type = state.get(PropertyHolder.CORNER_TYPE);
            Direction dir = state.get(PropertyHolder.FACING_HOR);

            if (type.isHorizontal())
            {
                VoxelShape shapeBottomLeft = VoxelShapes.or(
                        makeCuboidShape(0, 0, 0, 16, 16, 4),
                        makeCuboidShape(0, 0, 4, 16, 12, 8),
                        makeCuboidShape(0, 0, 8, 16, 8, 12),
                        makeCuboidShape(0, 0, 12, 16, 4, 16),
                        makeCuboidShape(0, 8, 8, 8, 16, 12),
                        makeCuboidShape(0, 4, 12, 4, 16, 16),
                        makeCuboidShape(0, 12, 4, 12, 16, 8)
                ).simplify();

                VoxelShape shapeBottomRight = VoxelShapes.or(
                        makeCuboidShape(0, 0, 0, 16, 16, 4),
                        makeCuboidShape(0, 0, 4, 16, 12, 8),
                        makeCuboidShape(0, 0, 8, 16, 8, 12),
                        makeCuboidShape(0, 0, 12, 16, 4, 16),
                        makeCuboidShape(8, 8, 8, 16, 16, 12),
                        makeCuboidShape(12, 4, 12, 16, 16, 16),
                        makeCuboidShape(4, 12, 4, 16, 16, 8)
                ).simplify();

                VoxelShape shapeTopLeft = VoxelShapes.or(
                        makeCuboidShape(0, 0, 0, 16, 16, 4),
                        makeCuboidShape(0, 4, 4, 16, 16, 8),
                        makeCuboidShape(0, 8, 8, 16, 16, 12),
                        makeCuboidShape(0, 12, 12, 16, 16, 16),
                        makeCuboidShape(0, 0, 8, 8, 8, 12),
                        makeCuboidShape(0, 0, 12, 4, 12, 16),
                        makeCuboidShape(0, 0, 4, 12, 4, 8)
                ).simplify();

                VoxelShape shapeTopRight = VoxelShapes.or(
                        makeCuboidShape(0, 0, 0, 16, 16, 4),
                        makeCuboidShape(0, 4, 4, 16, 16, 8),
                        makeCuboidShape(0, 8, 8, 16, 16, 12),
                        makeCuboidShape(0, 12, 12, 16, 16, 16),
                        makeCuboidShape(8, 0, 8, 16, 8, 12),
                        makeCuboidShape(12, 0, 12, 16, 12, 16),
                        makeCuboidShape(4, 0, 4, 16, 4, 8)
                ).simplify();

                VoxelShape shape = VoxelShapes.fullCube();
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
            else if (type.isTop())
            {
                VoxelShape shapeTop = VoxelShapes.or(
                        makeCuboidShape( 0,  0,  0, 16,  4,  4),
                        makeCuboidShape(12,  0,  4, 16,  4, 16),
                        makeCuboidShape( 0,  4,  0, 16,  8,  8),
                        makeCuboidShape( 8,  4,  8, 16,  8, 16),
                        makeCuboidShape( 0,  8,  0, 16, 12, 12),
                        makeCuboidShape( 4,  8, 12, 16, 12, 16),
                        makeCuboidShape( 0, 12,  0, 16, 16, 16)
                ).simplify();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                VoxelShape shapeBottom = VoxelShapes.or(
                        makeCuboidShape( 0,  0,  0, 16,  4, 16),
                        makeCuboidShape( 0,  4,  0, 16,  8, 12),
                        makeCuboidShape( 4,  4, 12, 16,  8, 16),
                        makeCuboidShape( 0,  8,  0, 16, 12,  8),
                        makeCuboidShape( 8,  8,  8, 16, 12, 16),
                        makeCuboidShape( 0, 12,  0, 16, 16,  4),
                        makeCuboidShape(12, 12,  4, 16, 16, 16)
                ).simplify();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeBottom));
            }
        }

        return builder.build();
    }
}