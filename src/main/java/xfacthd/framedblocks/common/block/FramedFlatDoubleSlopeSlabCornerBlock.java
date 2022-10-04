package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.blockentity.FramedFlatDoubleSlopeSlabCornerBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedFlatDoubleSlopeSlabCornerBlock extends AbstractFramedDoubleBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) ->
    {
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
        return topHalf ? side == Direction.UP : side == Direction.DOWN;
    };

    public FramedFlatDoubleSlopeSlabCornerBlock()
    {
        super(BlockType.FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(PropertyHolder.TOP_HALF, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, FramedProperties.TOP, PropertyHolder.TOP_HALF, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction face = context.getClickedFace();
        Direction facing = Utils.isY(face) ? context.getHorizontalDirection() : face.getOpposite();

        BlockState state = defaultBlockState().setValue(FramedProperties.FACING_HOR, facing);

        state = withTop(state, PropertyHolder.TOP_HALF, context.getClickedFace(), context.getClickLocation());
        state = state.setValue(FramedProperties.TOP, context.getPlayer() != null && context.getPlayer().isShiftKeyDown());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        if (Utils.isY(face))
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if (rot != Rotation.NONE)
        {
            return state.cycle(PropertyHolder.TOP_HALF);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, Direction.UP, rot);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedFlatDoubleSlopeSlabCornerBlockEntity(pos, state);
    }



    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeBot = box(0, 0, 0, 16,  8, 16);
        VoxelShape shapeTop = box(0, 8, 0, 16, 16, 16);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
            builder.put(state, topHalf ? shapeTop : shapeBot);
        }

        return builder.build();
    }
}
