package io.github.xfacthd.framedblocks.common.block.pillar;

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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedSplitPillarSocketBlock extends FramedPillarSocketBlock implements IFramedDoubleBlockInternal {
    public FramedSplitPillarSocketBlock(Properties props) {
        super(BlockType.FRAMED_SPLIT_PILLAR_SOCKET, props);
    }

    @Override
    public FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return IFramedDoubleBlockInternal.super.newBlockEntity(pos, state);
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        BlockState stateOne;
        if (DirUtils.isY(facing)) {
            stateOne = FBContent.BLOCK_FRAMED_SLAB.value()
                    .defaultBlockState()
                    .setValue(FramedProperties.TOP, facing == Direction.UP);
        } else {
            stateOne = FBContent.BLOCK_FRAMED_PANEL.value()
                    .defaultBlockState()
                    .setValue(FramedProperties.FACING_HOR, facing);
        }
        return new DoubleBlockParts(
                stateOne,
                FBContent.BLOCK_FRAMED_HALF_PILLAR.value()
                        .defaultBlockState()
                        .setValue(BlockStateProperties.FACING, facing.getOpposite())
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state) {
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side) {
        if (side == state.getValue(BlockStateProperties.FACING)) {
            return SolidityCheck.FIRST;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        if (side == facing || (side.getAxis() != facing.getAxis() && edge == facing)) {
            return CamoGetter.FIRST;
        }
        return CamoGetter.NONE;
    }
}
