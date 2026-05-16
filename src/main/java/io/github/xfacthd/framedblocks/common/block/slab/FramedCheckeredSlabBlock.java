package io.github.xfacthd.framedblocks.common.block.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedCheckeredSlabBlock extends FramedDoubleBlock {
    public FramedCheckeredSlabBlock(Properties props) {
        super(BlockType.FRAMED_CHECKERED_SLAB, props);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(PropertyHolder.ALT_TYPE, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.TOP, PropertyHolder.ALT_TYPE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PlacementStateBuilder.of(this, ctx)
                .withTop()
                .withWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        return state.cycle(FramedProperties.TOP);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return DirUtils.isNinetyDegree(rotation) ? state.cycle(PropertyHolder.ALT_TYPE) : state;
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return mirror != Mirror.NONE ? state.cycle(PropertyHolder.ALT_TYPE) : state;
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state) {
        BlockState segmentState = FBContent.BLOCK_FRAMED_CHECKERED_SLAB_SEGMENT.value()
                .defaultBlockState()
                .setValue(FramedProperties.TOP, state.getValue(FramedProperties.TOP));
        boolean inverted = state.getValue(PropertyHolder.ALT_TYPE);
        return new DoubleBlockParts(
                segmentState.setValue(PropertyHolder.SECOND, inverted),
                segmentState.setValue(PropertyHolder.SECOND, !inverted)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state) {
        return DoubleBlockTopInteractionMode.BOTH;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge) {
        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side) {
        boolean top = state.getValue(FramedProperties.TOP);
        if ((!top && side == Direction.DOWN) || (top && side == Direction.UP)) {
            return SolidityCheck.BOTH;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState();
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return FramedSlabBlock.createStateCycleSpec(this);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState();
    }
}
