package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

import java.util.EnumMap;
import java.util.Map;

public class FramedExtendedSlopePanelBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) ->
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction orientation = state.getValue(PropertyHolder.ROTATION).withFacing(facing);
        return side == facing || side == orientation.getOpposite();
    };

    public FramedExtendedSlopePanelBlock() { super(BlockType.FRAMED_EXTENDED_SLOPE_PANEL); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.ROTATION, FramedProperties.SOLID, FramedProperties.GLOWING, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction facing = context.getHorizontalDirection();

        Direction side = context.getClickedFace();
        HorizontalRotation rotation;
        if (side == facing.getOpposite())
        {
            rotation = HorizontalRotation.fromWallCross(context.getClickLocation(), side);
        }
        else
        {
            rotation = HorizontalRotation.fromDirection(facing, side);
        }

        BlockState state = defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, facing)
                .setValue(PropertyHolder.ROTATION, rotation);
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public BlockState rotate(BlockState state, BlockHitResult hit, Rotation rot)
    {
        Direction face = hit.getDirection();

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
        if (face == rotation.withFacing(dir))
        {
            double xz = Utils.fractionInDir(hit.getLocation(), dir.getOpposite());
            if (xz > .5)
            {
                face = dir.getOpposite();
            }
        }

        return rotate(state, face, rot);
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
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot) { return rotate(state, Direction.UP, rot); }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return FramedSlopePanelBlock.mirrorPanel(state, mirror);
    }



    private static final Map<HorizontalRotation, VoxelShape> SHAPES = Util.make(new EnumMap<>(HorizontalRotation.class), map ->
    {
        for (HorizontalRotation rot : HorizontalRotation.values())
        {
            VoxelShape shape = Shapes.or(
                    box(0, 0, 0, 16, 16, 8),
                    FramedSlopePanelBlock.SHAPES.get(rot).move(0, 0, .5)
            );
            map.put(rot, shape);
        }
    });

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            VoxelShape shape = SHAPES.get(state.getValue(PropertyHolder.ROTATION));
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            builder.put(
                    state,
                    Utils.rotateShape(Direction.NORTH, facing, shape)
            );
        }

        return builder.build();
    }
}
