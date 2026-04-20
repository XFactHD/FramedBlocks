package io.github.xfacthd.framedblocks.client.model.geometry.rail;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.geometry.PartConsumer;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.client.model.geometry.slope.FramedSlopeGeometry;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.util.FramedUtils;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.neoforged.neoforge.model.data.ModelData;

public class FramedRailSlopeGeometry extends FramedSlopeGeometry {
    private final BlockState railState;

    private FramedRailSlopeGeometry(GeometryFactory.Context ctx, BlockState railBlock, EnumProperty<RailShape> shapeProperty) {
        super(ctx.withState(getSlopeState(ctx.state())));
        this.railState = railBlock.setValue(shapeProperty, ctx.state().getValue(PropertyHolder.ASCENDING_RAIL_SHAPE));
    }

    @Override
    public boolean hasAdditionalUncachedParts() {
        return true;
    }

    @Override
    public void collectAdditionalPartsUncached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, FramedBlockData blockData, ModelData data) {
        BlockStateModel model = ModelUtils.getModel(railState);
        consumer.acceptAll(model, level, pos, random, railState, true, false, false, railState, null);
    }

    private static BlockState getSlopeState(BlockState state) {
        RailShape shape = state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE);
        Direction dir = FramedUtils.getDirectionFromAscendingRailShape(shape);
        boolean altSlope = state.getValue(FramedProperties.ALT_SLOPE);

        return FBContent.BLOCK_FRAMED_SLOPE.value()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, dir)
                .setValue(FramedProperties.ALT_SLOPE, altSlope);
    }

    public static FramedRailSlopeGeometry normal(GeometryFactory.Context ctx) {
        return new FramedRailSlopeGeometry(ctx, Blocks.RAIL.defaultBlockState(), BlockStateProperties.RAIL_SHAPE);
    }

    public static FramedRailSlopeGeometry powered(GeometryFactory.Context ctx) {
        boolean powered = ctx.state().getValue(BlockStateProperties.POWERED);
        return new FramedRailSlopeGeometry(
                ctx,
                Blocks.POWERED_RAIL.defaultBlockState().setValue(BlockStateProperties.POWERED, powered),
                BlockStateProperties.RAIL_SHAPE_STRAIGHT
        );
    }

    public static FramedRailSlopeGeometry detector(GeometryFactory.Context ctx) {
        boolean powered = ctx.state().getValue(BlockStateProperties.POWERED);
        return new FramedRailSlopeGeometry(
                ctx,
                Blocks.DETECTOR_RAIL.defaultBlockState().setValue(BlockStateProperties.POWERED, powered),
                BlockStateProperties.RAIL_SHAPE_STRAIGHT
        );
    }

    public static FramedRailSlopeGeometry activator(GeometryFactory.Context ctx) {
        boolean powered = ctx.state().getValue(BlockStateProperties.POWERED);
        return new FramedRailSlopeGeometry(
                ctx,
                Blocks.ACTIVATOR_RAIL.defaultBlockState().setValue(BlockStateProperties.POWERED, powered),
                BlockStateProperties.RAIL_SHAPE_STRAIGHT
        );
    }
}
