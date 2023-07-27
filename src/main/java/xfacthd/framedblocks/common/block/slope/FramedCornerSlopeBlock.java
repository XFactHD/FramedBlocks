package xfacthd.framedblocks.common.block.slope;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.CornerType;
import xfacthd.framedblocks.common.data.property.SlopeType;

public class FramedCornerSlopeBlock extends FramedBlock
{
    public FramedCornerSlopeBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.CORNER_TYPE, BlockStateProperties.WATERLOGGED,
                FramedProperties.SOLID, FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState();

        Direction side = context.getClickedFace();
        Direction typeSide = side;
        Vec3 hitPoint = Utils.fraction(context.getClickLocation());
        if (!Utils.isY(side))
        {
            if (hitPoint.y() < (3D / 16D))
            {
                typeSide = Direction.UP;
            }
            else if (hitPoint.y() > (13D / 16D))
            {
                typeSide = Direction.DOWN;
            }
        }

        Direction facing = context.getHorizontalDirection();
        state = withCornerType(state, context, side, typeSide, hitPoint, facing);

        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, Direction side, Rotation rot)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (type.isHorizontal())
        {
            return state.setValue(PropertyHolder.CORNER_TYPE, type.rotate(rot));
        }

        return rotate(state, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = rot.rotate(state.getValue(FramedProperties.FACING_HOR));
        return state.setValue(FramedProperties.FACING_HOR, dir);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (type.isHorizontal())
        {
            BlockState newState = Utils.mirrorFaceBlock(state, mirror);
            if (newState != state)
            {
                return newState.setValue(PropertyHolder.CORNER_TYPE, type.horizontalOpposite());
            }
            return state;
        }
        else
        {
            return Utils.mirrorCornerBlock(state, mirror);
        }
    }



    public static ShapeProvider generateCornerShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeSlopeBottom = FramedSlopeBlock.SHAPES.get(SlopeType.BOTTOM);
        VoxelShape shapeSlopeTop = FramedSlopeBlock.SHAPES.get(SlopeType.TOP);
        VoxelShape shapeSlopeHorizontal = FramedSlopeBlock.SHAPES.get(SlopeType.HORIZONTAL);

        VoxelShape shapeTop = ShapeUtils.andUnoptimized(
                shapeSlopeTop,
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, shapeSlopeTop)
        );

        VoxelShape shapeBottom = ShapeUtils.andUnoptimized(
                shapeSlopeBottom,
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, shapeSlopeBottom)
        );

        VoxelShape shapeBottomLeft = ShapeUtils.andUnoptimized(shapeSlopeBottom, shapeSlopeHorizontal);

        VoxelShape shapeBottomRight = ShapeUtils.andUnoptimized(
                shapeSlopeBottom,
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.EAST, shapeSlopeHorizontal)
        );

        VoxelShape shapeTopLeft = ShapeUtils.andUnoptimized(shapeSlopeTop, shapeSlopeHorizontal);

        VoxelShape shapeTopRight = ShapeUtils.andUnoptimized(
                shapeSlopeTop,
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.EAST, shapeSlopeHorizontal)
        );

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
            Direction dir = state.getValue(FramedProperties.FACING_HOR);

            if (type.isHorizontal())
            {
                VoxelShape shape = switch (type)
                {
                    case HORIZONTAL_BOTTOM_LEFT -> shapeBottomLeft;
                    case HORIZONTAL_BOTTOM_RIGHT -> shapeBottomRight;
                    case HORIZONTAL_TOP_LEFT -> shapeTopLeft;
                    case HORIZONTAL_TOP_RIGHT -> shapeTopRight;
                    default -> Shapes.block();
                };
                builder.put(state, ShapeUtils.rotateShape(Direction.NORTH, dir, shape));
            }
            else
            {
                builder.put(state, ShapeUtils.rotateShape(Direction.NORTH, dir, type.isTop() ? shapeTop : shapeBottom));
            }
        }

        return ShapeProvider.of(builder.build());
    }

    public static ShapeProvider generateInnerCornerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeSlopeBottom = FramedSlopeBlock.SHAPES.get(SlopeType.BOTTOM);
        VoxelShape shapeSlopeTop = FramedSlopeBlock.SHAPES.get(SlopeType.TOP);
        VoxelShape shapeSlopeHorizontal = FramedSlopeBlock.SHAPES.get(SlopeType.HORIZONTAL);

        VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                shapeSlopeTop,
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, shapeSlopeTop)
        );

        VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                shapeSlopeBottom,
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, shapeSlopeBottom)
        );

        VoxelShape shapeBottomLeft = ShapeUtils.orUnoptimized(shapeSlopeBottom, shapeSlopeHorizontal);

        VoxelShape shapeBottomRight = ShapeUtils.orUnoptimized(
                shapeSlopeBottom,
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.EAST, shapeSlopeHorizontal)
        );

        VoxelShape shapeTopLeft = ShapeUtils.orUnoptimized(shapeSlopeTop, shapeSlopeHorizontal);

        VoxelShape shapeTopRight = ShapeUtils.orUnoptimized(
                shapeSlopeTop,
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.EAST, shapeSlopeHorizontal)
        );

        VoxelShape[] shapes = new VoxelShape[4 * 6];
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            for (CornerType type : CornerType.values())
            {
                int idx = dir.get2DDataValue() | (type.ordinal() << 2);
                if (type.isHorizontal())
                {
                    VoxelShape shape = switch (type)
                    {
                        case HORIZONTAL_BOTTOM_LEFT -> shapeBottomLeft;
                        case HORIZONTAL_BOTTOM_RIGHT -> shapeBottomRight;
                        case HORIZONTAL_TOP_LEFT -> shapeTopLeft;
                        case HORIZONTAL_TOP_RIGHT -> shapeTopRight;
                        default -> Shapes.block();
                    };
                    shapes[idx] = ShapeUtils.rotateShape(Direction.NORTH, dir, shape);
                }
                else
                {
                    shapes[idx] = ShapeUtils.rotateShape(Direction.NORTH, dir, type.isTop() ? shapeTop : shapeBottom);
                }
            }
        }

        for (BlockState state : states)
        {
            CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            int idx = dir.get2DDataValue() | (type.ordinal() << 2);
            builder.put(state, shapes[idx]);
        }

        return ShapeProvider.of(builder.build());
    }
}