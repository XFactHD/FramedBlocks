package io.github.xfacthd.framedblocks.common.block.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.IFramedDoubleBlockInternal;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.PillarConnection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedStackedPyramidSlabBlock extends FramedConnectingPyramidBlock implements IFramedDoubleBlockInternal {
    public FramedStackedPyramidSlabBlock(BlockType type, Properties props) {
        super(type, props);
    }

    @Override
    public FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return IFramedDoubleBlockInternal.super.newBlockEntity(pos, state);
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        PillarConnection connection = state.getValue(PropertyHolder.PILLAR_CONNECTION);
        boolean altSlope = state.getValue(FramedProperties.ALT_SLOPE);
        BlockState stateOne;
        if (DirUtils.isY(facing)) {
            stateOne = FBContent.BLOCK_FRAMED_SLAB.value().defaultBlockState()
                    .setValue(FramedProperties.TOP, facing == Direction.DOWN);
        } else {
            stateOne = FBContent.BLOCK_FRAMED_PANEL.value().defaultBlockState()
                    .setValue(FramedProperties.FACING_HOR, facing.getOpposite());
        }
        return new DoubleBlockParts(
                stateOne,
                FBContent.BLOCK_FRAMED_UPPER_PYRAMID_SLAB.value().defaultBlockState()
                        .setValue(BlockStateProperties.FACING, facing)
                        .setValue(PropertyHolder.PILLAR_CONNECTION, connection)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state) {
        return switch (state.getValue(BlockStateProperties.FACING)) {
            case UP -> DoubleBlockTopInteractionMode.SECOND;
            case DOWN -> DoubleBlockTopInteractionMode.FIRST;
            default -> DoubleBlockTopInteractionMode.BOTH;
        };
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        return side == facing.getOpposite() ? SolidityCheck.FIRST : SolidityCheck.NONE;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        if (side == facing.getOpposite() || (side != facing && edge == facing.getOpposite())) {
            return CamoGetter.FIRST;
        }
        return CamoGetter.NONE;
    }
}
