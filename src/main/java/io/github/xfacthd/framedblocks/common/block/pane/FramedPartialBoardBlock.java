package io.github.xfacthd.framedblocks.common.block.pane;

import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public final class FramedPartialBoardBlock extends FramedBlock {
    private final boolean half;

    public FramedPartialBoardBlock(BlockType type, Properties props) {
        super(type, props);
        this.half = type == BlockType.FRAMED_HALF_BOARD;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.FACING_DIR);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return getStateForPlacement(this, context, half);
    }

    public static @Nullable BlockState getStateForPlacement(Block block, BlockPlaceContext context, boolean half) {
        return PlacementStateBuilder.of(block, context)
                .withCustom((state, modCtx) -> {
                    Direction face = modCtx.getClickedFace().getOpposite();
                    Direction orientation;
                    if (half) {
                        orientation = DirUtils.getDirByCross(face, modCtx.getClickLocation());
                    } else {
                        orientation = DirUtils.getDirByCorner(face, modCtx.getClickLocation());
                    }
                    return state.setValue(PropertyHolder.FACING_DIR, CompoundDirection.of(face, orientation));
                })
                .withWater()
                .build();
    }

    @Override
    public BlockState getItemModelSource() {
        if (getBlockType() == BlockType.FRAMED_INNER_CORNER_BOARD) {
            return defaultBlockState().setValue(PropertyHolder.FACING_DIR, CompoundDirection.of(Direction.DOWN, Direction.SOUTH));
        }
        return defaultBlockState();
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        if (!DirUtils.isY(cmpDir.direction())) {
            return cmpDir.direction();
        }
        if (!DirUtils.isY(cmpDir.orientation())) {
            return cmpDir.orientation();
        }
        return Direction.NORTH;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState().setValue(PropertyHolder.FACING_DIR, CompoundDirection.of(Direction.DOWN, Direction.WEST));
    }

    public static Direction getCornerDirTwo(CompoundDirection cmpDir) {
        Direction face = cmpDir.direction();
        Direction dirOne = cmpDir.orientation();
        if (DirUtils.isY(face)) {
            return dirOne.getCounterClockWise();
        } else if (DirUtils.isPositive(face)) {
            return dirOne.getClockWise(face.getAxis());
        } else {
            return dirOne.getCounterClockWise(face.getAxis());
        }
    }
}
