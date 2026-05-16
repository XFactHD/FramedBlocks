package io.github.xfacthd.framedblocks.common.block.pane;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.item.placement.PropertyLabels;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.api.util.text.ValuePrinter;
import io.github.xfacthd.framedblocks.api.util.text.ValuePrinters;
import io.github.xfacthd.framedblocks.common.block.ExtPlacementStateBuilder;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedCornerStripBlock extends FramedBlock {
    public static final ValuePrinter<SlopeType> TYPE_PRINTER = ValuePrinters.createForEnum(SlopeType.class, "corner_strip.type");

    public FramedCornerStripBlock(Properties props) {
        super(BlockType.FRAMED_CORNER_STRIP, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.SLOPE_TYPE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return ExtPlacementStateBuilder.of(this, ctx)
                .withHorizontalFacingAndSlopeType()
                .withWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        if (mode == WrenchRotationMode.SECONDARY) {
            return direction.cycle(state, PropertyHolder.SLOPE_TYPE);
        }
        return super.rotate(state, direction, mode);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return BlockUtils.rotate(state, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        if (state.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL) {
            return BlockUtils.mirrorCornerBlock(state, mirror);
        } else {
            return BlockUtils.mirrorFaceBlock(state, mirror);
        }
    }

    @Override
    public TriState shouldNotifyBlockEntityOfWrenchRotation(WrenchRotationMode mode, BlockState oldState, BlockState newState) {
        return mode.getDefaultNotifyBlockEntity();
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return StateCycleSpec.builder(this)
                .property(FramedProperties.FACING_HOR, PropertyLabels.FACING)
                .property(PropertyHolder.SLOPE_TYPE, builder -> builder
                        .printer(PropertyLabels.ORIENTATION, TYPE_PRINTER)
                )
                .build();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return getItemModelSource();
    }
}
