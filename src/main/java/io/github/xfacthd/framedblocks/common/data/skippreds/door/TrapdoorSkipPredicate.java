package io.github.xfacthd.framedblocks.common.data.skippreds.door;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.Half;

/// This class is machine-generated, any manual changes to this class will be overwritten.
@CullTest({
        BlockType.FRAMED_TRAPDOOR,
        BlockType.FRAMED_IRON_TRAPDOOR
})
public final class TrapdoorSkipPredicate implements SideSkipPredicate {
    public static final TrapdoorSkipPredicate INSTANCE = new TrapdoorSkipPredicate();

    private TrapdoorSkipPredicate() { }

    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType) {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            Half half = state.getValue(BlockStateProperties.HALF);
            boolean open = state.getValue(BlockStateProperties.OPEN);

            return switch (blockType) {
                case FRAMED_TRAPDOOR,
                     FRAMED_IRON_TRAPDOOR -> testAgainstTrapdoor(
                        dir, half, open, adjState, side
                );
                case FRAMED_DOOR,
                     FRAMED_IRON_DOOR -> testAgainstDoor(
                        dir, half, open, adjState, side
                );
                case FRAMED_GATE,
                     FRAMED_IRON_GATE -> testAgainstGate(
                        dir, half, open, adjState, side
                );
                default -> false;
            };
        }
        return false;
    }

    @CullTest.TestTarget({
            BlockType.FRAMED_TRAPDOOR,
            BlockType.FRAMED_IRON_TRAPDOOR
    })
    private static boolean testAgainstTrapdoor(
            Direction dir, Half half, boolean open, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        Half adjHalf = adjState.getValue(BlockStateProperties.HALF);
        boolean adjOpen = adjState.getValue(BlockStateProperties.OPEN);

        return DoorDirs.Trapdoor.getDoorEdgeDir(dir, half, open, side).isEqualTo(DoorDirs.Trapdoor.getDoorEdgeDir(adjDir, adjHalf, adjOpen, side.getOpposite()));
    }

    @CullTest.TestTarget({
            BlockType.FRAMED_DOOR,
            BlockType.FRAMED_IRON_DOOR
    })
    private static boolean testAgainstDoor(
            Direction dir, Half half, boolean open, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        DoorHingeSide adjHinge = adjState.getValue(BlockStateProperties.DOOR_HINGE);
        boolean adjOpen = adjState.getValue(BlockStateProperties.OPEN);

        return DoorDirs.Trapdoor.getDoorEdgeDir(dir, half, open, side).isEqualTo(DoorDirs.Door.getDoorEdgeDir(adjDir, adjHinge, adjOpen, side.getOpposite()));
    }

    @CullTest.TestTarget({
            BlockType.FRAMED_GATE,
            BlockType.FRAMED_IRON_GATE
    })
    private static boolean testAgainstGate(
            Direction dir, Half half, boolean open, BlockState adjState, Direction side
    ) {
        Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        DoorHingeSide adjHinge = adjState.getValue(BlockStateProperties.DOOR_HINGE);
        boolean adjOpen = adjState.getValue(BlockStateProperties.OPEN);

        return DoorDirs.Trapdoor.getDoorEdgeDir(dir, half, open, side).isEqualTo(DoorDirs.Gate.getDoorEdgeDir(adjDir, adjHinge, adjOpen, side.getOpposite()));
    }
}
