package xfacthd.framedblocks.common.data.skippreds.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleCopycatBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.skippreds.CullTest;

@CullTest(BlockType.FRAMED_COLLAPSIBLE_COPYCAT_BLOCK)
public final class CollapsibleCopycatBlockSkipPredicate implements SideSkipPredicate
{
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

            if (!(level.getBlockEntity(pos) instanceof FramedCollapsibleCopycatBlockEntity be))
            {
                return false;
            }
            if (!(level.getBlockEntity(pos.relative(side)) instanceof FramedCollapsibleCopycatBlockEntity adjBe))
            {
                return false;
            }

            byte[] offsets = be.getFaceOffsets();
            byte[] adjOffsets = adjBe.getFaceOffsets();
            for (Direction face : Direction.values())
            {
                if (face.getAxis() == side.getAxis())
                {
                    continue;
                }
                if (offsets[face.ordinal()] != adjOffsets[face.ordinal()])
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
