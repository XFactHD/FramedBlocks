package xfacthd.framedblocks.common.util;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
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
    boolean test(BlockGetter world, BlockPos pos, BlockState state, BlockState adjState, Direction side);

    static boolean compareState(BlockGetter world, BlockPos pos, Direction side)
    {
        return compareState(world, pos, side, side.getOpposite());
    }

    static boolean compareState(BlockGetter world, BlockPos pos, Direction side, Direction camoSide)
    {
        if (world.getBlockEntity(pos.relative(side)) instanceof FramedTileEntity te)
        {
            BlockState adjState = te.getCamoState(camoSide);
            if (adjState.isAir()) { return false; }

            return compareState(world, pos, adjState, camoSide);
        }
        return false;
    }

    static boolean compareState(BlockGetter world, BlockPos pos, BlockState adjState, Direction side)
    {
        if (world.getBlockEntity(pos) instanceof FramedTileEntity te)
        {
            BlockState state = te.getCamoState(side);
            return (state == adjState && !state.is(BlockTags.LEAVES)) || (state.isSolidRender(world, pos) && adjState.isSolidRender(world, pos.relative(side)));
        }

        return false;
    }
}