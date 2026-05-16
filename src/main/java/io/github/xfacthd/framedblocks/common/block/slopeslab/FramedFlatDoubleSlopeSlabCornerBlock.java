package io.github.xfacthd.framedblocks.common.block.slopeslab;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.block.render.NullCullPredicate;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slopeslab.FramedFlatDoubleSlopeSlabCornerBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedFlatDoubleSlopeSlabCornerBlock extends FramedDoubleBlock implements SlopeToggleBlock {
    public static final NullCullPredicate NULL_CULL_PREDICATE = new NullCullPredicate(
            state -> state.getValue(PropertyHolder.TOP_HALF) == state.getValue(FramedProperties.TOP),
            state -> state.getValue(PropertyHolder.TOP_HALF) != state.getValue(FramedProperties.TOP)
    );

    public FramedFlatDoubleSlopeSlabCornerBlock(Properties props) {
        super(BlockType.FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER, props);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(PropertyHolder.TOP_HALF, false)
                .setValue(FramedProperties.ALT_SLOPE, true)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, FramedProperties.TOP, PropertyHolder.TOP_HALF);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PlacementStateBuilder.of(this, ctx)
                .withHalfFacing()
                .withTop(PropertyHolder.TOP_HALF)
                .withCustom((state, modCtx) ->
                        state.setValue(FramedProperties.TOP, modCtx.getPlayer() != null && modCtx.getPlayer().isShiftKeyDown())
                )
                .withWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        return FramedSlopeSlabBlock.rotateSlopeSlab(state, direction, mode);
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
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean altSlope = state.getValue(FramedProperties.ALT_SLOPE);

        return new DoubleBlockParts(
                FBContent.BLOCK_FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(PropertyHolder.TOP_HALF, topHalf)
                        .setValue(FramedProperties.TOP, top)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope),
                FBContent.BLOCK_FRAMED_FLAT_SLOPE_SLAB_CORNER.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.TOP_HALF, topHalf)
                        .setValue(FramedProperties.TOP, !top)
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
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);

        if (side == Direction.UP && topHalf) {
            return top ? CamoGetter.FIRST : CamoGetter.SECOND;
        } else if (side == Direction.DOWN && !topHalf) {
            return top ? CamoGetter.SECOND : CamoGetter.FIRST;
        }

        if (side == facing || side == facing.getCounterClockWise()) {
            if ((!topHalf && edge == Direction.DOWN) || (topHalf && edge == Direction.UP)) {
                return CamoGetter.FIRST;
            }
        } else if (side == facing.getOpposite() || side == facing.getClockWise()) {
            if ((!topHalf && edge == Direction.DOWN) || (topHalf && edge == Direction.UP)) {
                return top == topHalf ? CamoGetter.FIRST : CamoGetter.SECOND;
            }
        }

        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side) {
        boolean top = state.getValue(FramedProperties.TOP);
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
        if (topHalf && side == Direction.UP) {
            return top ? SolidityCheck.FIRST : SolidityCheck.SECOND;
        } else if (!topHalf && side == Direction.DOWN) {
            return top ? SolidityCheck.SECOND : SolidityCheck.FIRST;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FramedFlatDoubleSlopeSlabCornerBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return FramedSlopeSlabBlock.createStateCycleSpec(this);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return getItemModelSource();
    }
}
