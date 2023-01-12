package xfacthd.framedblocks.common.block.slopepanel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public class FramedFlatSlopePanelCornerBlock extends FramedBlock
{
    public FramedFlatSlopePanelCornerBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.FRONT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.ROTATION, PropertyHolder.FRONT, FramedProperties.SOLID, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return getStateForPlacement(this, true, context);
    }

    public static BlockState getStateForPlacement(Block block, boolean hasFront, BlockPlaceContext context)
    {
        return getStateForPlacement(block, hasFront, true, context);
    }

    public static BlockState getStateForPlacement(Block block, boolean hasFront, boolean hasWater, BlockPlaceContext context)
    {
        Direction facing = context.getHorizontalDirection();

        Direction side = context.getClickedFace();
        HorizontalRotation rotation;
        if (side == facing.getOpposite())
        {
            rotation = HorizontalRotation.fromWallCorner(context.getClickLocation(), side);
        }
        else
        {
            rotation = HorizontalRotation.fromDirection(facing, side);
        }

        BlockState state = block.defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, facing)
                .setValue(PropertyHolder.ROTATION, rotation);
        if (hasFront)
        {
            boolean front = false;
            if (side.getAxis() != facing.getAxis())
            {
                Vec3 subHit = Utils.fraction(context.getClickLocation());
                double xz = Utils.isX(facing) ? subHit.x : subHit.z;
                front = (xz < .5) == Utils.isPositive(facing);
            }

            state = state.setValue(PropertyHolder.FRONT, front);
        }
        if (hasWater)
        {
            state = withWater(state, context.getLevel(), context.getClickedPos());
        }
        return state;
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
    public BlockState rotate(BlockState state, Rotation rot) { return rotate(state, Direction.UP, rot); }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
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



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            VoxelShape shape = Shapes.join(
                    FramedSlopePanelBlock.SHAPES.get(rot),
                    FramedSlopePanelBlock.SHAPES.get(rot.rotate(Rotation.COUNTERCLOCKWISE_90)),
                    BooleanOp.AND
            );
            if (state.getValue(PropertyHolder.FRONT))
            {
                shape = shape.move(0, 0, .5);
            }

            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            builder.put(
                    state,
                    Utils.rotateShape(Direction.NORTH, facing, shape)
            );
        }

        return ShapeProvider.of(builder.build());
    }

    public static ShapeProvider generateInnerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            VoxelShape shape = Shapes.or(
                    FramedSlopePanelBlock.SHAPES.get(rot),
                    FramedSlopePanelBlock.SHAPES.get(rot.rotate(Rotation.COUNTERCLOCKWISE_90))
            );
            if (state.getValue(PropertyHolder.FRONT))
            {
                shape = shape.move(0, 0, .5);
            }

            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            builder.put(
                    state,
                    Utils.rotateShape(Direction.NORTH, facing, shape)
            );
        }

        return ShapeProvider.of(builder.build());
    }
}
