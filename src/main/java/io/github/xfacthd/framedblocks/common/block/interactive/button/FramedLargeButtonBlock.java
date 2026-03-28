package io.github.xfacthd.framedblocks.common.block.interactive.button;

import io.github.xfacthd.framedblocks.api.model.wrapping.WrapHelper;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Set;

public class FramedLargeButtonBlock extends FramedButtonBlock {
    public static final LargeButtonStateMerger LARGE_STATE_MERGER = new LargeButtonStateMerger();
    private static final VoxelShape SHAPE_BOTTOM = box(1, 0, 1, 15, 2, 15);
    private static final VoxelShape SHAPE_BOTTOM_PRESSED = box(1, 0, 1, 15, 1, 15);
    private static final VoxelShape SHAPE_TOP = box(1, 14, 1, 15, 16, 15);
    private static final VoxelShape SHAPE_TOP_PRESSED = box(1, 15, 1, 15, 16, 15);
    private static final VoxelShape[] SHAPES_HORIZONTAL = makeHorizontalShapes();

    private FramedLargeButtonBlock(BlockType type, Properties props, BlockSetType blockSet, int pressTime) {
        super(type, props, blockSet, pressTime);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state);
    }

    @Override
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos) {
        return state.getValue(FACE) != AttachFace.WALL;
    }

    public static VoxelShape getShape(BlockState state) {
        boolean pressed = state.getValue(POWERED);
        return switch (state.getValue(FACE)) {
            case FLOOR -> pressed ? SHAPE_BOTTOM_PRESSED : SHAPE_BOTTOM;
            case CEILING -> pressed ? SHAPE_TOP_PRESSED : SHAPE_TOP;
            case WALL -> {
                int idx = state.getValue(FACING).get2DDataValue() + (pressed ? 4 : 0);
                yield SHAPES_HORIZONTAL[idx];
            }
        };
    }

    private static VoxelShape[] makeHorizontalShapes() {
        VoxelShape shape = box(1, 1, 0, 15, 15, 2);
        VoxelShape shapePressed = box(1, 1, 0, 15, 15, 1);

        return ShapeUtils.makeHorizontalRotationsWithFlag(shape, shapePressed, Direction.SOUTH);
    }

    public static FramedLargeButtonBlock largeWood(Properties props) {
        return new FramedLargeButtonBlock(
                BlockType.FRAMED_LARGE_BUTTON,
                props,
                BlockSetType.OAK,
                30
        );
    }

    public static FramedLargeButtonBlock largeStone(Properties props) {
        return new FramedLargeButtonBlock(
                BlockType.FRAMED_LARGE_STONE_BUTTON,
                props,
                BlockSetType.STONE,
                20
        );
    }

    public static final class LargeButtonStateMerger implements StateMerger {
        private LargeButtonStateMerger() { }

        @Override
        public BlockState apply(BlockState state) {
            state = WrapHelper.DEFAULT_MERGER.apply(state);

            AttachFace face = state.getValue(FramedLargeButtonBlock.FACE);
            if (face != AttachFace.WALL) {
                state = state.setValue(FramedLargeButtonBlock.FACING, Direction.NORTH);
            }
            return state;
        }

        @Override
        public Set<Property<?>> getHandledProperties(Holder<Block> block) {
            return Utils.concat(
                    WrapHelper.DEFAULT_MERGER.getHandledProperties(block),
                    Set.of(FramedLargeButtonBlock.FACING)
            );
        }
    }
}
