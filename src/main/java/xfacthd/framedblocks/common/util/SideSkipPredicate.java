package xfacthd.framedblocks.common.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;

public interface SideSkipPredicate
{
    SideSkipPredicate FALSE = (world, pos, state, adjState, side) -> false;

    SideSkipPredicate CTM = (world, pos, state, adjState, side) ->
    {
        if (adjState.getBlock() instanceof IFramedBlock block)
        {
            if (!block.getCtmPredicate().test(adjState, side.getOpposite()))
            {
                return false;
            }

            if (world.getBlockEntity(pos.relative(side)) instanceof FramedTileEntity te)
            {
                adjState = te.getCamoState(side.getOpposite());
            }
        }

        //noinspection deprecation
        if (adjState.isAir()) { return false; }

        if (!((IFramedBlock) state.getBlock()).getCtmPredicate().test(state, side)) { return false; }

        return compareState(world, pos, adjState, side);
    };

    /**
     * Check wether the given side should be hidden in presence of the given neighbor
     * @param world The world
     * @param pos The blocks position in the world
     * @param state The blocks state
     * @param adjState The neighboring blocks state
     * @param side The side to be checked
     * @return Wether the given side should be hidden
     */
    boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side);

    static boolean compareState(IBlockReader world, BlockPos pos, Direction side)
    {
        return compareState(world, pos, side, side.getOpposite());
    }

    static boolean compareState(IBlockReader world, BlockPos pos, Direction side, Direction camoSide)
    {
        if (world.getBlockEntity(pos.relative(side)) instanceof FramedTileEntity te)
        {
            BlockState adjState = te.getCamoState(camoSide);
            //noinspection deprecation
            if (adjState.isAir()) { return false; }

            return compareState(world, pos, adjState, camoSide);
        }
        return false;
    }

    static boolean compareState(IBlockReader world, BlockPos pos, BlockState adjState, Direction side)
    {
        if (world.getBlockEntity(pos) instanceof FramedTileEntity te)
        {
            BlockState state = te.getCamoState(side);
            return state == adjState || (state.canOcclude() && adjState.canOcclude());
        }

        return false;
    }
}