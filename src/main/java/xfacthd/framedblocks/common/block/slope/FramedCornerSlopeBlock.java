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
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.ExtPlacementStateBuilder;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.CornerType;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

import java.util.HashMap;
import java.util.Map;

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
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return ExtPlacementStateBuilder.of(this, ctx)
                .withHorizontalFacingAndCornerType()
                .withWater()
                .build();
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
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = rot.rotate(state.getValue(FramedProperties.FACING_HOR));
        return state.setValue(FramedProperties.FACING_HOR, dir);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror)
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

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }



    private record ShapeKey(Direction dir, CornerType type) { }

    public static final class CornerShapeGen implements SplitShapeGenerator
    {
        @Override
        public ShapeProvider generate(ImmutableList<BlockState> states)
        {
            return generateShapes(states, FramedSlopeBlock.SHAPES);
        }

        @Override
        public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
        {
            return generateShapes(states, FramedSlopeBlock.OCCLUSION_SHAPES);
        }

        private static ShapeProvider generateShapes(ImmutableList<BlockState> states, ShapeCache<SlopeType> shapeCache)
        {
            VoxelShape shapeSlopeBottom = shapeCache.get(SlopeType.BOTTOM);
            VoxelShape shapeSlopeTop = shapeCache.get(SlopeType.TOP);
            VoxelShape shapeSlopeHorizontal = shapeCache.get(SlopeType.HORIZONTAL);
            VoxelShape shapeSlopeHorizontalEast = ShapeUtils.rotateShapeUnoptimizedAroundY(
                    Direction.NORTH, Direction.EAST, shapeSlopeHorizontal
            );

            VoxelShape shapeTop = ShapeUtils.andUnoptimized(
                    shapeSlopeTop,
                    ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, shapeSlopeTop)
            );
            VoxelShape shapeBottom = ShapeUtils.andUnoptimized(
                    shapeSlopeBottom,
                    ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, shapeSlopeBottom)
            );

            VoxelShape shapeBottomLeft = ShapeUtils.andUnoptimized(shapeSlopeBottom, shapeSlopeHorizontal);
            VoxelShape shapeBottomRight = ShapeUtils.andUnoptimized(shapeSlopeBottom, shapeSlopeHorizontalEast);
            VoxelShape shapeTopLeft = ShapeUtils.andUnoptimized(shapeSlopeTop, shapeSlopeHorizontal);
            VoxelShape shapeTopRight = ShapeUtils.andUnoptimized(shapeSlopeTop, shapeSlopeHorizontalEast);

            Map<ShapeKey, VoxelShape> shapes = new HashMap<>();
            for (CornerType type : CornerType.values())
            {
                VoxelShape shape = switch (type)
                {
                    case BOTTOM -> shapeBottom;
                    case TOP -> shapeTop;
                    case HORIZONTAL_BOTTOM_LEFT -> shapeBottomLeft;
                    case HORIZONTAL_BOTTOM_RIGHT -> shapeBottomRight;
                    case HORIZONTAL_TOP_LEFT -> shapeTopLeft;
                    case HORIZONTAL_TOP_RIGHT -> shapeTopRight;
                };
                ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, shapes, type, ShapeKey::new);
            }

            ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

            for (BlockState state : states)
            {
                CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
                Direction dir = state.getValue(FramedProperties.FACING_HOR);
                builder.put(state, shapes.get(new ShapeKey(dir, type)));
            }

            return ShapeProvider.of(builder.build());
        }
    }

    public static final class InnerCornerShapeGen implements SplitShapeGenerator
    {
        @Override
        public ShapeProvider generate(ImmutableList<BlockState> states)
        {
            return generateShapes(states, FramedSlopeBlock.SHAPES);
        }

        @Override
        public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
        {
            return generateShapes(states, FramedSlopeBlock.OCCLUSION_SHAPES);
        }

        private static ShapeProvider generateShapes(ImmutableList<BlockState> states, ShapeCache<SlopeType> shapeCache)
        {
            ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

            VoxelShape shapeSlopeBottom = shapeCache.get(SlopeType.BOTTOM);
            VoxelShape shapeSlopeTop = shapeCache.get(SlopeType.TOP);
            VoxelShape shapeSlopeHorizontal = shapeCache.get(SlopeType.HORIZONTAL);
            VoxelShape shapeSlopeHorizontalEast = ShapeUtils.rotateShapeUnoptimizedAroundY(
                    Direction.NORTH, Direction.EAST, shapeSlopeHorizontal
            );

            VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                    shapeSlopeTop,
                    ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, shapeSlopeTop)
            );
            VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                    shapeSlopeBottom,
                    ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.WEST, shapeSlopeBottom)
            );

            VoxelShape shapeBottomLeft = ShapeUtils.orUnoptimized(shapeSlopeBottom, shapeSlopeHorizontal);
            VoxelShape shapeBottomRight = ShapeUtils.orUnoptimized(shapeSlopeBottom, shapeSlopeHorizontalEast);
            VoxelShape shapeTopLeft = ShapeUtils.orUnoptimized(shapeSlopeTop, shapeSlopeHorizontal);
            VoxelShape shapeTopRight = ShapeUtils.orUnoptimized(shapeSlopeTop, shapeSlopeHorizontalEast);

            Map<ShapeKey, VoxelShape> shapes = new HashMap<>();
            for (CornerType type : CornerType.values())
            {
                VoxelShape shape = switch (type)
                {
                    case BOTTOM -> shapeBottom;
                    case TOP -> shapeTop;
                    case HORIZONTAL_BOTTOM_LEFT -> shapeBottomLeft;
                    case HORIZONTAL_BOTTOM_RIGHT -> shapeBottomRight;
                    case HORIZONTAL_TOP_LEFT -> shapeTopLeft;
                    case HORIZONTAL_TOP_RIGHT -> shapeTopRight;
                };
                ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, shapes, type, ShapeKey::new);
            }

            for (BlockState state : states)
            {
                CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
                Direction dir = state.getValue(FramedProperties.FACING_HOR);
                builder.put(state, shapes.get(new ShapeKey(dir, type)));
            }

            return ShapeProvider.of(builder.build());
        }
    }
}
