package xfacthd.framedblocks.common.block.slopepanelcorner;

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
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.ExtPlacementStateBuilder;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.block.slopepanel.FramedSlopePanelBlock;
import xfacthd.framedblocks.common.block.slopeslab.FramedSlopeSlabBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

import java.util.EnumMap;

@SuppressWarnings("deprecation")
public class FramedCornerSlopePanelWallBlock extends FramedBlock
{
    private final boolean large;

    public FramedCornerSlopePanelWallBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, true));
        this.large = type == BlockType.FRAMED_LARGE_CORNER_SLOPE_PANEL_W ||
                     type == BlockType.FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.ROTATION,
                FramedProperties.Y_SLOPE, BlockStateProperties.WATERLOGGED
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return getStateForPlacement(this, ctx, large);
    }

    public static BlockState getStateForPlacement(Block block, BlockPlaceContext ctx, boolean invert)
    {
        return ExtPlacementStateBuilder.of(block, ctx)
                .withHorizontalTargetFacing()
                .withCornerRotation(!invert)
                .tryWithWater()
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
        Direction side = hit.getDirection();

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rotation.withFacing(dir);
        Direction perpRotDir = rotation.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
        switch ((BlockType) getBlockType())
        {
            case FRAMED_SMALL_CORNER_SLOPE_PANEL_W, FRAMED_LARGE_CORNER_SLOPE_PANEL_W ->
            {
                if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
                {
                    side = dir;
                }
            }
            case FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W, FRAMED_LARGE_INNER_CORNER_SLOPE_PANEL_W ->
            {
                if (side == rotDir || side == perpRotDir)
                {
                    Vec3 hitVec = hit.getLocation();
                    double paralell = Utils.fractionInDir(hitVec, dir);
                    double perp = Utils.fractionInDir(hitVec, side == rotDir ? perpRotDir : rotDir) - .5;
                    if (perp * 2D > paralell)
                    {
                        side = dir;
                    }
                }
            }
        }
        return rotate(state, side, rot);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (face.getAxis() == dir.getAxis())
        {
            HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
            return state.setValue(PropertyHolder.ROTATION, rotation.rotate(rot));
        }
        else if (Utils.isY(face))
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, state.getValue(FramedProperties.FACING_HOR), rot);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return mirrorCornerPanel(state, mirror);
    }

    public static BlockState mirrorCornerPanel(BlockState state, Mirror mirror)
    {
        if (mirror == Mirror.NONE)
        {
            return state;
        }

        BlockState newState = Utils.mirrorFaceBlock(state, mirror);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        rot = rot.rotate(rot.isVertical() ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90);
        return newState.setValue(PropertyHolder.ROTATION, rot);
    }



    public static ShapeProvider generateSmallShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape[] shapes = new VoxelShape[4 * 4];
        for (HorizontalRotation rot : HorizontalRotation.values())
        {
            VoxelShape shapeOne = switch (rot)
            {
                case UP, LEFT -> ShapeUtils.rotateShapeUnoptimized(
                        Direction.NORTH,
                        Direction.WEST,
                        FramedSlopePanelBlock.SHAPES.get(HorizontalRotation.LEFT)
                );
                case DOWN, RIGHT -> ShapeUtils.rotateShapeUnoptimized(
                        Direction.NORTH,
                        Direction.EAST,
                        FramedSlopePanelBlock.SHAPES.get(HorizontalRotation.RIGHT)
                );
            };
            VoxelShape shapeTwo = switch (rot)
            {
                case UP, RIGHT -> FramedSlopeSlabBlock.SHAPES.get(Boolean.TRUE).move(0, .5, 0);
                case DOWN, LEFT -> FramedSlopeSlabBlock.SHAPES.get(Boolean.FALSE);
            };
            VoxelShape preShape = ShapeUtils.andUnoptimized(shapeOne, shapeTwo);
            ShapeUtils.makeHorizontalRotations(preShape, Direction.NORTH, shapes, rot.ordinal() << 2);
        }

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            int idx = dir.get2DDataValue() | (rot.ordinal() << 2);
            builder.put(state, shapes[idx]);
        }

        return ShapeProvider.of(builder.build());
    }

    public static final ShapeCache<HorizontalRotation> SHAPES_LARGE = new ShapeCache<>(
            new EnumMap<>(HorizontalRotation.class),
            map ->
            {
                for (HorizontalRotation rot : HorizontalRotation.values())
                {
                    VoxelShape shapeOne = switch (rot)
                    {
                        case UP, LEFT -> ShapeUtils.rotateShapeUnoptimized(
                                Direction.NORTH,
                                Direction.WEST,
                                FramedSlopePanelBlock.SHAPES.get(HorizontalRotation.LEFT).move(0, 0, .5)
                        );
                        case DOWN, RIGHT -> ShapeUtils.rotateShapeUnoptimized(
                                Direction.NORTH,
                                Direction.EAST,
                                FramedSlopePanelBlock.SHAPES.get(HorizontalRotation.RIGHT).move(0, 0, .5)
                        );
                    };
                    VoxelShape shapeTwo = switch (rot)
                    {
                        case UP, RIGHT -> FramedSlopeSlabBlock.SHAPES.get(Boolean.TRUE);
                        case DOWN, LEFT -> FramedSlopeSlabBlock.SHAPES.get(Boolean.FALSE).move(0, .5, 0);
                    };
                    VoxelShape mask = Shapes.joinUnoptimized(Shapes.block(), rot.getOpposite().getCornerShape(), BooleanOp.NOT_SAME);
                    map.put(rot, ShapeUtils.orUnoptimized(
                            ShapeUtils.andUnoptimized(shapeOne, shapeTwo),
                            ShapeUtils.orUnoptimized(
                                    ShapeUtils.andUnoptimized(shapeOne, mask),
                                    ShapeUtils.andUnoptimized(shapeTwo, mask)
                            )
                    ));
                }
            }
    );

    public static ShapeProvider generateLargeShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape[] shapes = new VoxelShape[4 * 4];
        for (HorizontalRotation rot : HorizontalRotation.values())
        {
            VoxelShape preShape = SHAPES_LARGE.get(rot);
            ShapeUtils.makeHorizontalRotations(preShape, Direction.NORTH, shapes, rot.ordinal() << 2);
        }

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            int idx = dir.get2DDataValue() | (rot.ordinal() << 2);
            builder.put(state, shapes[idx]);
        }

        return ShapeProvider.of(builder.build());
    }

    public static final ShapeCache<HorizontalRotation> SHAPES_SMALL_INNER = new ShapeCache<>(
            new EnumMap<>(HorizontalRotation.class),
            shapes ->
            {
                for (HorizontalRotation rot : HorizontalRotation.values())
                {
                    VoxelShape shapeOne = switch (rot)
                    {
                        case UP, LEFT -> ShapeUtils.rotateShapeUnoptimized(
                                Direction.NORTH,
                                Direction.EAST,
                                FramedSlopePanelBlock.SHAPES.get(HorizontalRotation.RIGHT).move(0, 0, .5)
                        );
                        case DOWN, RIGHT -> ShapeUtils.rotateShapeUnoptimized(
                                Direction.NORTH,
                                Direction.WEST,
                                FramedSlopePanelBlock.SHAPES.get(HorizontalRotation.LEFT).move(0, 0, .5)
                        );
                    };
                    VoxelShape shapeTwo = switch (rot)
                    {
                        case UP, RIGHT -> FramedSlopeSlabBlock.SHAPES.get(Boolean.FALSE).move(0, .5, 0);
                        case DOWN, LEFT -> FramedSlopeSlabBlock.SHAPES.get(Boolean.TRUE);
                    };
                    shapes.put(rot, ShapeUtils.andUnoptimized(
                            ShapeUtils.orUnoptimized(shapeOne, shapeTwo),
                            rot.getCornerShape()
                    ));
                }
            }
    );

    public static ShapeProvider generateSmallInnerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape[] shapes = new VoxelShape[4 * 4];
        for (HorizontalRotation rot : HorizontalRotation.values())
        {
            VoxelShape preShape = SHAPES_SMALL_INNER.get(rot);
            ShapeUtils.makeHorizontalRotations(preShape, Direction.NORTH, shapes, rot.ordinal() << 2);
        }

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            int idx = dir.get2DDataValue() | (rot.ordinal() << 2);
            builder.put(state, shapes[idx]);
        }

        return ShapeProvider.of(builder.build());
    }

    public static ShapeProvider generateLargeInnerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape[] shapes = new VoxelShape[4 * 4];
        for (HorizontalRotation rot : HorizontalRotation.values())
        {
            VoxelShape shapeOne = switch (rot)
            {
                case UP, LEFT -> ShapeUtils.rotateShapeUnoptimized(
                        Direction.NORTH,
                        Direction.EAST,
                        FramedSlopePanelBlock.SHAPES.get(HorizontalRotation.RIGHT)
                );
                case DOWN, RIGHT -> ShapeUtils.rotateShapeUnoptimized(
                        Direction.NORTH,
                        Direction.WEST,
                        FramedSlopePanelBlock.SHAPES.get(HorizontalRotation.LEFT)
                );
            };
            VoxelShape shapeTwo = switch (rot)
            {
                case UP, RIGHT -> FramedSlopeSlabBlock.SHAPES.get(Boolean.FALSE);
                case DOWN, LEFT -> FramedSlopeSlabBlock.SHAPES.get(Boolean.TRUE).move(0, .5, 0);
            };
            VoxelShape preShape = ShapeUtils.orUnoptimized(shapeOne, shapeTwo);
            ShapeUtils.makeHorizontalRotations(preShape, Direction.NORTH, shapes, rot.ordinal() << 2);
        }

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            int idx = dir.get2DDataValue() | (rot.ordinal() << 2);
            builder.put(state, shapes[idx]);
        }

        return ShapeProvider.of(builder.build());
    }
}
