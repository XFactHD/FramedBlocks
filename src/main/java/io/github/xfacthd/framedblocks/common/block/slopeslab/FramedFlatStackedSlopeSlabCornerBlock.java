package io.github.xfacthd.framedblocks.common.block.slopeslab;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
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
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedFlatStackedSlopeSlabCornerBlock extends FramedDoubleBlock implements SlopeToggleBlock {
    private final boolean corner;

    public FramedFlatStackedSlopeSlabCornerBlock(BlockType type, Properties props) {
        super(type, props);
        this.corner = type == BlockType.FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER;
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(FramedProperties.ALT_SLOPE, true)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, FramedProperties.TOP);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PlacementStateBuilder.of(this, ctx)
                .withHalfFacing()
                .withTop()
                .withWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        return switch (mode) {
            case PRIMARY -> super.rotate(state, direction, mode);
            case SECONDARY -> state.cycle(FramedProperties.TOP);
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return BlockUtils.rotate(state, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return BlockUtils.mirrorCornerBlock(state, mirror);
    }

    @Override
    public TriState shouldNotifyBlockEntityOfWrenchRotation(WrenchRotationMode mode, BlockState oldState, BlockState newState) {
        return mode.getDefaultNotifyBlockEntity();
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean altSlope = state.getValue(FramedProperties.ALT_SLOPE);

        Block topBlock;
        if (getBlockType() == BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER) {
            topBlock = FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER.value();
        } else {
            topBlock = FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER.value();
        }

        return new DoubleBlockParts(
                FBContent.BLOCK_FRAMED_SLAB.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.TOP, top),
                topBlock.defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, dir)
                        .setValue(FramedProperties.TOP, top)
                        .setValue(PropertyHolder.TOP_HALF, !top)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state) {
        if (state.getValue(FramedProperties.TOP)) {
            return DoubleBlockTopInteractionMode.FIRST;
        }
        return DoubleBlockTopInteractionMode.SECOND;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge) {
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == dirTwo || (!DirUtils.isY(side) && edge == dirTwo)) {
            return CamoGetter.FIRST;
        } else if (!corner && edge == dirTwo.getOpposite()) {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            if (side == facing || side == facing.getCounterClockWise()) {
                return CamoGetter.SECOND;
            }
        }

        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side) {
        boolean top = state.getValue(FramedProperties.TOP);
        if ((!top && side == Direction.DOWN) || (top && side == Direction.UP)) {
            return SolidityCheck.FIRST;
        }

        if (!corner) {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            if (side == facing || side == facing.getCounterClockWise()) {
                return SolidityCheck.BOTH;
            }
        }
        return SolidityCheck.NONE;
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return FramedElevatedSlopeSlabBlock.createStateCycleSpec(this);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }
}
