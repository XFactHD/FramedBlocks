package xfacthd.framedblocks.common.block.stairs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.CtmPredicate;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.block.slope.FramedHalfSlopeBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public class FramedVerticalSlopedStairsBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = CtmPredicate.HOR_DIR.or((state, side) ->
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        return side == rot.getOpposite().withFacing(facing) || side == rot.rotate(Rotation.CLOCKWISE_90).withFacing(facing);
    });

    public FramedVerticalSlopedStairsBlock() { super(BlockType.FRAMED_VERTICAL_SLOPED_STAIRS); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.ROTATION, FramedProperties.SOLID, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState();

        Direction facing = context.getHorizontalDirection();
        state = state.setValue(FramedProperties.FACING_HOR, facing);

        Direction face = context.getClickedFace();
        HorizontalRotation rot;
        if (face == facing.getOpposite())
        {
            rot = HorizontalRotation.fromWallCorner(context.getClickLocation(), face);
        }
        else
        {
            rot = HorizontalRotation.fromPerpendicularWallCorner(facing, face, context.getClickLocation());
        }
        state = state.setValue(PropertyHolder.ROTATION, rot);

        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public BlockState rotate(BlockState state, BlockHitResult hit, Rotation rot)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction face = hit.getDirection();

        HorizontalRotation horRot = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = horRot.withFacing(facing);
        Direction rotDirTwo = horRot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);

        if (!Utils.isY(face) && (face == rotDir || face == rotDirTwo))
        {
            double frac = Utils.fractionInDir(hit.getLocation(), facing.getOpposite());
            if (frac >= .5)
            {
                face = Direction.UP;
            }
        }
        return rotate(state, face, rot);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (Utils.isY(face))
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(facing));
        }
        else if (face.getAxis() == facing.getAxis())
        {
            HorizontalRotation horRot = state.getValue(PropertyHolder.ROTATION);
            return state.setValue(PropertyHolder.ROTATION, horRot.rotate(rot));
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        return rotate(state, Direction.UP, rotation);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        if (mirror == Mirror.NONE) { return state; }

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if ((mirror == Mirror.FRONT_BACK && Utils.isX(dir)) || (mirror == Mirror.LEFT_RIGHT && Utils.isZ(dir)))
        {
            state = state.setValue(FramedProperties.FACING_HOR, dir.getOpposite());
        }

        HorizontalRotation horRot = state.getValue(PropertyHolder.ROTATION);
        horRot = horRot.isVertical() ? horRot.rotate(Rotation.CLOCKWISE_90) : horRot.rotate(Rotation.COUNTERCLOCKWISE_90);
        state = state.setValue(PropertyHolder.ROTATION, horRot);

        return state;
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape panelShape = box(0, 0, 0, 16, 16, 8);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            VoxelShape slopeShape = switch (state.getValue(PropertyHolder.ROTATION))
            {
                case UP -> Utils.rotateShape(Direction.NORTH, Direction.EAST, FramedHalfSlopeBlock.SHAPE_BOTTOM_RIGHT);
                case DOWN -> Utils.rotateShape(Direction.NORTH, Direction.WEST, FramedHalfSlopeBlock.SHAPE_TOP_LEFT);
                case RIGHT -> Utils.rotateShape(Direction.NORTH, Direction.WEST, FramedHalfSlopeBlock.SHAPE_BOTTOM_LEFT);
                case LEFT -> Utils.rotateShape(Direction.NORTH, Direction.EAST, FramedHalfSlopeBlock.SHAPE_TOP_RIGHT);
            };

            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            builder.put(state, Utils.rotateShape(
                    Direction.NORTH,
                    facing,
                    Shapes.or(slopeShape, panelShape)
            ));
        }

        return ShapeProvider.of(builder.build());
    }
}
