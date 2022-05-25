package xfacthd.framedblocks.common.util;

import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
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
        if (!((IFramedBlock) state.getBlock()).getCtmPredicate().test(state, side)) { return false; }

        if (adjState.getBlock() instanceof IFramedBlock)
        {
            if (!((IFramedBlock) adjState.getBlock()).getCtmPredicate().test(adjState, side.getOpposite()))
            {
                return false;
            }

            TileEntity te = Utils.getTileEntitySafe(world, pos.relative(side));
            if (te instanceof FramedTileEntity)
            {
                adjState = ((FramedTileEntity) te).getCamoState(side.getOpposite());
            }
        }

        //noinspection deprecation
        if (adjState.isAir()) { return false; }

        return compareState(world, pos, adjState, side, side);
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
        return compareState(world, pos, side, side, side.getOpposite());
    }

    static boolean compareState(IBlockReader world, BlockPos pos, Direction side, Direction camoSide)
    {
        return compareState(world, pos, side, camoSide, camoSide);
    }

    static boolean compareState(IBlockReader world, BlockPos pos, Direction side, Direction camoSide, Direction adjCamoSide)
    {
        TileEntity te = Utils.getTileEntitySafe(world, pos.relative(side));
        if (te instanceof FramedTileEntity)
        {
            BlockState adjState = ((FramedTileEntity) te).getCamoState(adjCamoSide);
            //noinspection deprecation
            if (adjState.isAir()) { return false; }

            return compareState(world, pos, adjState, side, camoSide);
        }
        return false;
    }

    static boolean compareState(IBlockReader world, BlockPos pos, BlockState adjState, Direction side, Direction camoSide)
    {
        TileEntity te = Utils.getTileEntitySafe(world, pos);
        if (te instanceof FramedTileEntity)
        {
            BlockState state = ((FramedTileEntity) te).getCamoState(camoSide);
            if (state == adjState)
            {
                return !state.is(BlockTags.LEAVES);
            }
            return state.isSolidRender(world, pos) && adjState.isSolidRender(world, pos.relative(side));
        }

        return false;
    }
}