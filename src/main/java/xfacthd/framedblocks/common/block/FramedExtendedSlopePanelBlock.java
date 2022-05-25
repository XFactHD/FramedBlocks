package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.CtmPredicate;
import xfacthd.framedblocks.common.util.Utils;

import java.util.EnumMap;
import java.util.Map;

public class FramedExtendedSlopePanelBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) ->
    {
        Direction facing = state.getValue(PropertyHolder.FACING_HOR);
        Direction orientation = state.getValue(PropertyHolder.ROTATION).withFacing(facing);
        return side == facing || side == orientation.getOpposite();
    };

    public FramedExtendedSlopePanelBlock() { super(BlockType.FRAMED_EXTENDED_SLOPE_PANEL); }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.ROTATION, PropertyHolder.SOLID, PropertyHolder.GLOWING, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
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
                .setValue(PropertyHolder.FACING_HOR, facing)
                .setValue(PropertyHolder.ROTATION, rotation);
        return withWater(state, context.getLevel(), context.getClickedPos());
    }



    private static final Map<Rotation, VoxelShape> SHAPES = Util.make(new EnumMap<>(Rotation.class), map ->
    {
        for (Rotation rot : Rotation.values())
        {
            VoxelShape shape = VoxelShapes.or(
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
            Direction facing = state.getValue(PropertyHolder.FACING_HOR);
            builder.put(
                    state,
                    Utils.rotateShape(Direction.NORTH, facing, shape)
            );
        }

        return builder.build();
    }
}
