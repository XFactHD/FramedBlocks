package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.StairsType;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class StairsSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (SideSkipPredicate.CTM.test(world, pos, state, adjState, side)) { return true; }

        Direction dir = state.get(StairsBlock.FACING);
        StairsShape shape = state.get(StairsBlock.SHAPE);
        boolean top = state.get(StairsBlock.HALF) == Half.TOP;

        if (adjState.getBlock() == FBContent.blockFramedStairs.get())
        {
            return testAgainstStairs(world, pos, dir, shape, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedSlab.get())
        {
            return testAgainstSlab(world, pos, dir, shape, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedDoubleSlab.get())
        {
            return testAgainstDoubleSlab(world, pos, dir, shape, top, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedSlabEdge.get())
        {
            return testAgainstEdge(world, pos, dir, shape, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedPanel.get())
        {
            return testAgainstPanel(world, pos, dir, shape, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedDoublePanel.get())
        {
            return testAgainstDoublePanel(world, pos, dir, shape, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedCornerPillar.get())
        {
            return testAgainstPillar(world, pos, dir, shape, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedSlabCorner.get())
        {
            return testAgainstCorner(world, pos, dir, shape, top, adjState, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedVerticalStairs.get())
        {
            return testAgainstVerticalStairs(world, pos, dir, shape, top, adjState, side);
        }

        return false;
    }

    private boolean testAgainstStairs(IBlockReader world, BlockPos pos, Direction dir, StairsShape shape, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(StairsBlock.FACING);
        StairsShape adjShape = adjState.get(StairsBlock.SHAPE);
        if ((isStairSide(shape, dir, side) && isStairSide(adjShape, adjDir, side.getOpposite())) ||
            (isSlabSide(shape, dir, side) && isSlabSide(adjShape, adjDir, side.getOpposite()))
        )
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstSlab(IBlockReader world, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        boolean adjTop = adjState.get(PropertyHolder.TOP);
        if (top != adjTop) { return false; }
        if (!isSlabSide(shape, dir, side)) { return false; }

        return SideSkipPredicate.compareState(world, pos, side);
    }

    private boolean testAgainstDoubleSlab(IBlockReader world, BlockPos pos, Direction dir, StairsShape shape, boolean top, Direction side)
    {
        if (!isSlabSide(shape, dir, side)) { return false; }

        return SideSkipPredicate.compareState(world, pos, side, top ? Direction.UP : Direction.DOWN);
    }

    private boolean testAgainstEdge(IBlockReader world, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);
        if (top != adjTop) { return false; }

        if (adjDir == side.getOpposite())
        {
            if (!isSlabSide(shape, dir, side)) { return false; }

            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if ((top && side == Direction.DOWN) || (!top && side == Direction.UP))
        {
            if (shape != StairsShape.STRAIGHT) { return false; }

            return dir == adjDir && SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstPanel(IBlockReader world, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        if (shape != StairsShape.STRAIGHT) { return false; }
        if ((top && side != Direction.DOWN) || (!top && side != Direction.UP)) { return false; }

        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        return dir == adjDir && SideSkipPredicate.compareState(world, pos, side);
    }

    private boolean testAgainstDoublePanel(IBlockReader world, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        if (shape != StairsShape.STRAIGHT) { return false; }
        if ((top && side != Direction.DOWN) || (!top && side != Direction.UP)) { return false; }

        Direction adjDir = adjState.get(PropertyHolder.FACING_NE);
        if (dir == adjDir || dir.getOpposite() == adjDir)
        {
            return SideSkipPredicate.compareState(world, pos, side, dir);
        }
        return false;
    }

    private boolean testAgainstPillar(IBlockReader world, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        if (shape != StairsShape.OUTER_LEFT && shape != StairsShape.OUTER_RIGHT) { return false; }
        if ((top && side != Direction.DOWN) || (!top && side != Direction.UP)) { return false; }

        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        if ((shape == StairsShape.OUTER_LEFT && dir == adjDir) || (shape == StairsShape.OUTER_RIGHT && dir.rotateY() == adjDir))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstCorner(IBlockReader world, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        if (shape != StairsShape.OUTER_LEFT && shape != StairsShape.OUTER_RIGHT) { return false; }
        if ((top && side != Direction.DOWN) || (!top && side != Direction.UP)) { return false; }

        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);
        if ((shape == StairsShape.OUTER_LEFT && dir == adjDir) || (shape == StairsShape.OUTER_RIGHT && dir.rotateY() == adjDir))
        {
            return adjTop == top && SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }

    private boolean testAgainstVerticalStairs(IBlockReader world, BlockPos pos, Direction dir, StairsShape shape, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        StairsType adjType = adjState.get(PropertyHolder.STAIRS_TYPE);

        if (adjType == StairsType.VERTICAL)
        {
            if (((side == Direction.DOWN && top) || (side == Direction.UP && !top)) &&
                ((shape == StairsShape.INNER_LEFT && adjDir == dir) || (shape == StairsShape.INNER_RIGHT && adjDir == dir.rotateY()))
            )
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
        }
        else if (adjType.isTop() != top)
        {
            if ((side == dir.rotateY() && adjDir == dir) || (side == dir.rotateYCCW() && adjDir == dir.rotateY()))
            {
                return SideSkipPredicate.compareState(world, pos, side);
            }
        }
        return false;
    }



    public static boolean isStairSide(StairsShape shape, Direction dir, Direction side)
    {
        if (shape == StairsShape.STRAIGHT) { return side == dir.rotateY() || side == dir.rotateYCCW(); }

        if (shape == StairsShape.INNER_LEFT) { return side == dir.getOpposite() || side == dir.rotateY(); }
        if (shape == StairsShape.INNER_RIGHT) { return side == dir.getOpposite() || side == dir.rotateYCCW(); }

        if (shape == StairsShape.OUTER_LEFT)  { return side == dir || side == dir.rotateYCCW(); }
        if (shape == StairsShape.OUTER_RIGHT) { return side == dir || side == dir.rotateY(); }

        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isSlabSide(StairsShape shape, Direction dir, Direction side)
    {
        if (shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT) { return false; }

        if (shape == StairsShape.STRAIGHT) { return side == dir.getOpposite(); }

        if (shape == StairsShape.OUTER_LEFT)  { return side == dir.getOpposite() || side == dir.rotateY(); }
        if (shape == StairsShape.OUTER_RIGHT) { return side == dir.getOpposite() || side == dir.rotateYCCW(); }

        return false;
    }
}