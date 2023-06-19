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
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.CornerType;

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
        VoxelShape shapeTop = Shapes.join(
                FramedSlopeBlock.SHAPE_TOP,
                Utils.rotateShape(Direction.NORTH, Direction.WEST, FramedSlopeBlock.SHAPE_TOP),
                BooleanOp.AND
        );

        VoxelShape shapeBottom = Shapes.join(
                FramedSlopeBlock.SHAPE_BOTTOM,
                Utils.rotateShape(Direction.NORTH, Direction.WEST, FramedSlopeBlock.SHAPE_BOTTOM),
                BooleanOp.AND
        );

        VoxelShape shapeBottomLeft = Shapes.join(
                FramedSlopeBlock.SHAPE_BOTTOM,
                FramedSlopeBlock.SHAPE_HORIZONTAL,
                BooleanOp.AND
        );

        VoxelShape shapeBottomRight = Shapes.join(
                FramedSlopeBlock.SHAPE_BOTTOM,
                Utils.rotateShape(Direction.NORTH, Direction.EAST, FramedSlopeBlock.SHAPE_HORIZONTAL),
                BooleanOp.AND
        );

        VoxelShape shapeTopLeft = Shapes.join(
                FramedSlopeBlock.SHAPE_TOP,
                FramedSlopeBlock.SHAPE_HORIZONTAL,
                BooleanOp.AND
        );

        VoxelShape shapeTopRight = Shapes.join(
                FramedSlopeBlock.SHAPE_TOP,
                Utils.rotateShape(Direction.NORTH, Direction.EAST, FramedSlopeBlock.SHAPE_HORIZONTAL),
                BooleanOp.AND
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
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shape));
            }
            else
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, type.isTop() ? shapeTop : shapeBottom));
            }
        }

        return ShapeProvider.of(builder.build());
    }

    public static ShapeProvider generateInnerCornerShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeTop = Shapes.or(
                FramedSlopeBlock.SHAPE_TOP,
                Utils.rotateShape(Direction.NORTH, Direction.WEST, FramedSlopeBlock.SHAPE_TOP)
        );

        VoxelShape shapeBottom = Shapes.or(
                FramedSlopeBlock.SHAPE_BOTTOM,
                Utils.rotateShape(Direction.NORTH, Direction.WEST, FramedSlopeBlock.SHAPE_BOTTOM)
        );

        VoxelShape shapeBottomLeft = Shapes.or(
                FramedSlopeBlock.SHAPE_BOTTOM,
                FramedSlopeBlock.SHAPE_HORIZONTAL
        );

        VoxelShape shapeBottomRight = Shapes.or(
                FramedSlopeBlock.SHAPE_BOTTOM,
                Utils.rotateShape(Direction.NORTH, Direction.EAST, FramedSlopeBlock.SHAPE_HORIZONTAL)
        );

        VoxelShape shapeTopLeft = Shapes.or(
                FramedSlopeBlock.SHAPE_TOP,
                FramedSlopeBlock.SHAPE_HORIZONTAL
        );

        VoxelShape shapeTopRight = Shapes.or(
                FramedSlopeBlock.SHAPE_TOP,
                Utils.rotateShape(Direction.NORTH, Direction.EAST, FramedSlopeBlock.SHAPE_HORIZONTAL)
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
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shape));
            }
            else
            {
                builder.put(state, Utils.rotateShape(Direction.NORTH, dir, type.isTop() ? shapeTop : shapeBottom));
            }
        }

        return ShapeProvider.of(builder.build());
    }
}