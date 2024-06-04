package xfacthd.framedblocks.common.data.skippreds.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.blockentity.special.ICollapsibleCopycatBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.skippreds.CullTest;

@CullTest(BlockType.FRAMED_COLLAPSIBLE_COPYCAT_BLOCK)
public final class CollapsibleCopycatBlockSkipPredicate implements SideSkipPredicate
{
    private static final Direction[] DIRECTIONS = Direction.values();

    @Override
    @CullTest.TestTarget(BlockType.FRAMED_COLLAPSIBLE_COPYCAT_BLOCK)
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() == state.getBlock())
        {
            int solid = state.getValue(PropertyHolder.SOLID_FACES);
            if ((solid & (1 << side.ordinal())) == 0)
            {
                return false;
            }

            int adjSolid = adjState.getValue(PropertyHolder.SOLID_FACES);
            if ((adjSolid & (1 << side.getOpposite().ordinal())) == 0)
            {
                return false;
            }

            if (!(level.getBlockEntity(pos) instanceof ICollapsibleCopycatBlockEntity be))
            {
                return false;
            }
            if (!(level.getBlockEntity(pos.relative(side)) instanceof ICollapsibleCopycatBlockEntity adjBe))
            {
                return false;
            }

            for (Direction face : DIRECTIONS)
            {
                if (face.getAxis() == side.getAxis())
                {
                    continue;
                }
                int offset = be.getFaceOffset(state, face);
                int adjOffset = adjBe.getFaceOffset(adjState, face);
                if (offset != adjOffset)
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
