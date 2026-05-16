package io.github.xfacthd.framedblocks.common.block.cube;

import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.item.placement.PropertyLabels;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedTubeBlock extends FramedBlock {
    public static final String LABEL_THICK = Utils.translationKey("label", "state_cycling.property.tube.thick");

    public FramedTubeBlock(Properties props) {
        super(BlockType.FRAMED_TUBE, props);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.THICK, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.AXIS, PropertyHolder.THICK);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PlacementStateBuilder.of(this, ctx)
                .withClickedAxis()
                .withWater()
                .build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player) {
        if (player.getMainHandItem().is(FramedConstants.Objects.FRAMED_HAMMER.value())) {
            if (!level.isClientSide()) {
                state = state.setValue(PropertyHolder.THICK, !state.getValue(PropertyHolder.THICK));
                level.setBlock(pos, state, Block.UPDATE_ALL);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return state.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y;
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        return direction.cycle(state, BlockStateProperties.AXIS);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        if (axis != Direction.Axis.Y && (rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90)) {
            axis = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        }
        return state.setValue(BlockStateProperties.AXIS, axis);
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState().setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return StateCycleSpec.builder(this)
                .property(BlockStateProperties.AXIS, PropertyLabels.AXIS)
                .property(PropertyHolder.THICK, LABEL_THICK)
                .build();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return getItemModelSource();
    }
}
