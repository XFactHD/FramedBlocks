package xfacthd.framedblocks.common.data.skippreds.prism;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CompoundDirection;
import xfacthd.framedblocks.common.data.property.DirectionAxis;
import xfacthd.framedblocks.common.data.skippreds.CullTest;
import xfacthd.framedblocks.common.data.skippreds.HalfDir;

@CullTest(BlockType.FRAMED_INNER_SLOPED_PRISM)
public final class InnerSlopedPrismSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        Direction orientation = cmpDir.orientation();
        if (side != orientation)
        {
            return SideSkipPredicate.FULL_FACE.test(level, pos, state, adjState, side);
        }

        if (orientation.getAxis() == cmpDir.direction().getAxis())
        {
            return false;
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            return switch (type)
            {
                case FRAMED_INNER_SLOPED_PRISM -> testAgainstInnerSlopedPrism(cmpDir, adjState, side);
                case FRAMED_INNER_PRISM -> testAgainstInnerPrism(cmpDir, adjState, side);
                case FRAMED_DOUBLE_SLOPED_PRISM -> testAgainstDoubleSlopedPrism(cmpDir, adjState, side);
                case FRAMED_DOUBLE_PRISM -> testAgainstDoublePrism(cmpDir, adjState, side);
                default -> false;
            };
        }

        return false;
    }

    @CullTest.SingleTarget(BlockType.FRAMED_INNER_SLOPED_PRISM)
    private static boolean testAgainstInnerSlopedPrism(CompoundDirection cmpDir, BlockState adjState, Direction side)
    {
        CompoundDirection adjCmpDir = adjState.getValue(PropertyHolder.FACING_DIR);
        return getTriDir(cmpDir, side).isEqualTo(getTriDir(adjCmpDir, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_INNER_PRISM)
    private static boolean testAgainstInnerPrism(CompoundDirection cmpDir, BlockState adjState, Direction side)
    {
        DirectionAxis adjDirAxis = adjState.getValue(PropertyHolder.FACING_AXIS);
        return getTriDir(cmpDir, side).isEqualTo(InnerPrismSkipPredicate.getTriDir(adjDirAxis, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_SLOPED_PRISM,
            partTargets = BlockType.FRAMED_INNER_SLOPED_PRISM
    )
    private static boolean testAgainstDoubleSlopedPrism(CompoundDirection cmpDir, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstInnerSlopedPrism(cmpDir, states.getA(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_PRISM,
            partTargets = BlockType.FRAMED_INNER_PRISM
    )
    private static boolean testAgainstDoublePrism(CompoundDirection cmpDir, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstInnerPrism(cmpDir, states.getA(), side);
    }



    public static HalfDir getTriDir(CompoundDirection cmpDir, Direction side)
    {
        Direction dir = cmpDir.direction();
        Direction orientation = cmpDir.orientation();
        if (dir.getAxis() != orientation.getAxis() && side == orientation)
        {
            return HalfDir.fromDirections(side, dir);
        }
        return HalfDir.NULL;
    }
}
