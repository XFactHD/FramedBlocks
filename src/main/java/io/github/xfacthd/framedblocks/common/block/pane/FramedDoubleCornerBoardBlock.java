package io.github.xfacthd.framedblocks.common.block.pane;

import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public final class FramedDoubleCornerBoardBlock extends FramedDoubleBlock {
    public FramedDoubleCornerBoardBlock(BlockType blockType, Properties props) {
        super(blockType, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.FACING_DIR);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return FramedPartialBoardBlock.getStateForPlacement(this, context, false);
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return new DoubleBlockParts(
                FBContent.BLOCK_FRAMED_INNER_CORNER_BOARD.value()
                        .defaultBlockState()
                        .setValue(PropertyHolder.FACING_DIR, cmpDir),
                FBContent.BLOCK_FRAMED_CORNER_BOARD.value()
                        .defaultBlockState()
                        .setValue(PropertyHolder.FACING_DIR, CompoundDirection.of(
                                cmpDir.direction(),
                                cmpDir.orientation().getOpposite()
                        ))
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        if (DirUtils.isY(cmpDir.direction())) {
            return DoubleBlockTopInteractionMode.BOTH;
        }
        if (cmpDir.orientation() == Direction.UP || FramedPartialBoardBlock.getCornerDirTwo(cmpDir) == Direction.UP) {
            return DoubleBlockTopInteractionMode.FIRST;
        }
        return DoubleBlockTopInteractionMode.BOTH;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        if (side == cmpDir.direction()) {
            return SolidityCheck.BOTH;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge) {
        if (edge == null) {
            return CamoGetter.NONE;
        }

        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        Direction face = cmpDir.direction();
        Direction dirOne = cmpDir.orientation();
        Direction dirTwo = FramedPartialBoardBlock.getCornerDirTwo(cmpDir);
        if (side == face) {
            if (edge == dirOne || edge == dirTwo) {
                return CamoGetter.FIRST;
            }
        } else if (edge == face) {
            if (side == dirOne || side == dirTwo) {
                return CamoGetter.FIRST;
            }
        }
        return CamoGetter.NONE;
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        if (mode == WrenchRotationMode.SECONDARY) {
            CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
            return state.setValue(PropertyHolder.FACING_DIR, cmpDir.rotateOrientation(direction));
        }
        return super.rotate(state, direction, mode);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return state.setValue(PropertyHolder.FACING_DIR, cmpDir.rotate(rotation));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return state.setValue(PropertyHolder.FACING_DIR, cmpDir.mirror(mirror));
    }

    @Override
    @SuppressWarnings("JavaExistingMethodCanBeUsed")
    public TriState shouldNotifyBlockEntityOfWrenchRotation(WrenchRotationMode mode, BlockState oldState, BlockState newState) {
        if (mode == WrenchRotationMode.PRIMARY || DirUtils.isY(oldState.getValue(PropertyHolder.FACING_DIR).direction())) {
            return TriState.DEFAULT;
        }
        return TriState.FALSE;
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState().setValue(PropertyHolder.FACING_DIR, CompoundDirection.of(Direction.DOWN, Direction.SOUTH));
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return FramedPartialBoardBlock.createStateCycleSpec(this);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState();
    }
}
