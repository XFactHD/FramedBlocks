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

@CullTest(BlockType.FRAMED_PRISM)
public final class PrismSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        Direction dir = dirAxis.direction();
        Direction.Axis axis = dirAxis.axis();
        if (side == dir.getOpposite() || axis == dir.getAxis() || side.getAxis() != axis)
        {
            return false;
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            return switch (type)
            {
                case FRAMED_PRISM -> testAgainstPrism(dirAxis, adjState, side);
                case FRAMED_SLOPED_PRISM -> testAgainstSlopedPrism(dirAxis, adjState, side);
                case FRAMED_DOUBLE_PRISM -> testAgainstDoublePrism(dirAxis, adjState, side);
                case FRAMED_DOUBLE_SLOPED_PRISM -> testAgainstDoubleSlopedPrism(dirAxis, adjState, side);
                default -> false;
            };
        }
        return false;
    }

    @CullTest.SingleTarget(BlockType.FRAMED_PRISM)
    private static boolean testAgainstPrism(DirectionAxis dirAxis, BlockState adjState, Direction side)
    {
        DirectionAxis adjDirAxis = adjState.getValue(PropertyHolder.FACING_AXIS);
        return getTriDir(dirAxis, side).isEqualTo(getTriDir(adjDirAxis, side.getOpposite()));
    }

    @CullTest.SingleTarget(BlockType.FRAMED_SLOPED_PRISM)
    private static boolean testAgainstSlopedPrism(DirectionAxis dirAxis, BlockState adjState, Direction side)
    {
        CompoundDirection adjCmpDir = adjState.getValue(PropertyHolder.FACING_DIR);
        return getTriDir(dirAxis, side).isEqualTo(SlopedPrismSkipPredicate.getTriDir(adjCmpDir, side.getOpposite()));
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_PRISM,
            partTargets = BlockType.FRAMED_PRISM
    )
    private static boolean testAgainstDoublePrism(DirectionAxis dirAxis, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPrism(dirAxis, states.getA(), side);
    }

    @CullTest.DoubleTarget(
            value = BlockType.FRAMED_DOUBLE_SLOPED_PRISM,
            partTargets = BlockType.FRAMED_SLOPED_PRISM
    )
    private static boolean testAgainstDoubleSlopedPrism(DirectionAxis dirAxis, BlockState adjState, Direction side)
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopedPrism(dirAxis, states.getA(), side);
    }



    public static HalfDir getTriDir(DirectionAxis dirAxis, Direction side)
    {
        Direction dir = dirAxis.direction();
        Direction.Axis axis = dirAxis.axis();
        if (dir.getAxis() != axis && side.getAxis() == axis)
        {
            return HalfDir.fromDirections(side, dir);
        }
        return HalfDir.NULL;
    }
}
