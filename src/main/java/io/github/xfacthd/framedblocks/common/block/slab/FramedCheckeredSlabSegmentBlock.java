package io.github.xfacthd.framedblocks.common.block.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.util.TriState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedCheckeredSlabSegmentBlock extends FramedBlock {
    public FramedCheckeredSlabSegmentBlock(Properties props) {
        super(BlockType.FRAMED_CHECKERED_SLAB_SEGMENT, props);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(PropertyHolder.SECOND, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.TOP, PropertyHolder.SECOND);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PlacementStateBuilder.of(this, ctx)
                .withTop()
                .withCustom((state, modCtx) -> state.setValue(
                        PropertyHolder.SECOND, DirUtils.isX(modCtx.getHorizontalDirection())
                ))
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
        return DirUtils.isNinetyDegree(rotation) ? state.cycle(PropertyHolder.SECOND) : state;
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return mirror != Mirror.NONE ? state.cycle(PropertyHolder.SECOND) : state;
    }

    @Override
    public TriState shouldNotifyBlockEntityOfWrenchRotation(WrenchRotationMode mode, BlockState oldState, BlockState newState) {
        return mode.getDefaultNotifyBlockEntity();
    }

    @Override
    public @Nullable BlockState getItemModelSource() {
        return null;
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return StateCycleSpec.UNSUPPORTED;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return state;
    }
}
