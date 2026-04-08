package io.github.xfacthd.framedblocks.common.block.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slab.FramedAdjustableDoubleBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public class FramedAdjustableDoublePanelBlock extends FramedAdjustableDoubleBlock {
    private FramedAdjustableDoublePanelBlock(
            BlockType type,
            Properties props,
            Function<BlockState, DoubleBlockParts> partsBuilder,
            BlockEntityType.BlockEntitySupplier<FramedAdjustableDoubleBlockEntity> beSupplier
    ) {
        super(type, props, state -> state.getValue(FramedProperties.FACING_HOR), partsBuilder, beSupplier);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PlacementStateBuilder.of(this, ctx)
                .withTargetOrHorizontalFacing()
                .build();
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state) {
        return DoubleBlockTopInteractionMode.BOTH;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side) {
        Direction facing = getFacing(state);
        if (side == facing.getOpposite()) {
            return SolidityCheck.FIRST;
        }
        if (side == facing) {
            return SolidityCheck.SECOND;
        }
        return SolidityCheck.BOTH;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = getFacing(state);
        if (side == facing.getOpposite()) {
            return CamoGetter.FIRST;
        }
        if (side == facing) {
            return CamoGetter.SECOND;
        }
        if (edge == facing.getOpposite()) {
            return CamoGetter.FIRST;
        }
        if (edge == facing) {
            return CamoGetter.SECOND;
        }
        return CamoGetter.NONE;
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state) {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    public static FramedAdjustableDoublePanelBlock standard(Properties props) {
        return new FramedAdjustableDoublePanelBlock(
                BlockType.FRAMED_ADJ_DOUBLE_PANEL,
                props,
                FramedAdjustableDoubleBlock::makeStandardParts,
                FramedAdjustableDoubleBlockEntity::standard
        );
    }

    public static FramedAdjustableDoublePanelBlock copycat(Properties props) {
        return new FramedAdjustableDoublePanelBlock(
                BlockType.FRAMED_ADJ_DOUBLE_COPYCAT_PANEL,
                props,
                FramedAdjustableDoubleBlock::makeCopycatParts,
                FramedAdjustableDoubleBlockEntity::copycat
        );
    }
}
