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

public class FramedCornerPillarBlock extends FramedBlock
{
    public static final SideSkipPredicate SKIP_PREDICATE = (world, pos, state, adjState, side) ->
    {
        Direction dir = state.get(PropertyHolder.FACING_HOR);

        if (adjState.getBlock() == FBContent.blockFramedPanel && (side == dir || side == dir.rotateYCCW()))
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            if ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && dir == adjDir))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedCornerPillar)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            if ((side == dir && adjDir == dir.rotateYCCW()) || (side == dir.rotateYCCW() && adjDir == dir.rotateY()))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedSlabCorner)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            boolean adjTop = adjState.get(PropertyHolder.TOP);
            if ((adjTop && side == Direction.DOWN) || (!adjTop && side == Direction.UP))
            {
                return dir == adjDir && SideSkipPredicate.compareState(world, pos, side);
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedDoublePanel)
        {
            TileEntity te = world.getTileEntity(pos.offset(side));
            if (!(te instanceof FramedDoubleTileEntity)) { return false; }
            FramedDoubleTileEntity tile = (FramedDoubleTileEntity) te;

            Direction adjDir = adjState.get(PropertyHolder.FACING_NE);
            if (side == dir && (adjDir == dir.rotateY() || adjDir == dir.rotateYCCW()))
            {
                return SideSkipPredicate.compareState(world, pos, tile.getCamoState(dir.rotateYCCW()), side);
            }

            if (side == dir.rotateYCCW() && (adjDir == dir || adjDir == dir.getOpposite()))
            {
                return SideSkipPredicate.compareState(world, pos, tile.getCamoState(dir), side);
            }
        }

        if (adjState.getBlock() == FBContent.blockFramedStairs && side.getAxis() == Direction.Axis.Y)
        {
            Direction adjDir = adjState.get(BlockStateProperties.HORIZONTAL_FACING);
            StairsShape adjShape = adjState.get(BlockStateProperties.STAIRS_SHAPE);
            boolean adjTop = adjState.get(BlockStateProperties.HALF) == Half.TOP;

            if ((adjTop && side == Direction.UP) || (!adjTop && side == Direction.DOWN))
            {
                if (adjShape == StairsShape.OUTER_LEFT)
                {
                    return dir == adjDir && SideSkipPredicate.compareState(world, pos, side);
                }
                if (adjShape == StairsShape.OUTER_RIGHT)
                {
                    return dir.rotateYCCW() == adjDir && SideSkipPredicate.compareState(world, pos, side);
                }
            }
            return false;
        }

        if (adjState.getBlock() == FBContent.blockFramedVerticalStairs)
        {
            Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
            StairsType adjType = adjState.get(PropertyHolder.STAIRS_TYPE);

            if (adjType == StairsType.VERTICAL)
            {
                if ((side == dir.rotateYCCW() || side == dir) && adjDir == dir)
                {
                    return SideSkipPredicate.compareState(world, pos, side);
                }
            }
            else if (side.getAxis() == Direction.Axis.Y)
            {
                if ((side == Direction.DOWN) == adjType.isTop() && adjDir == dir)
                {
                    return SideSkipPredicate.compareState(world, pos, side);
                }
            }
            return false;
        }

        return false;
    };

    public FramedCornerPillarBlock()
    {
        super("framed_corner_pillar", BlockType.FRAMED_CORNER_PILLAR);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, BlockStateProperties.WATERLOGGED);
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

        return withWater(state, context.getWorld(), context.getPos());
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shape = makeCuboidShape(0, 0, 0, 8, 16, 8);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.get(PropertyHolder.FACING_HOR);
            builder.put(state, Utils.rotateShape(Direction.NORTH, dir, shape));
        }

        return builder.build();
    }
}