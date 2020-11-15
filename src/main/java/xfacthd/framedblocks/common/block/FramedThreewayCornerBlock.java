package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedThreewayCornerBlock extends FramedBlock
{
    public FramedThreewayCornerBlock(String name, BlockType type) { super(name, type); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.TOP, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = getDefaultState();

        Direction facing = context.getPlacementHorizontalFacing();
        state = state.with(PropertyHolder.FACING_HOR, facing);

        state = withWater(state, context.getWorld(), context.getPos());
        return withTop(state, context.getFace(), context.getHitVec());
    }

    public static ImmutableMap<BlockState, VoxelShape> generatePrismShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            //TODO: implement
            builder.put(state, VoxelShapes.fullCube());
        }

        return builder.build();
    }

    public static ImmutableMap<BlockState, VoxelShape> generateInnerPrismShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            //TODO: implement
            builder.put(state, VoxelShapes.fullCube());
        }

        return builder.build();
    }

    public static ImmutableMap<BlockState, VoxelShape> generateThreewayShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            //TODO: implement
            builder.put(state, VoxelShapes.fullCube());
        }

        return builder.build();
    }

    public static ImmutableMap<BlockState, VoxelShape> generateInnerThreewayShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            //TODO: implement
            builder.put(state, VoxelShapes.fullCube());
        }

        return builder.build();
    }
}