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
@CullTest(BlockType.FRAMED_CORNER_TUBE)
public final class CornerTubeSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        CornerTubeOrientation orientation = state.getValue(PropertyHolder.CORNER_TYPE_ORIENTATION);
        if (MiscDirs.CornerTube.testEarlyExit(orientation, side)) {
            return false;
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            boolean thick = state.getValue(PropertyHolder.THICK);

            return switch (blockType) {
                case FRAMED_CORNER_TUBE -> testAgainstCornerTube(
                        orientation, thick, adjState, side
                );
                case FRAMED_TUBE -> testAgainstTube(
                        orientation, thick, adjState, side
                );
                case FRAMED_HOPPER -> testAgainstHopper(
                        orientation, thick, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget(BlockType.FRAMED_CORNER_TUBE)
    private static boolean testAgainstCornerTube(
            CornerTubeOrientation orientation, boolean thick, BlockState adjState, Direction side
    ) {
        CornerTubeOrientation adjOrientation = adjState.getValue(PropertyHolder.CORNER_TYPE_ORIENTATION);
        boolean adjThick = adjState.getValue(PropertyHolder.THICK);

        return MiscDirs.CornerTube.getOpeningDir(orientation, thick, side).isEqualTo(MiscDirs.CornerTube.getOpeningDir(adjOrientation, adjThick, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_TUBE)
    private static boolean testAgainstTube(
            CornerTubeOrientation orientation, boolean thick, BlockState adjState, Direction side
    ) {
        Direction.Axis adjAxis = adjState.getValue(BlockStateProperties.AXIS);
        boolean adjThick = adjState.getValue(PropertyHolder.THICK);

        return MiscDirs.CornerTube.getOpeningDir(orientation, thick, side).isEqualTo(MiscDirs.Tube.getOpeningDir(adjAxis, adjThick, side.getOpposite()));
    }

    @CullTest.TestTarget(BlockType.FRAMED_HOPPER)
    private static boolean testAgainstHopper(
            CornerTubeOrientation orientation, boolean thick, Direction side
    ) {
        return MiscDirs.CornerTube.getOpeningDir(orientation, thick, side).isEqualTo(MiscDirs.Hopper.getOpeningDir(side.getOpposite()));
    }
}
