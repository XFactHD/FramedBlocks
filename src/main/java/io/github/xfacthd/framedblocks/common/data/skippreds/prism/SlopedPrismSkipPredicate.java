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
@CullTest(BlockType.FRAMED_SLOPED_PRISM)
public final class SlopedPrismSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        if (PrismDirs.SlopedPrism.testEarlyExit(cmpDir, side)) {
            return false;
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            return switch (blockType) {
                case FRAMED_SLOPED_PRISM -> testAgainstSlopedPrism(
                        cmpDir, adjState, side
                );
                case FRAMED_PRISM -> testAgainstPrism(
                        cmpDir, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_SLOPED_PRISM)
    private static boolean testAgainstSlopedPrism(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        CompoundDirection adjCmpDir = adjState.getValue(PropertyHolder.FACING_DIR);
        return PrismDirs.SlopedPrism.getTriDir(cmpDir, side).isEqualTo(PrismDirs.SlopedPrism.getTriDir(adjCmpDir, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_PRISM)
    private static boolean testAgainstPrism(
            CompoundDirection cmpDir, BlockState adjState, Direction side
    ) {
        DirectionAxis adjDirAxis = adjState.getValue(PropertyHolder.FACING_AXIS);
        return PrismDirs.SlopedPrism.getTriDir(cmpDir, side).isEqualTo(PrismDirs.Prism.getTriDir(adjDirAxis, side.getOpposite()));
    }
}
