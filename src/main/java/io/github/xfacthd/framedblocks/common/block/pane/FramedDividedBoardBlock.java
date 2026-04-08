package io.github.xfacthd.framedblocks.common.block.pane;

import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public final class FramedDividedBoardBlock extends FramedDoubleBlock {
    public FramedDividedBoardBlock(BlockType blockType, Properties props) {
        super(blockType, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.FACING_DIR);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return FramedPartialBoardBlock.getStateForPlacement(this, context, true);
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        BlockState partState = FBContent.BLOCK_FRAMED_HALF_BOARD.value().defaultBlockState();
        return new DoubleBlockParts(
                partState.setValue(PropertyHolder.FACING_DIR, cmpDir),
                partState.setValue(PropertyHolder.FACING_DIR, CompoundDirection.of(
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
        return switch (cmpDir.orientation()) {
            case DOWN -> DoubleBlockTopInteractionMode.SECOND;
            case UP -> DoubleBlockTopInteractionMode.FIRST;
            default -> DoubleBlockTopInteractionMode.BOTH;
        };
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
        Direction dir = cmpDir.orientation();
        if (side == face) {
            if (edge == dir) {
                return CamoGetter.FIRST;
            }
            if (edge == dir.getOpposite()) {
                return CamoGetter.SECOND;
            }
        } else if (edge == face) {
            if (side == dir) {
                return CamoGetter.FIRST;
            }
            if (side == dir.getOpposite()) {
                return CamoGetter.SECOND;
            }
        }
        return CamoGetter.NONE;
    }

    @Override
    public BlockState getItemModelSource() {
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
        return defaultBlockState();
    }
}
