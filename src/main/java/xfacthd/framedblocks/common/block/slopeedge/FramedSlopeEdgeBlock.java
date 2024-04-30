package xfacthd.framedblocks.common.block.slopeedge;

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
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;

public class FramedSlopeEdgeBlock extends FramedBlock implements IComplexSlopeSource
{
    public FramedSlopeEdgeBlock()
    {
        super(BlockType.FRAMED_SLOPE_EDGE);
        registerDefaultState(defaultBlockState()
                .setValue(PropertyHolder.ALT_TYPE, false)
                .setValue(FramedProperties.Y_SLOPE, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.SLOPE_TYPE, PropertyHolder.ALT_TYPE,
                FramedProperties.Y_SLOPE, BlockStateProperties.WATERLOGGED
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return ExtPlacementStateBuilder.of(this, ctx)
                .withHorizontalFacingAndSlopeType()
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
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        if (Utils.isY(face) || (type != SlopeType.HORIZONTAL && face == dir.getOpposite()))
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if (rot != Rotation.NONE && face == dir)
        {
            return state.cycle(PropertyHolder.SLOPE_TYPE);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, Direction.UP, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        if (state.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            return Utils.mirrorCornerBlock(state, mirror);
        }
        else
        {
            return Utils.mirrorFaceBlock(state, mirror);
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
        return getItemModelSource();
    }

    @Override
    public boolean isHorizontalSlope(BlockState state)
    {
        return state.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL;
    }



    public record ShapeKey(SlopeType type, boolean altType) { }

    public static final ShapeCache<ShapeKey> SHAPES = ShapeCache.create(map ->
    {
        VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                box(0, 0, 0, 16, 0.25, 8),
                box(0, 0.25, 0, 16, 4, 7.75),
                box(0, 4, 0, 16, 7.75, 4),
                box(0, 7.75, 0, 16, 8, 0.25)
        );
        map.put(new ShapeKey(SlopeType.BOTTOM, false), shapeBottom);
        map.put(new ShapeKey(SlopeType.BOTTOM, true), shapeBottom.move(0, .5, .5));

        VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                box(0, 15.75, 0, 16, 16, 8),
                box(0, 12, 0, 16, 15.75, 7.75),
                box(0, 8.25, 0, 16, 12, 4),
                box(0, 8, 0, 16, 8.25, 0.25)
        );
        map.put(new ShapeKey(SlopeType.TOP, false), shapeTop);
        map.put(new ShapeKey(SlopeType.TOP, true), shapeTop.move(0, -.5, .5));

        VoxelShape shapeHorizontal = ShapeUtils.orUnoptimized(
                box(0, 0, 0, 0.25, 16, 8),
                box(0.25, 0, 0, 4, 16, 7.75),
                box(4, 0, 0, 7.75, 16, 4),
                box(7.75, 0, 0, 8, 16, 0.25)
        );
        map.put(new ShapeKey(SlopeType.HORIZONTAL, false), shapeHorizontal);
        map.put(new ShapeKey(SlopeType.HORIZONTAL, true), shapeHorizontal.move(.5, 0, .5));
    });

    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape[] shapes = new VoxelShape[3 * 4 * 2];

        for (SlopeType type : SlopeType.values())
        {
            ShapeUtils.makeHorizontalRotations(
                    SHAPES.get(new ShapeKey(type, false)),
                    Direction.NORTH,
                    shapes,
                    type,
                    (dir, keyType) -> makeShapeIndex(dir, keyType, false)
            );
            ShapeUtils.makeHorizontalRotations(
                    SHAPES.get(new ShapeKey(type, true)),
                    Direction.NORTH,
                    shapes,
                    type,
                    (dir, keyType) -> makeShapeIndex(dir, keyType, true)
            );
        }

        ImmutableMap.Builder<BlockState, VoxelShape> builder = new ImmutableMap.Builder<>();

        for (BlockState state : states)
        {
            SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean altType = state.getValue(PropertyHolder.ALT_TYPE);
            builder.put(state, shapes[makeShapeIndex(dir, type, altType)]);
        }

        return ShapeProvider.of(builder.build());
    }

    public static int makeShapeIndex(Direction dir, SlopeType type, boolean altType)
    {
        return (type.ordinal() << 3) | (dir.get2DDataValue() << 1) | (altType ? 1 : 0);
    }
}
