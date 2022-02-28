package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.*;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.Utils;

public class FramedSlabEdgeBlock extends FramedBlock
{
    public FramedSlabEdgeBlock()
    {
        super(BlockType.FRAMED_SLAB_EDGE);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.TOP, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = withTop(defaultBlockState(), context.getClickedFace(), context.getClickLocation());
        state = state.setValue(PropertyHolder.FACING_HOR, context.getHorizontalDirection());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public boolean isPathfindable(BlockState state, IBlockReader world, BlockPos pos, PathType type)
    {
        return type == PathType.WATER && world.getFluidState(pos).is(FluidTags.WATER);
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape bottomShape = box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
        VoxelShape topShape = box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            VoxelShape shape = Utils.rotateShape(
                    Direction.NORTH,
                    state.getValue(PropertyHolder.FACING_HOR),
                    state.getValue(PropertyHolder.TOP) ? topShape : bottomShape
            );
            builder.put(state, shape);
        }

        return builder.build();
    }
}