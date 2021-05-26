package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;
import xfacthd.framedblocks.common.util.SideSkipPredicate;
import xfacthd.framedblocks.common.util.Utils;

public class FramedSlabEdgeBlock extends FramedBlock
{
    public static final SideSkipPredicate SKIP_PREDICATE = (world, pos, state, adjState, side) ->
    {
        Direction dir = state.get(PropertyHolder.FACING_HOR);

        if (adjState.getBlock() instanceof FramedSlabEdgeBlock)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            if (side == dir && adjDir == side.getOpposite())
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            else if (side == dir.rotateY() || side == dir.rotateYCCW())
            {
                return dir == adjDir && SideSkipPredicate.compareState(world, pos, side);
            }
            else if (side.getAxis() == Direction.Axis.Y && dir == adjDir)
            {
                return state.get(PropertyHolder.TOP) != adjState.get(PropertyHolder.TOP) && SideSkipPredicate.compareState(world, pos, side);
            }

            return false;
        }

        if (adjState.getBlock() instanceof FramedSlabBlock && side == dir)
        {
            if (state.get(PropertyHolder.TOP) != adjState.get(PropertyHolder.TOP)) { return false; }

            return SideSkipPredicate.compareState(world, pos, side);
        }

        if (adjState.getBlock() instanceof FramedDoubleSlabBlock && side == dir)
        {
            TileEntity te = world.getTileEntity(pos.offset(side));
            if (!(te instanceof FramedDoubleTileEntity)) { return false; }
            FramedDoubleTileEntity tile = (FramedDoubleTileEntity) te;

            Direction face = state.get(PropertyHolder.TOP) ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(world, pos, tile.getCamoState(face), side);
        }

        if (adjState.getBlock() instanceof FramedPanelBlock && side.getAxis() == Direction.Axis.Y)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            if (dir != adjDir) { return false; }

            boolean top = state.get(PropertyHolder.TOP);
            if ((side == Direction.UP && top) || (side == Direction.DOWN && !top))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        if (adjState.getBlock() instanceof FramedDoublePanelBlock && side.getAxis() == Direction.Axis.Y)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_NE);
            if (dir != adjDir && dir != adjDir.getOpposite()) { return false; }

            boolean top = state.get(PropertyHolder.TOP);
            if ((side == Direction.UP && top) || (side == Direction.DOWN && !top))
            {
                TileEntity te = world.getTileEntity(pos.offset(side));
                if (!(te instanceof FramedDoubleTileEntity)) { return false; }
                FramedDoubleTileEntity tile = (FramedDoubleTileEntity) te;

                return SideSkipPredicate.compareState(world, pos, tile.getCamoState(dir), side);
            }
            return false;
        }

        return false;
    };

    public FramedSlabEdgeBlock()
    {
        super("framed_slab_edge", BlockType.FRAMED_SLAB_EDGE);
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