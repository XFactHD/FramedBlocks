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
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.CtmPredicate;
import xfacthd.framedblocks.common.util.Utils;

/*
FIXME: BREAKING CHANGE!!!
FIXME: Fix inner corner top/bottom rotation discrepancy from other corners (should be rotated 90 degree clockwise)
*/
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.CORNER_TYPE, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState();

        Direction side = context.getClickedFace();
        Vec3 hitPoint = Utils.fraction(context.getClickLocation());
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
        state = withCornerType(state, context, side, hitPoint, facing);

        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    public static ImmutableMap<BlockState, VoxelShape> generateCornerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
            Direction dir = state.getValue(PropertyHolder.FACING_HOR);

            if (type.isHorizontal())
            {
                VoxelShape shapeBottomLeft = Shapes.or(
                        box(0, 0, 0,  4,  4, 16),
                        box(0, 0, 0,  8,  8, 12),
                        box(0, 0, 0, 12, 12,  8),
                        box(0, 0, 0, 16, 16,  4)
                ).optimize();

                VoxelShape shapeBottomRight = Shapes.or(
                        box( 0, 0, 0, 16, 16,  4),
                        box( 4, 0, 0, 16, 12,  8),
                        box( 8, 0, 0, 16,  8, 12),
                        box(12, 0, 0, 16,  4, 16)
                ).optimize();

                VoxelShape shapeTopLeft = Shapes.or(
                        box(0,  0, 0, 16, 16,  4),
                        box(0,  4, 0, 12, 16,  8),
                        box(0,  8, 0,  8, 16, 12),
                        box(0, 12, 0,  4, 16, 16)
                ).optimize();

                VoxelShape shapeTopRight = Shapes.or(
                        box( 0,  0, 0, 16, 16,  4),
                        box( 4,  4, 0, 16, 16,  8),
                        box( 8,  8, 0, 16, 16, 12),
                        box(12, 12, 0, 16, 16, 16)
                ).optimize();

                VoxelShape shape = switch (type)
                {
                    case HORIZONTAL_BOTTOM_LEFT -> shapeBottomLeft;
                    case HORIZONTAL_BOTTOM_RIGHT -> shapeBottomRight;
                    case HORIZONTAL_TOP_LEFT -> shapeTopLeft;
                    case HORIZONTAL_TOP_RIGHT -> shapeTopRight;
                    default -> Shapes.block();
                };
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shape));
            }
            else if (type.isTop())
            {
                VoxelShape shapeTop = Shapes.or(
                        box(0,  0, 0,  4,  4,  4),
                        box(0,  4, 0,  8,  8,  8),
                        box(0,  8, 0, 12, 12, 12),
                        box(0, 12, 0, 16, 16, 16)
                ).optimize();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                VoxelShape shapeBottom = Shapes.or(
                        box(0,  0, 0, 16,  4, 16),
                        box(0,  4, 0, 12,  8, 12),
                        box(0,  8, 0,  8, 12,  8),
                        box(0, 12, 0,  4, 16,  4)
                ).optimize();

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
            CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
            Direction dir = state.getValue(PropertyHolder.FACING_HOR);

            if (type.isHorizontal())
            {
                VoxelShape shapeBottomLeft = Shapes.or(
                        box(0, 0, 0, 16, 16, 4),
                        box(0, 0, 4, 16, 12, 8),
                        box(0, 0, 8, 16, 8, 12),
                        box(0, 0, 12, 16, 4, 16),
                        box(0, 8, 8, 8, 16, 12),
                        box(0, 4, 12, 4, 16, 16),
                        box(0, 12, 4, 12, 16, 8)
                ).optimize();

                VoxelShape shapeBottomRight = Shapes.or(
                        box(0, 0, 0, 16, 16, 4),
                        box(0, 0, 4, 16, 12, 8),
                        box(0, 0, 8, 16, 8, 12),
                        box(0, 0, 12, 16, 4, 16),
                        box(8, 8, 8, 16, 16, 12),
                        box(12, 4, 12, 16, 16, 16),
                        box(4, 12, 4, 16, 16, 8)
                ).optimize();

                VoxelShape shapeTopLeft = Shapes.or(
                        box(0, 0, 0, 16, 16, 4),
                        box(0, 4, 4, 16, 16, 8),
                        box(0, 8, 8, 16, 16, 12),
                        box(0, 12, 12, 16, 16, 16),
                        box(0, 0, 8, 8, 8, 12),
                        box(0, 0, 12, 4, 12, 16),
                        box(0, 0, 4, 12, 4, 8)
                ).optimize();

                VoxelShape shapeTopRight = Shapes.or(
                        box(0, 0, 0, 16, 16, 4),
                        box(0, 4, 4, 16, 16, 8),
                        box(0, 8, 8, 16, 16, 12),
                        box(0, 12, 12, 16, 16, 16),
                        box(8, 0, 8, 16, 8, 12),
                        box(12, 0, 12, 16, 12, 16),
                        box(4, 0, 4, 16, 4, 8)
                ).optimize();

                VoxelShape shape = switch (type)
                {
                    case HORIZONTAL_BOTTOM_LEFT -> shapeBottomLeft;
                    case HORIZONTAL_BOTTOM_RIGHT -> shapeBottomRight;
                    case HORIZONTAL_TOP_LEFT -> shapeTopLeft;
                    case HORIZONTAL_TOP_RIGHT -> shapeTopRight;
                    default -> Shapes.block();
                };
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shape));
            }
            else if (type.isTop())
            {
                VoxelShape shapeTop = Shapes.or(
                        box( 0,  0,  0, 16,  4,  4),
                        box(12,  0,  4, 16,  4, 16),
                        box( 0,  4,  0, 16,  8,  8),
                        box( 8,  4,  8, 16,  8, 16),
                        box( 0,  8,  0, 16, 12, 12),
                        box( 4,  8, 12, 16, 12, 16),
                        box( 0, 12,  0, 16, 16, 16)
                ).optimize();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeTop));
            }
            else
            {
                VoxelShape shapeBottom = Shapes.or(
                        box( 0,  0,  0, 16,  4, 16),
                        box( 0,  4,  0, 16,  8, 12),
                        box( 4,  4, 12, 16,  8, 16),
                        box( 0,  8,  0, 16, 12,  8),
                        box( 8,  8,  8, 16, 12, 16),
                        box( 0, 12,  0, 16, 16,  4),
                        box(12, 12,  4, 16, 16, 16)
                ).optimize();

                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shapeBottom));
            }
        }

        return builder.build();
    }
}