package io.github.xfacthd.framedblocks.common.data.skippreds.prism;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import io.github.xfacthd.framedblocks.common.data.property.DirectionAxis;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 This class is machine-generated, any manual changes to this class will be overwritten.
 */
@CullTest(BlockType.FRAMED_PRISM)
public final class PrismSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        if (PrismDirs.Prism.testEarlyExit(dirAxis, side)) {
            return false;
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            return switch (blockType) {
                case FRAMED_PRISM -> testAgainstPrism(
                        dirAxis, adjState, side
                );
                case FRAMED_SLOPED_PRISM -> testAgainstSlopedPrism(
                        dirAxis, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_PRISM)
    private static boolean testAgainstPrism(
            DirectionAxis dirAxis, BlockState adjState, Direction side
    ) {
        DirectionAxis adjDirAxis = adjState.getValue(PropertyHolder.FACING_AXIS);
        return PrismDirs.Prism.getTriDir(dirAxis, side).isEqualTo(PrismDirs.Prism.getTriDir(adjDirAxis, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPED_PRISM)
    private static boolean testAgainstSlopedPrism(
            DirectionAxis dirAxis, BlockState adjState, Direction side
    ) {
        CompoundDirection adjCmpDir = adjState.getValue(PropertyHolder.FACING_DIR);
        return PrismDirs.Prism.getTriDir(dirAxis, side).isEqualTo(PrismDirs.SlopedPrism.getTriDir(adjCmpDir, side.getOpposite()));
    }
}
