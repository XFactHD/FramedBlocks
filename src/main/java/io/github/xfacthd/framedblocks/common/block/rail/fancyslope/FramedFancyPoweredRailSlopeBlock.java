package io.github.xfacthd.framedblocks.common.block.rail.fancyslope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.IFramedDoubleBlockInternal;
import io.github.xfacthd.framedblocks.common.block.SlopeBlock;
import io.github.xfacthd.framedblocks.common.block.rail.vanillaslope.FramedPoweredRailSlopeBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import io.github.xfacthd.framedblocks.common.util.FramedUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.jspecify.annotations.Nullable;

public class FramedFancyPoweredRailSlopeBlock extends FramedPoweredRailSlopeBlock<FramedDoubleBlockEntity> implements IFramedDoubleBlockInternal, SlopeBlock.RailSlopeBlock
{
    public FramedFancyPoweredRailSlopeBlock(
            BlockType type, Properties props, boolean isPoweredRail, BlockEntityType.BlockEntitySupplier<FramedDoubleBlockEntity> beFactory
    ) {
        super(type, props, isPoweredRail, beFactory);
    }

    @Override
    public @Nullable BlockState runOcclusionTestAndGetLookupState(
            SideSkipPredicate pred, BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side
    ) {
        DoubleBlockParts partStates = getCache(adjState).getParts();
        return super.runOcclusionTestAndGetLookupState(pred, level, pos, state, partStates.stateOne(), side);
    }

    @Override
    public @Nullable BlockState getComponentBySkipPredicate(
            BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction side
    ) {
        BlockState slopeState = getCache(state).getParts().stateOne();
        if (IFramedDoubleBlock.testComponent(level, pos, slopeState, neighborState, side)) {
            return slopeState;
        }
        return null;
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state) {
        return DoubleBlockTopInteractionMode.FIRST;
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state) {
        RailShape shape = state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE);
        boolean altSlope = state.getValue(FramedProperties.ALT_SLOPE);

        BlockState railState = (switch (getBlockType()) {
            case FRAMED_FANCY_POWERED_RAIL_SLOPE -> FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL;
            case FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE -> FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL;
            default -> throw new IllegalArgumentException("Invalid block type");
        }).value().defaultBlockState();
        Direction facing = FramedUtils.getDirectionFromAscendingRailShape(shape);

        return new DoubleBlockParts(
                FBContent.BLOCK_FRAMED_SLOPE.value().defaultBlockState()
                        .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.BOTTOM)
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope),
                railState.setValue(BlockStateProperties.RAIL_SHAPE_STRAIGHT, shape)
        );
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = getFacing(state);
        if (side == facing || side == Direction.DOWN) {
            return CamoGetter.FIRST;
        } else if (side.getAxis() != facing.getAxis() && !DirUtils.isY(side)) {
            if (edge == facing || edge == Direction.DOWN) {
                return CamoGetter.FIRST;
            }
        }
        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side) {
        if (side == Direction.DOWN || side == getFacing(state)) {
            return SolidityCheck.FIRST;
        }
        return SolidityCheck.NONE;
    }
}
