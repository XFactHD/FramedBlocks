package io.github.xfacthd.framedblocks.common.block.pillar;

import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.overlay.AxisOverlayCarrier;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.block.PillarLikeBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.property.PillarConnection;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedPillarBlock extends FramedBlock implements PillarLikeBlock, AxisOverlayCarrier {
    private final PillarConnection pillarConnection;

    public FramedPillarBlock(BlockType blockType, Properties props) {
        super(blockType, props);
        this.pillarConnection = switch (blockType) {
            case FRAMED_POST -> PillarConnection.POST;
            case FRAMED_PILLAR -> PillarConnection.PILLAR;
            default -> throw new IllegalArgumentException("Unexpected BlockType in FramedPillarBlock: " + blockType);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.AXIS);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PlacementStateBuilder.of(this, ctx)
                .withCustom((state, modCtx) ->
                        state.setValue(BlockStateProperties.AXIS, modCtx.getClickedFace().getAxis())
                )
                .withWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        return state.cycle(BlockStateProperties.AXIS);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        if (axis != Direction.Axis.Y && DirUtils.isNinetyDegree(rotation)) {
            axis = DirUtils.getPerpendicularAxis(axis, Direction.Axis.Y);
            return state.setValue(BlockStateProperties.AXIS, axis);
        }
        return state;
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState().setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState().setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
    }

    @Override
    public PillarConnection getPillarConnection(BlockState state, Direction side) {
        return side.getAxis() == state.getValue(BlockStateProperties.AXIS) ? pillarConnection : PillarConnection.NONE;
    }

    @Override
    public Direction.Axis getAxis(BlockState state) {
        return state.getValue(BlockStateProperties.AXIS);
    }
}
