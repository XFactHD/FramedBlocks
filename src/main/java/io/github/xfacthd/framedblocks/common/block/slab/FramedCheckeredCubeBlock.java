package io.github.xfacthd.framedblocks.common.block.slab;

import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedCheckeredCubeBlock extends FramedDoubleBlock {
    public FramedCheckeredCubeBlock(Properties props) {
        super(BlockType.FRAMED_CHECKERED_CUBE, props);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.ALT_TYPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.ALT_TYPE);
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        return state;
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
        BlockState segmentState = FBContent.BLOCK_FRAMED_CHECKERED_CUBE_SEGMENT.value().defaultBlockState();
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
        return SolidityCheck.BOTH;
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState();
    }
}
