package xfacthd.framedblocks.common.block.slopepanel;

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
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.ExtPlacementStateBuilder;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public class FramedSlopePanelBlock extends FramedBlock
{
    public FramedSlopePanelBlock()
    {
        super(BlockType.FRAMED_SLOPE_PANEL);
        registerDefaultState(defaultBlockState()
                .setValue(PropertyHolder.FRONT, false)
                .setValue(FramedProperties.Y_SLOPE, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.ROTATION, PropertyHolder.FRONT, FramedProperties.SOLID,
                BlockStateProperties.WATERLOGGED, FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return getStateForPlacement(this, context);
    }

    public static BlockState getStateForPlacement(Block block, BlockPlaceContext context)
    {
        return ExtPlacementStateBuilder.of(block, context)
                .withHorizontalFacing()
                .withCrossOrSideRotation()
                .withFront()
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
        HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
        if (face.getAxis() == dir.getAxis() || face == rotation.withFacing(dir))
        {
            return state.setValue(PropertyHolder.ROTATION, rotation.rotate(rot));
        }
        else if (Utils.isY(face))
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if (rot != Rotation.NONE)
        {
            return state.cycle(PropertyHolder.FRONT);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        Direction side = Direction.UP;
        if (state.getValue(PropertyHolder.ROTATION) == HorizontalRotation.UP)
        {
            side = Direction.DOWN;
        }
        return rotate(state, side, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return mirrorPanel(state, mirror);
    }

    public static BlockState mirrorPanel(BlockState state, Mirror mirror)
    {
        BlockState newState = Utils.mirrorFaceBlock(state, mirror);

        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        if (newState != state && !rot.isVertical())
        {
            state = state.setValue(PropertyHolder.ROTATION, rot.getOpposite());
        }

        return state;
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



    public static final ShapeCache<SlopePanelShape> SHAPES = ShapeCache.create(map ->
    {
        VoxelShape shapeUp = ShapeUtils.orUnoptimized(
                box(0, 0, 0, 16, .5, 8),
                box(0, .5, 0, 16, 4, 7.75),
                box(0, 4, 0, 16, 8, 6),
                box(0, 8, 0, 16, 12, 4),
                box(0, 12, 0, 16, 15, 2),
                box(0, 15, 0, 16, 16, 0.5)
        );
        map.put(SlopePanelShape.UP_BACK, shapeUp);
        map.put(SlopePanelShape.UP_FRONT, shapeUp.move(0, 0, .5));

        VoxelShape shapeRight = ShapeUtils.orUnoptimized(
                box(0, 0, 0, .5, 16, 8),
                box(.5, 0, 0, 4, 16, 7.75),
                box(4, 0, 0, 8, 16, 6),
                box(8, 0, 0, 12, 16, 4),
                box(12, 0, 0, 15, 16, 2),
                box(15, 0, 0, 16, 16, 0.5)
        );
        map.put(SlopePanelShape.RIGHT_BACK, shapeRight);
        map.put(SlopePanelShape.RIGHT_FRONT, shapeRight.move(0, 0, .5));

        VoxelShape shapeDown = ShapeUtils.orUnoptimized(
                box(0, 15.5, 0, 16, 16, 8),
                box(0, 12, 0, 16, 15.5, 7.75),
                box(0, 8, 0, 16, 12, 6),
                box(0, 4, 0, 16, 8, 4),
                box(0, 1, 0, 16, 4, 2),
                box(0, 0, 0, 16, 1, 0.5)
        );
        map.put(SlopePanelShape.DOWN_BACK, shapeDown);
        map.put(SlopePanelShape.DOWN_FRONT, shapeDown.move(0, 0, .5));

        VoxelShape shapeLeft = ShapeUtils.orUnoptimized(
                box(15.5, 0, 0, 16, 16, 8),
                box(12, 0, 0, 15.5, 16, 7.75),
                box(8, 0, 0, 12, 16, 6),
                box(4, 0, 0, 8, 16, 4),
                box(1, 0, 0, 4, 16, 2),
                box(0, 0, 0, 1, 16, 0.5)
        );
        map.put(SlopePanelShape.LEFT_BACK, shapeLeft);
        map.put(SlopePanelShape.LEFT_FRONT, shapeLeft.move(0, 0, .5));
    });

    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        int maskFront = 0b10000;
        VoxelShape[] shapes = new VoxelShape[4 * 4 * 2];
        for (HorizontalRotation rot : HorizontalRotation.values())
        {
            VoxelShape shape = SHAPES.get(SlopePanelShape.get(rot, false));
            VoxelShape shapeFront = SHAPES.get(SlopePanelShape.get(rot, true));
            ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, shapes, rot.ordinal() << 2);
            ShapeUtils.makeHorizontalRotations(shapeFront, Direction.NORTH, shapes, maskFront | (rot.ordinal() << 2));
        }

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            int front = state.getValue(PropertyHolder.FRONT) ? maskFront : 0;
            int idx = dir.get2DDataValue() | (rot.ordinal() << 2) | front;
            builder.put(state, shapes[idx]);
        }

        return ShapeProvider.of(builder.build());
    }
}
