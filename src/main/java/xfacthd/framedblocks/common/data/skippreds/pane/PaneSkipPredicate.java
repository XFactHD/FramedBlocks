package xfacthd.framedblocks.common.data.skippreds.pane;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;

@CullTest(BlockType.FRAMED_PANE)
public final class PaneSkipPredicate implements SideSkipPredicate
{
    @Override
    @CullTest.TestTarget(BlockType.FRAMED_PANE)
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() != state.getBlock())
        {
            return false;
        }

        boolean north = state.getValue(BlockStateProperties.NORTH);
        boolean east = state.getValue(BlockStateProperties.EAST);
        boolean south = state.getValue(BlockStateProperties.SOUTH);
        boolean west = state.getValue(BlockStateProperties.WEST);

        boolean adjNorth = adjState.getValue(BlockStateProperties.NORTH);
        boolean adjEast = adjState.getValue(BlockStateProperties.EAST);
        boolean adjSouth = adjState.getValue(BlockStateProperties.SOUTH);
        boolean adjWest = adjState.getValue(BlockStateProperties.WEST);

        if (Utils.isY(side) && north == adjNorth && east == adjEast && south == adjSouth && west == adjWest)
        {
            return true;
        }

        return switch (side)
        {
            case NORTH -> north && adjSouth;
            case EAST -> east && adjWest;
            case SOUTH -> south && adjNorth;
            case WEST -> west && adjEast;
            default -> false;
        };
    }
}