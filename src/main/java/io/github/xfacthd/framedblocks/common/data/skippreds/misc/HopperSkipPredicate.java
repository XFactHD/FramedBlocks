package io.github.xfacthd.framedblocks.common.data.skippreds.misc;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CornerTubeOrientation;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 This class is machine-generated, any manual changes to this class will be overwritten.
 */
@CullTest(BlockType.FRAMED_HOPPER)
public final class HopperSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            return switch (blockType) {
                case FRAMED_HOPPER -> testAgainstHopper(
                        side
                );
                case FRAMED_TUBE -> testAgainstTube(
                        adjState, side
                );
                case FRAMED_CORNER_TUBE -> testAgainstCornerTube(
                        adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_HOPPER)
    private static boolean testAgainstHopper(
            Direction side
    ) {
        return (MiscDirs.Hopper.isHopperSideDir(side) && MiscDirs.Hopper.isHopperSideDir(side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_TUBE)
    private static boolean testAgainstTube(
            BlockState adjState, Direction side
    ) {
        Direction.Axis adjAxis = adjState.getValue(BlockStateProperties.AXIS);
        boolean adjThick = adjState.getValue(PropertyHolder.THICK);

        return MiscDirs.Hopper.getOpeningDir(side).isEqualTo(MiscDirs.Tube.getOpeningDir(adjAxis, adjThick, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_TUBE)
    private static boolean testAgainstCornerTube(
            BlockState adjState, Direction side
    ) {
        CornerTubeOrientation adjOrientation = adjState.getValue(PropertyHolder.CORNER_TYPE_ORIENTATION);
        boolean adjThick = adjState.getValue(PropertyHolder.THICK);

        return MiscDirs.Hopper.getOpeningDir(side).isEqualTo(MiscDirs.CornerTube.getOpeningDir(adjOrientation, adjThick, side.getOpposite()));
    }
}
