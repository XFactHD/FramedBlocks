package io.github.xfacthd.framedblocks.common.block.stairs.standard;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.IFramedDoubleBlockInternal;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.stairs.FramedSlicedSlopedStairsSlopeBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class FramedSlicedSlopedStairsSlopeBlock extends FramedSlopedStairsBlock implements IFramedDoubleBlockInternal {
    public FramedSlicedSlopedStairsSlopeBlock(Properties props) {
        super(BlockType.FRAMED_SLICED_SLOPED_STAIRS_SLOPE, props);
    }

    @Override
    public FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FramedSlicedSlopedStairsSlopeBlockEntity(pos, state);
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);

        return new DoubleBlockParts(
                FBContent.BLOCK_FRAMED_SLOPE.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.HORIZONTAL),
                FBContent.BLOCK_FRAMED_VERTICAL_HALF_SLOPE.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(FramedProperties.TOP, top)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state) {
        return DoubleBlockTopInteractionMode.BOTH;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;

        if (side == facing || side == facing.getCounterClockWise()) {
            return SolidityCheck.FIRST;
        }
        if (side == dirTwo) {
            return SolidityCheck.BOTH;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;

        if (side == facing || side == facing.getCounterClockWise()) {
            return CamoGetter.FIRST;
        }
        if (side == facing.getOpposite() || side == facing.getClockWise()) {
            return edge == dirTwo ? CamoGetter.SECOND : CamoGetter.NONE;
        }
        if (side == dirTwo) {
            if (edge == facing || edge == facing.getCounterClockWise()) {
                return CamoGetter.FIRST;
            }
            if (edge == facing.getOpposite() || edge == facing.getClockWise()) {
                return CamoGetter.SECOND;
            }
            return CamoGetter.NONE;
        }
        if (side == dirTwo.getOpposite() && (edge == facing || edge == facing.getCounterClockWise())) {
            return CamoGetter.FIRST;
        }
        return CamoGetter.NONE;
    }
}
