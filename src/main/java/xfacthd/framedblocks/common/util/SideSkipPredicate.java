package xfacthd.framedblocks.common.util;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
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
        if (adjState.getBlock() instanceof IFramedBlock)
        {
            if (!((IFramedBlock) adjState.getBlock()).getCtmPredicate().test(adjState, side.getOpposite()))
            {
                return false;
            }

            TileEntity te = world.getTileEntity(pos.offset(side));
            if (te instanceof FramedTileEntity)
            {
                adjState = ((FramedTileEntity) te).getCamoState(side.getOpposite());
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
        TileEntity te = world.getTileEntity(pos.offset(side));
        if (te instanceof FramedTileEntity)
        {
            BlockState adjState = ((FramedTileEntity) te).getCamoState(camoSide);
            //noinspection deprecation
            if (adjState.isAir()) { return false; }

            return compareState(world, pos, adjState, camoSide);
        }
        return false;
    }

    static boolean compareState(IBlockReader world, BlockPos pos, BlockState adjState, Direction side)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof FramedTileEntity)
        {
            BlockState state = ((FramedTileEntity) te).getCamoState(side);
            return state == adjState || (state.isSolid() && adjState.isSolid());
        }

        return false;
    }
}