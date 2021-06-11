package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;
import xfacthd.framedblocks.common.util.SideSkipPredicate;
import xfacthd.framedblocks.common.util.Utils;

public class FramedSlabEdgeBlock extends FramedBlock
{
    public static final SideSkipPredicate SKIP_PREDICATE = (world, pos, state, adjState, side) ->
    {
        Direction dir = state.get(PropertyHolder.FACING_HOR);
        boolean top = state.get(PropertyHolder.TOP);

        if (adjState.getBlock() == FBContent.blockFramedSlabEdge)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            boolean adjTop = adjState.get(PropertyHolder.TOP);
            if (side == dir && adjDir == side.getOpposite())
            {
                return top == adjTop && SideSkipPredicate.compareState(world, pos, side);
            }
            else if (side == dir.rotateY() || side == dir.rotateYCCW())
            {
                return dir == adjDir && top == adjTop && SideSkipPredicate.compareState(world, pos, side);
            }
            else if (side.getAxis() == Direction.Axis.Y && dir == adjDir)
            {
                return top != adjTop && SideSkipPredicate.compareState(world, pos, side);
            }

            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedSlab && side == dir)
        {
            if (top != adjState.get(PropertyHolder.TOP)) { return false; }

            return SideSkipPredicate.compareState(world, pos, side);
        }

        if (adjState.getBlock() == FBContent.blockFramedSlabCorner)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            boolean adjTop = adjState.get(PropertyHolder.TOP);
            if ((side == dir.rotateY() && adjDir == dir) || (side == dir.rotateYCCW() && adjDir == dir.rotateY()))
            {
                return top == adjTop && SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedDoubleSlab && side == dir)
        {
            TileEntity te = world.getTileEntity(pos.offset(side));
            if (!(te instanceof FramedDoubleTileEntity)) { return false; }
            FramedDoubleTileEntity tile = (FramedDoubleTileEntity) te;

            Direction face = state.get(PropertyHolder.TOP) ? Direction.UP : Direction.DOWN;
            return SideSkipPredicate.compareState(world, pos, tile.getCamoState(face), side);
        }

        if (adjState.getBlock() == FBContent.blockFramedPanel && side.getAxis() == Direction.Axis.Y)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            if (dir != adjDir) { return false; }

            if ((side == Direction.UP && top) || (side == Direction.DOWN && !top))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedDoublePanel && side.getAxis() == Direction.Axis.Y)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_NE);
            if (dir != adjDir && dir != adjDir.getOpposite()) { return false; }

            if ((side == Direction.UP && top) || (side == Direction.DOWN && !top))
            {
                TileEntity te = world.getTileEntity(pos.offset(side));
                if (!(te instanceof FramedDoubleTileEntity)) { return false; }
                FramedDoubleTileEntity tile = (FramedDoubleTileEntity) te;

                return SideSkipPredicate.compareState(world, pos, tile.getCamoState(dir), side);
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedStairs)
        {
            Direction adjDir = adjState.get(BlockStateProperties.HORIZONTAL_FACING);
            StairsShape adjShape = adjState.get(BlockStateProperties.STAIRS_SHAPE);
            boolean adjTop = adjState.get(BlockStateProperties.HALF) == Half.TOP;

            if ((top && side == Direction.UP) || (!top && side == Direction.DOWN))
            {
                if (adjShape != StairsShape.STRAIGHT || dir != adjDir) { return false; }
                return top == adjTop && SideSkipPredicate.compareState(world, pos, side);
            }
            else if (top == adjTop && side == dir && FramedStairsBlock.isSlabSide(adjShape, adjDir, side.getOpposite()))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedVerticalStairs)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            StairsType adjType = adjState.get(PropertyHolder.STAIRS_TYPE);

            if (adjType == StairsType.VERTICAL) { return false; }
            if (((side == dir.rotateYCCW() && adjDir == dir) || (side == dir.rotateY() && adjDir == dir.rotateY())))
            {
                return top != adjType.isTop() && SideSkipPredicate.compareState(world, pos, side);
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