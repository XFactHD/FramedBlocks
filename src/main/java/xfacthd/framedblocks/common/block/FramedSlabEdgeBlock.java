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
        setDefaultState(getDefaultState().with(PropertyHolder.TOP, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.TOP, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = withTop(getDefaultState(), context.getFace(), context.getHitVec());
        state = state.with(PropertyHolder.FACING_HOR, context.getPlacementHorizontalFacing());
        return withWater(state, context.getWorld(), context.getPos());
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader world, BlockPos pos, PathType type)
    {
        return type == PathType.WATER && world.getFluidState(pos).isTagged(FluidTags.WATER);
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape bottomShape = makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
        VoxelShape topShape = makeCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            VoxelShape shape = Utils.rotateShape(
                    Direction.NORTH,
                    state.get(PropertyHolder.FACING_HOR),
                    state.get(PropertyHolder.TOP) ? topShape : bottomShape
            );
            builder.put(state, shape);
        }

        return builder.build();
    }
}