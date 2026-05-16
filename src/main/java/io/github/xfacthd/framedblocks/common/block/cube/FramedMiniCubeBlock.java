package io.github.xfacthd.framedblocks.common.block.cube;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.item.placement.PropertyLabels;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.model.wrapping.WrapHelper;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.api.util.text.ValuePrinters;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.Holder;
import net.minecraft.util.TriState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public class FramedMiniCubeBlock extends FramedBlock {
    public FramedMiniCubeBlock(Properties props) {
        super(BlockType.FRAMED_MINI_CUBE, props);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.ROTATION_16, FramedProperties.TOP);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PlacementStateBuilder.of(this, ctx)
                .withCustom((state, modCtx) -> state.setValue(
                        BlockStateProperties.ROTATION_16,
                        RotationSegment.convertToSegment(modCtx.getRotation() + 180F)
                ))
                .withTop()
                .withWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        return direction.rotateRot16(state);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        int rot = state.getValue(BlockStateProperties.ROTATION_16);
        return state.setValue(BlockStateProperties.ROTATION_16, rotation.rotate(rot, 16));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        int rot = state.getValue(BlockStateProperties.ROTATION_16);
        return state.setValue(BlockStateProperties.ROTATION_16, mirror.mirror(rot, 16));
    }

    @Override
    public TriState shouldNotifyBlockEntityOfWrenchRotation(WrenchRotationMode mode, BlockState oldState, BlockState newState) {
        boolean quadrantChanged = DirUtils.isDifferentRot16Quadrant(oldState, newState);
        return quadrantChanged ? TriState.DEFAULT : TriState.FALSE;
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState();
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return StateCycleSpec.builder(this)
                .property(BlockStateProperties.ROTATION_16, PropertyLabels.ROTATION)
                .property(FramedProperties.TOP, builder -> builder.printer(PropertyLabels.HALF, ValuePrinters.HALF_BOOL))
                .reverseCycleOrder()
                .build();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState();
    }

    public static final class MiniCubeStateMerger implements StateMerger {
        public static final MiniCubeStateMerger INSTANCE = new MiniCubeStateMerger();

        private MiniCubeStateMerger() { }

        @Override
        public BlockState apply(BlockState state) {
            state = WrapHelper.DEFAULT_MERGER.apply(state);
            int rot = state.getValue(BlockStateProperties.ROTATION_16);
            if (rot > 3) {
                state = state.setValue(BlockStateProperties.ROTATION_16, rot % 4);
            }
            return state;
        }

        @Override
        public Set<Property<?>> getHandledProperties(Holder<Block> block) {
            return Utils.concat(
                    WrapHelper.DEFAULT_MERGER.getHandledProperties(block),
                    Set.of(BlockStateProperties.ROTATION_16)
            );
        }
    }
}
