package xfacthd.framedblocks.common.data.skippreds.pane;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;
import xfacthd.framedblocks.common.data.skippreds.HalfDir;

@CullTest(BlockType.FRAMED_FLOOR_BOARD)
public final class FloorBoardSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (!Utils.isY(side))
        {
            boolean top = state.getValue(FramedProperties.TOP);
            if (adjState.getBlock() == state.getBlock())
            {
                return testAgainstFloorBoard(top, adjState);
            }
            else if (adjState.getBlock() == FBContent.BLOCK_FRAMED_CORNER_STRIP.get())
            {
                return testAgainstCornerStrip(top, adjState, side);
            }
        }

        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_FLOOR_BOARD)
    private static boolean testAgainstFloorBoard(boolean top, BlockState adjState)
    {
        return top == adjState.getValue(FramedProperties.TOP);
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_STRIP)
    private static boolean testAgainstCornerStrip(boolean top, BlockState adjState, Direction side)
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);

        return getHalfDir(top, side).isEqualTo(CornerStripSkipPredicate.getHalfDir(adjDir, adjType, side.getOpposite()));
    }



    public static HalfDir getHalfDir(boolean top, Direction side)
    {
        if (!Utils.isY(side))
        {
            return HalfDir.fromDirections(side, top ? Direction.UP : Direction.DOWN);
        }
        return HalfDir.NULL;
    }
}
