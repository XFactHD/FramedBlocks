package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public class FramedFlatExtendedSlopePanelCornerBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = CtmPredicate.HOR_DIR.or((state, side) ->
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction orientation = state.getValue(PropertyHolder.ROTATION).withFacing(facing);

        if (side == orientation.getOpposite())
        {
            return true;
        }

        Direction rotOrientation;
        if (Utils.isPositive(facing))
        {
            rotOrientation = orientation.getClockWise(facing.getAxis());
        }
        else
        {
            rotOrientation = orientation.getCounterClockWise(facing.getAxis());
        }
        return side == rotOrientation.getOpposite();
    });

    public FramedFlatExtendedSlopePanelCornerBlock(BlockType type) { super(type); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.ROTATION, FramedProperties.SOLID,
                FramedProperties.GLOWING, BlockStateProperties.WATERLOGGED
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return FramedFlatSlopePanelCornerBlock.getStateForPlacement(this, false, context);
    }



    private static final VoxelShape PANEL_SHAPE = box(0, 0, 0, 16, 16, 8);

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            VoxelShape shape = Shapes.join(
                    FramedSlopePanelBlock.SHAPES.get(rot),
                    FramedSlopePanelBlock.SHAPES.get(rot.rotate(Rotation.COUNTERCLOCKWISE_90)),
                    BooleanOp.AND
            ).move(0, 0, .5);

            shape = Shapes.or(
                    shape,
                    PANEL_SHAPE
            );

            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            builder.put(
                    state,
                    Utils.rotateShape(Direction.NORTH, facing, shape)
            );
        }

        return builder.build();
    }

    public static ImmutableMap<BlockState, VoxelShape> generateInnerShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            VoxelShape shape = Shapes.or(
                    FramedSlopePanelBlock.SHAPES.get(rot),
                    FramedSlopePanelBlock.SHAPES.get(rot.rotate(Rotation.COUNTERCLOCKWISE_90))
            ).move(0, 0, .5);

            shape = Shapes.or(
                    shape,
                    PANEL_SHAPE
            );

            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            builder.put(
                    state,
                    Utils.rotateShape(Direction.NORTH, facing, shape)
            );
        }

        return builder.build();
    }
}
