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
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedSlopeSlabBlock extends FramedBlock
{
    public FramedSlopeSlabBlock()
    {
        super(BlockType.FRAMED_SLOPE_SLAB);
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
                FramedProperties.FACING_HOR, FramedProperties.TOP, PropertyHolder.TOP_HALF,
                BlockStateProperties.WATERLOGGED, FramedProperties.SOLID, FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withTargetOrHorizontalFacing()
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
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (Utils.isY(face) || face == dir.getOpposite())
        {
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
        return Utils.mirrorFaceBlock(state, mirror);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }



    public record ShapeKey(boolean top, boolean topHalf) { }

    public static final ShapeCache<SlopeSlabShape> SHAPES = ShapeCache.create(map ->
    {
        VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                box(0, 0, 0, 16,   .5, 16),
                box(0, 0, 0, 16,    2, 15),
                box(0, 2, 0, 16,    4, 12),
                box(0, 4, 0, 16,    6,  8),
                box(0, 6, 0, 16, 7.75,  4),
                box(0, 6, 0, 16,    8, .5)
        );
        map.put(SlopeSlabShape.BOTTOM_BOTTOM_HALF, shapeBottom);
        map.put(SlopeSlabShape.BOTTOM_TOP_HALF, shapeBottom.move(0, .5, 0));

        VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                box(0,   0, 0, 16, 2, .5),
                box(0, .25, 0, 16, 2,  4),
                box(0,   2, 0, 16, 4,  8),
                box(0,   4, 0, 16, 6, 12),
                box(0,   6, 0, 16, 8, 15),
                box(0, 7.5, 0, 16, 8, 16)
        );
        map.put(SlopeSlabShape.TOP_BOTTOM_HALF, shapeTop);
        map.put(SlopeSlabShape.TOP_TOP_HALF, shapeTop.move(0, .5, 0));
    });

    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeBottomBottomHalf = SHAPES.get(SlopeSlabShape.BOTTOM_BOTTOM_HALF);
        VoxelShape shapeBottomTopHalf = SHAPES.get(SlopeSlabShape.BOTTOM_TOP_HALF);
        VoxelShape shapeTopBottomHalf = SHAPES.get(SlopeSlabShape.TOP_BOTTOM_HALF);
        VoxelShape shapeTopTopHalf = SHAPES.get(SlopeSlabShape.TOP_TOP_HALF);

        int maskTop = 0b0100;
        int maskTopHalf = 0b1000;
        VoxelShape[] shapes = new VoxelShape[16];
        ShapeUtils.makeHorizontalRotations(shapeBottomBottomHalf, Direction.NORTH, shapes, 0);
        ShapeUtils.makeHorizontalRotations(shapeBottomTopHalf, Direction.NORTH, shapes, maskTopHalf);
        ShapeUtils.makeHorizontalRotations(shapeTopBottomHalf, Direction.NORTH, shapes, maskTop);
        ShapeUtils.makeHorizontalRotations(shapeTopTopHalf, Direction.NORTH, shapes, maskTop | maskTopHalf);

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
