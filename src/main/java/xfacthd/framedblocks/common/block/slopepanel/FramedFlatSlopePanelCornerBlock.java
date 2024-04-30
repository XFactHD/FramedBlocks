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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.ExtPlacementStateBuilder;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public class FramedFlatSlopePanelCornerBlock extends FramedBlock
{
    public FramedFlatSlopePanelCornerBlock(BlockType type)
    {
        super(type);
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
        return getStateForPlacement(this, true, context);
    }

    public static BlockState getStateForPlacement(Block block, boolean hasFront, BlockPlaceContext context)
    {
        ExtPlacementStateBuilder builder = ExtPlacementStateBuilder.of(block, context)
                .withHorizontalFacing()
                .withCornerOrSideRotation();

        if (hasFront)
        {
            builder = builder.withFront();
        }

        return builder.tryWithWater()
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
        HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rotation.withFacing(dir);
        Direction perpRotDir = rotation.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);

        if (face == rotDir || face == perpRotDir)
        {
            if (getBlockType() == BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER)
            {
                face = dir.getOpposite();
            }
            else //FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER
            {
                Vec3 vec = Utils.fraction(hit.getLocation());

                double hor = Utils.isX(dir) ? vec.x() : vec.z();
                if (!Utils.isPositive(dir))
                {
                    hor = 1D - hor;
                }
                if (!state.getValue(PropertyHolder.FRONT))
                {
                    hor -= .5D;
                }

                Direction perpDir = face == rotDir ? perpRotDir : rotDir;
                double vert = Utils.isY(perpDir) ? vec.y() : (Utils.isX(dir) ? vec.z() : vec.x());
                if (perpDir == Direction.DOWN || (!Utils.isY(perpDir) && !Utils.isPositive(perpDir)))
                {
                    vert = 1F - vert;
                }
                if ((hor * 2D) < vert)
                {
                    face = dir.getOpposite();
                }
            }
        }

        return rotate(state, face, rot);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
        if (face.getAxis() == dir.getAxis())
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
        return rotate(state, Direction.UP, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return mirrorCorner(state, mirror);
    }

    public static BlockState mirrorCorner(BlockState state, Mirror mirror)
    {
        BlockState newState = Utils.mirrorFaceBlock(state, mirror);
        if (newState != state)
        {
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            boolean vert = rot.isVertical();

            rot = rot.rotate(vert ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90);

            return newState.setValue(PropertyHolder.ROTATION, rot);
        }
        return state;
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.ROTATION, HorizontalRotation.RIGHT);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }



    public record ShapeKey(HorizontalRotation rot, boolean front) { }

    public static final ShapeCache<ShapeKey> SHAPES = ShapeCache.create(map ->
    {
        for (HorizontalRotation rot : HorizontalRotation.values())
        {
            VoxelShape preShape = ShapeUtils.andUnoptimized(
                    FramedSlopePanelBlock.SHAPES.get(SlopePanelShape.get(rot, false)),
                    FramedSlopePanelBlock.SHAPES.get(SlopePanelShape.get(rot.rotate(Rotation.COUNTERCLOCKWISE_90), false))
            );
            map.put(new ShapeKey(rot, false), preShape);
            map.put(new ShapeKey(rot, true), preShape.move(0, 0, .5));
        }
    });

    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        int maskFront = 0b100;
        VoxelShape[] shapes = new VoxelShape[4 * 4 * 2];
        for (HorizontalRotation rot : HorizontalRotation.values())
        {
            VoxelShape preShape = SHAPES.get(new ShapeKey(rot, false));
            VoxelShape preShapeFront = SHAPES.get(new ShapeKey(rot, true));

            ShapeUtils.makeHorizontalRotations(preShape, Direction.NORTH, shapes, rot.ordinal() << 3);
            ShapeUtils.makeHorizontalRotations(preShapeFront, Direction.NORTH, shapes, maskFront | (rot.ordinal() << 3));
        }

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            int front = state.getValue(PropertyHolder.FRONT) ? maskFront : 0;
            int idx = dir.get2DDataValue() | front | (rot.ordinal() << 3);
            builder.put(state, shapes[idx]);
        }

        return ShapeProvider.of(builder.build());
    }

    public static final ShapeCache<ShapeKey> INNER_SHAPES = ShapeCache.create(map ->
    {
        for (HorizontalRotation rot : HorizontalRotation.values())
        {
            VoxelShape preShape = ShapeUtils.orUnoptimized(
                    FramedSlopePanelBlock.SHAPES.get(SlopePanelShape.get(rot, false)),
                    FramedSlopePanelBlock.SHAPES.get(SlopePanelShape.get(rot.rotate(Rotation.COUNTERCLOCKWISE_90), false))
            );
            map.put(new ShapeKey(rot, false), preShape);
            map.put(new ShapeKey(rot, true), preShape.move(0, 0, .5));
        }
    });

    public static ShapeProvider generateInnerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        int maskFront = 0b100;
        VoxelShape[] shapes = new VoxelShape[4 * 4 * 2];
        for (HorizontalRotation rot : HorizontalRotation.values())
        {
            VoxelShape preShape = INNER_SHAPES.get(new ShapeKey(rot, false));
            VoxelShape preShapeFront = INNER_SHAPES.get(new ShapeKey(rot, true));

            for (Direction dir : Direction.Plane.HORIZONTAL)
            {
                int idx = dir.get2DDataValue() | (rot.ordinal() << 3);
                shapes[idx] = ShapeUtils.rotateShape(Direction.NORTH, dir, preShape);
                idx = dir.get2DDataValue() | maskFront | (rot.ordinal() << 3);
                shapes[idx] = ShapeUtils.rotateShape(Direction.NORTH, dir, preShapeFront);
            }
        }

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            int front = state.getValue(PropertyHolder.FRONT) ? maskFront : 0;
            int idx = dir.get2DDataValue() | front | (rot.ordinal() << 3);
            builder.put(state, shapes[idx]);
        }

        return ShapeProvider.of(builder.build());
    }
}
