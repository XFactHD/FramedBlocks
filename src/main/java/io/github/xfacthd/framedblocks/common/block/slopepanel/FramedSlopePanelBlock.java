package io.github.xfacthd.framedblocks.common.block.slopepanel;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.block.item.placement.PropertyLabels;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.block.ExtPlacementStateBuilder;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedSlopePanelBlock extends FramedBlock implements SlopeToggleBlock {
    public FramedSlopePanelBlock(Properties props) {
        super(BlockType.FRAMED_SLOPE_PANEL, props);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.FRONT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.ROTATION, PropertyHolder.FRONT);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return getStateForPlacement(this, context);
    }

    public static @Nullable BlockState getStateForPlacement(Block block, BlockPlaceContext context) {
        return ExtPlacementStateBuilder.of(block, context)
                .withHorizontalFacing()
                .withCrossOrSideRotation()
                .withFront()
                .withWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        return switch (mode) {
            case PRIMARY -> super.rotate(state, direction, mode);
            case SECONDARY -> HorizontalRotation.rotate(state, direction);
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return BlockUtils.rotate(state, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return mirrorPanel(state, mirror);
    }

    public static BlockState mirrorPanel(BlockState state, Mirror mirror) {
        state = BlockUtils.mirrorFaceBlock(state, mirror);

        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        if (mirror != Mirror.NONE && !rot.isVertical()) {
            state = state.setValue(PropertyHolder.ROTATION, rot.getOpposite());
        }

        return state;
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
        return createStateCycleSpec(this);
    }

    public static StateCycleSpec createStateCycleSpec(Block block) {
        return StateCycleSpec.builder(block)
                .property(FramedProperties.FACING_HOR, PropertyLabels.FACING)
                .property(PropertyHolder.FRONT, builder ->
                        builder.printer(PropertyLabels.HALF, PropertyHolder.Printers.FRONT_PRINTER)
                )
                .property(PropertyHolder.ROTATION, builder ->
                        builder.values(HorizontalRotation.CYCLE_ORDER).printer(PropertyLabels.ORIENTATION)
                )
                .build();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return getItemModelSource();
    }
}
