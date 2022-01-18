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

public class HalfStairsSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.get(PropertyHolder.FACING_HOR);
        boolean top = state.get(PropertyHolder.TOP);
        boolean right = state.get(PropertyHolder.RIGHT);

        Direction stairFace = right ? dir.rotateY() : dir.rotateYCCW();
        Direction baseFace = top ? Direction.UP : Direction.DOWN;

        if (adjState.matchesBlock(FBContent.blockFramedHalfStairs.get()))
        {
            return testAgainstHalfStairs(world, pos, dir, top, right, stairFace, adjState, side);
        }

        if (side == stairFace)
        {
            if (adjState.matchesBlock(FBContent.blockFramedStairs.get()))
            {
                return testAgainstStairs(world, pos, top, adjState, side);
            }
            else if (adjState.matchesBlock(FBContent.blockFramedVerticalStairs.get()))
            {
                return testAgainstVerticalStairs(world, pos, dir, top, right, adjState, side);
            }
        }
        else
        {
            if (adjState.matchesBlock(FBContent.blockFramedSlabEdge.get()))
            {
                return testAgainstSlabEdge(world, pos, dir, top, right, baseFace, adjState, side);
            }
            else if (adjState.matchesBlock(FBContent.blockFramedCornerPillar.get()))
            {
                return testAgainstCornerPillar(world, pos, dir, right, baseFace, adjState, side);
            }
            else if (adjState.matchesBlock(FBContent.blockFramedSlabCorner.get()))
            {
                return testAgainstSlabCorner(world, pos, dir, top, right, baseFace, adjState, side);
            }
            else if (adjState.matchesBlock(FBContent.blockFramedPanel.get()))
            {
                return testAgainstPanel(world, pos, dir, right, baseFace, adjState, side);
            }
            else if (adjState.matchesBlock(FBContent.blockFramedDoublePanel.get()))
            {
                return testAgainstDoublePanel(world, pos, dir, stairFace, baseFace, adjState, side);
            }
        }

        return false;
    }

    private static boolean testAgainstHalfStairs(IBlockReader world, BlockPos pos, Direction dir, boolean top, boolean right, Direction stairFace, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);
        boolean adjRight = adjState.get(PropertyHolder.RIGHT);

        if (side == stairFace)
        {
            return adjDir == dir && adjTop == top && adjRight != right && SideSkipPredicate.compareState(world, pos, side);
        }
        else if (side.getAxis() == Direction.Axis.Y)
        {
            return adjDir == dir && adjTop != top && adjRight == right && SideSkipPredicate.compareState(world, pos, side);
        }
        else if (side == dir)
        {
            return adjDir == dir.getOpposite() && adjRight != right && SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstStairs(IBlockReader world, BlockPos pos, boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(StairsBlock.FACING);
        StairsShape adjShape = adjState.get(StairsBlock.SHAPE);
        boolean adjTop = adjState.get(StairsBlock.HALF) == Half.TOP;

        return top == adjTop && StairsSkipPredicate.isStairSide(adjShape, adjDir, side.getOpposite()) && SideSkipPredicate.compareState(world, pos, side);
    }

    private static boolean testAgainstVerticalStairs(IBlockReader world, BlockPos pos, Direction dir, boolean top, boolean right, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        StairsType adjType = adjState.get(PropertyHolder.STAIRS_TYPE);

        if ((right && adjDir == dir) || (!right && adjDir == dir.rotateY()))
        {
            return adjType.isTop() != top && SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlabEdge(IBlockReader world, BlockPos pos, Direction dir, boolean top, boolean right, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace && side != dir.getOpposite()) { return false; }

        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);

        if ((right && adjDir == dir.rotateY()) || (!right && adjDir == dir.rotateYCCW()))
        {
            return (adjTop == top) == (side == dir.getOpposite()) && SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstCornerPillar(IBlockReader world, BlockPos pos, Direction dir, boolean right, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace.getOpposite() && side != dir) { return false; }

        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);

        if (side == dir && ((right && adjDir == dir.getOpposite()) || (!right && adjDir == dir.rotateYCCW())))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if (side == baseFace.getOpposite() && ((right && adjDir == dir.rotateY()) || (!right && adjDir == dir)))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstSlabCorner(IBlockReader world, BlockPos pos, Direction dir, boolean top, boolean right, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace.getOpposite() && side != dir.getOpposite()) { return false; }

        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);
        boolean adjTop = adjState.get(PropertyHolder.TOP);

        if ((right && adjDir == dir.rotateY()) || (!right && adjDir == dir))
        {
            return adjTop == top && SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstPanel(IBlockReader world, BlockPos pos, Direction dir, boolean right, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace && side != dir) { return false; }

        Direction adjDir = adjState.get(PropertyHolder.FACING_HOR);

        if ((right && adjDir == dir.rotateY()) || (!right && adjDir == dir.rotateYCCW()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static boolean testAgainstDoublePanel(IBlockReader world, BlockPos pos, Direction dir, Direction stairFace, Direction baseFace, BlockState adjState, Direction side)
    {
        if (side != baseFace && side != dir) { return false; }

        Direction adjDir = adjState.get(PropertyHolder.FACING_NE);

        return adjDir.getAxis() != dir.getAxis() && SideSkipPredicate.compareState(world, pos, side, stairFace);
    }
}
