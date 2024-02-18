package xfacthd.framedblocks.common.block.slopeslab;

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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedFlatSlopeSlabCornerBlock extends FramedBlock
{
    public FramedFlatSlopeSlabCornerBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(PropertyHolder.TOP_HALF, false)
                .setValue(FramedProperties.Y_SLOPE, true)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, FramedProperties.TOP, PropertyHolder.TOP_HALF, FramedProperties.SOLID,
                BlockStateProperties.WATERLOGGED, FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withHalfFacing()
                .withTop(PropertyHolder.TOP_HALF)
                .withCustom((state, modCtx) ->
                        state.setValue(FramedProperties.TOP, ctx.getPlayer() != null && ctx.getPlayer().isShiftKeyDown())
                )
                .withWater()
                .build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
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
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, Direction.UP, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorCornerBlock(state, mirror);
    }



    public static final ShapeCache<SlopeSlabShape> SHAPES = ShapeCache.createEnum(SlopeSlabShape.class, map ->
    {
        VoxelShape shapeSlopeBottom = FramedSlopeSlabBlock.SHAPES.get(SlopeSlabShape.BOTTOM_BOTTOM_HALF);
        VoxelShape shapeSlopeTop = FramedSlopeSlabBlock.SHAPES.get(SlopeSlabShape.TOP_BOTTOM_HALF);

        VoxelShape shapeBottomBottomHalf = ShapeUtils.andUnoptimized(
                shapeSlopeBottom,
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, shapeSlopeBottom)
        );
        map.put(SlopeSlabShape.BOTTOM_BOTTOM_HALF, shapeBottomBottomHalf);
        map.put(SlopeSlabShape.BOTTOM_TOP_HALF, shapeBottomBottomHalf.move(0, .5, 0));

        VoxelShape shapeTopBottomHalf = ShapeUtils.andUnoptimized(
                shapeSlopeTop,
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, shapeSlopeTop)
        );
        map.put(SlopeSlabShape.TOP_BOTTOM_HALF, shapeTopBottomHalf);
        map.put(SlopeSlabShape.TOP_TOP_HALF, shapeTopBottomHalf.move(0, .5, 0));
    });

    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        int maskTop = 0b0100;
        int maskTopHalf = 0b1000;
        VoxelShape[] shapes = new VoxelShape[16];
        ShapeUtils.makeHorizontalRotations(SHAPES.get(SlopeSlabShape.BOTTOM_BOTTOM_HALF), Direction.NORTH, shapes, 0);
        ShapeUtils.makeHorizontalRotations(SHAPES.get(SlopeSlabShape.BOTTOM_TOP_HALF), Direction.NORTH, shapes, maskTopHalf);
        ShapeUtils.makeHorizontalRotations(SHAPES.get(SlopeSlabShape.TOP_BOTTOM_HALF), Direction.NORTH, shapes, maskTop);
        ShapeUtils.makeHorizontalRotations(SHAPES.get(SlopeSlabShape.TOP_TOP_HALF), Direction.NORTH, shapes, maskTop | maskTopHalf);

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            int top = state.getValue(FramedProperties.TOP) ? maskTop : 0;
            int topHalf = state.getValue(PropertyHolder.TOP_HALF) ? maskTopHalf : 0;
            int idx = dir.get2DDataValue() | top | topHalf;
            builder.put(state, shapes[idx]);
        }

        return ShapeProvider.of(builder.build());
    }

    public static final ShapeCache<SlopeSlabShape> INNER_SHAPES = ShapeCache.createEnum(SlopeSlabShape.class, map ->
    {
        VoxelShape shapeSlopeBottom = FramedSlopeSlabBlock.SHAPES.get(SlopeSlabShape.BOTTOM_BOTTOM_HALF);
        VoxelShape shapeSlopeTop = FramedSlopeSlabBlock.SHAPES.get(SlopeSlabShape.TOP_BOTTOM_HALF);

        VoxelShape shapeBottomBottomHalf = ShapeUtils.orUnoptimized(
                shapeSlopeBottom,
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, shapeSlopeBottom)
        );
        map.put(SlopeSlabShape.BOTTOM_BOTTOM_HALF, shapeBottomBottomHalf);
        map.put(SlopeSlabShape.BOTTOM_TOP_HALF, shapeBottomBottomHalf.move(0, .5, 0));

        VoxelShape shapeTopBottomHalf = ShapeUtils.orUnoptimized(
                shapeSlopeTop,
                ShapeUtils.rotateShapeUnoptimized(Direction.NORTH, Direction.WEST, shapeSlopeTop)
        );
        map.put(SlopeSlabShape.TOP_BOTTOM_HALF, shapeTopBottomHalf);
        map.put(SlopeSlabShape.TOP_TOP_HALF, shapeTopBottomHalf.move(0, .5, 0));
    });

    public static ShapeProvider generateInnerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        int maskTop = 0b0100;
        int maskTopHalf = 0b1000;
        VoxelShape[] shapes = new VoxelShape[16];
        ShapeUtils.makeHorizontalRotations(INNER_SHAPES.get(SlopeSlabShape.BOTTOM_BOTTOM_HALF), Direction.NORTH, shapes, 0);
        ShapeUtils.makeHorizontalRotations(INNER_SHAPES.get(SlopeSlabShape.BOTTOM_TOP_HALF), Direction.NORTH, shapes, maskTopHalf);
        ShapeUtils.makeHorizontalRotations(INNER_SHAPES.get(SlopeSlabShape.TOP_BOTTOM_HALF), Direction.NORTH, shapes, maskTop);
        ShapeUtils.makeHorizontalRotations(INNER_SHAPES.get(SlopeSlabShape.TOP_TOP_HALF), Direction.NORTH, shapes, maskTop | maskTopHalf);

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            int top = state.getValue(FramedProperties.TOP) ? maskTop : 0;
            int topHalf = state.getValue(PropertyHolder.TOP_HALF) ? maskTopHalf : 0;
            int idx = dir.get2DDataValue() | top | topHalf;
            builder.put(state, shapes[idx]);
        }

        return ShapeProvider.of(builder.build());
    }
}
