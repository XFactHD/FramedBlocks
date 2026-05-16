package io.github.xfacthd.framedblocks.common.block.slopeslab;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.block.item.IFramedBlockItem;
import io.github.xfacthd.framedblocks.api.block.item.placement.PropertyLabels;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.api.util.text.ValuePrinter;
import io.github.xfacthd.framedblocks.api.util.text.ValuePrinters;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.item.block.FramedMirroringBlockItem;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.TriState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedSlopeSlabBlock extends FramedBlock implements SlopeToggleBlock {
    public static final Component VALUE_UPRIGHT = Utils.translate("value", "state_cycling.property.slope_slab.orientation.upright");
    public static final Component VALUE_UPSIDEDOWN = Utils.translate("value", "state_cycling.property.slope_slab.orientation.upside_down");
    public static final ValuePrinter<Boolean> PRINTER_ORIENTATION = ValuePrinter.of(top -> top ? VALUE_UPSIDEDOWN : VALUE_UPRIGHT);

    public FramedSlopeSlabBlock(Properties props) {
        super(BlockType.FRAMED_SLOPE_SLAB, props);
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
                .withTargetOrHorizontalFacing()
                .withTop(PropertyHolder.TOP_HALF)
                .withCustom((state, modCtx) ->
                        state.setValue(FramedProperties.TOP, modCtx.getPlayer() != null && modCtx.getPlayer().isShiftKeyDown())
                )
                .withWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        return rotateSlopeSlab(state, direction, mode);
    }

    @SuppressWarnings("deprecation")
    public static BlockState rotateSlopeSlab(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        return switch (mode) {
            case PRIMARY -> state.rotate(direction.toVanillaRotation());
            case SECONDARY -> state.cycle(switch (direction) {
                case CLOCKWISE -> PropertyHolder.TOP_HALF;
                case COUNTERCLOCKWISE -> FramedProperties.TOP;
            });
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return BlockUtils.rotate(state, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return BlockUtils.mirrorFaceBlock(state, mirror);
    }

    @Override
    public TriState shouldNotifyBlockEntityOfWrenchRotation(WrenchRotationMode mode, BlockState oldState, BlockState newState) {
        return mode.getDefaultNotifyBlockEntity();
    }

    @Override
    public IFramedBlockItem createBlockItem(Item.Properties props) {
        return new FramedMirroringBlockItem(this, props);
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return createStateCycleSpec(this);
    }

    public static StateCycleSpec createStateCycleSpec(Block block) {
        return StateCycleSpec.builder(block)
                .property(FramedProperties.FACING_HOR, PropertyLabels.FACING)
                .property(PropertyHolder.TOP_HALF, builder ->
                        builder.printer(PropertyLabels.HALF, ValuePrinters.HALF_BOOL)
                )
                .property(FramedProperties.TOP, builder ->
                        builder.printer(PropertyLabels.ORIENTATION, PRINTER_ORIENTATION)
                )
                .build();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return getItemModelSource();
    }
}
