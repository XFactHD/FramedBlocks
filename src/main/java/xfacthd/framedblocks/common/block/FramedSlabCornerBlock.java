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
import net.minecraft.util.math.vector.Vector3d;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;
import xfacthd.framedblocks.common.util.SideSkipPredicate;
import xfacthd.framedblocks.common.util.Utils;

public class FramedSlabCornerBlock extends FramedBlock
{
    public static final SideSkipPredicate SKIP_PREDICATE = (world, pos, state, adjState, side) ->
    {
        Direction dir = state.get(PropertyHolder.FACING_HOR);
        boolean top = state.get(PropertyHolder.TOP);

        if (adjState.getBlock() == FBContent.blockFramedSlabCorner)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            boolean adjTop = adjState.get(PropertyHolder.TOP);
            if ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjDir == dir.rotateY()))
            {
                return top == adjTop && SideSkipPredicate.compareState(world, pos, side);
            }
            if ((side == Direction.DOWN && !top && adjTop) || (side == Direction.UP && top && !adjTop))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedSlabEdge)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            boolean adjTop = adjState.get(PropertyHolder.TOP);
            if ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjDir == dir))
            {
                return top == adjTop && SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedCornerPillar)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            if ((top && side == Direction.UP) || (!top && side == Direction.DOWN))
            {
                return dir == adjDir && SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedStairs && side.getAxis() == Direction.Axis.Y)
        {
            Direction adjDir = adjState.get(BlockStateProperties.HORIZONTAL_FACING);
            StairsShape adjShape = adjState.get(BlockStateProperties.STAIRS_SHAPE);
            boolean adjTop = adjState.get(BlockStateProperties.HALF) == Half.TOP;

            if (top != adjTop) { return false; }

            if (adjShape == StairsShape.OUTER_LEFT)
            {
                return dir == adjDir && SideSkipPredicate.compareState(world, pos, side);
            }
            if (adjShape == StairsShape.OUTER_RIGHT)
            {
                return dir.rotateYCCW() == adjDir && SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedVerticalStairs)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            StairsType adjType = adjState.get(PropertyHolder.STAIRS_TYPE);

            if ((side.getAxis() == Direction.Axis.Y || side == dir || side == dir.rotateYCCW()) && adjType.isTop() != top && dir == adjDir)
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        return false;
    };

    public FramedSlabCornerBlock()
    {
        super("framed_slab_corner", BlockType.FRAMED_SLAB_CORNER);
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
        BlockState state = getDefaultState();

        Direction face = context.getFace();
        Vector3d hitPoint = Utils.fraction(context.getHitVec());
        if (face.getAxis().isHorizontal())
        {
            boolean xAxis = face.getAxis() == Direction.Axis.X;
            boolean positive = face.rotateYCCW().getAxisDirection() == Direction.AxisDirection.POSITIVE;
            double xz = xAxis ? hitPoint.getZ() : hitPoint.getX();

            Direction dir = face.getOpposite();
            if ((xz > .5D) == positive)
            {
                dir = dir.rotateY();
            }
            state = state.with(PropertyHolder.FACING_HOR, dir);
        }
        else
        {
            double x = hitPoint.getX();
            double z = hitPoint.getZ();

            Direction dir = z > .5D ? Direction.SOUTH : Direction.NORTH;
            if ((x > .5D) == (dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE)) { dir = dir.rotateY(); }
            state = state.with(PropertyHolder.FACING_HOR, dir);
        }

        state = withTop(state, face, context.getHitVec());
        return withWater(state, context.getWorld(), context.getPos());
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeBot = makeCuboidShape(0, 0, 0, 8, 8, 8);
        VoxelShape shapeTop = makeCuboidShape(0, 8, 0, 8, 16, 8);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.get(PropertyHolder.FACING_HOR);
            boolean top = state.get(PropertyHolder.TOP);
            builder.put(state, Utils.rotateShape(Direction.NORTH, dir, top ? shapeTop : shapeBot));
        }

        return builder.build();
    }
}