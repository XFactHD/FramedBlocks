package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.Rotation;

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
        Rotation rotation;
        if (side == facing.getOpposite())
        {
            rotation = Rotation.fromWallCross(context.getClickLocation(), side);
        }
        else
        {
            rotation = Rotation.fromDirection(facing, side);
        }

        BlockState state = defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, facing)
                .setValue(PropertyHolder.ROTATION, rotation);
        return withWater(state, context.getLevel(), context.getClickedPos());
    }



    private static final Map<Rotation, VoxelShape> SHAPES = Util.make(new EnumMap<>(Rotation.class), map ->
    {
        for (Rotation rot : Rotation.values())
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
