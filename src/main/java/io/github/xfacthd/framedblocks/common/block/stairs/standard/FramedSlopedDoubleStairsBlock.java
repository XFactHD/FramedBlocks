package io.github.xfacthd.framedblocks.common.block.stairs.standard;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.IFramedDoubleBlockInternal;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.stairs.FramedSlopedDoubleStairsBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedSlopedDoubleStairsBlock extends FramedSlopedStairsBlock implements IFramedDoubleBlockInternal {
    public FramedSlopedDoubleStairsBlock(Properties props) {
        super(BlockType.FRAMED_SLOPED_DOUBLE_STAIRS, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        BlockUtils.removeProperty(builder, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FramedSlopedDoubleStairsBlockEntity(pos, state);
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);

        return new DoubleBlockParts(
                FBContent.BLOCK_FRAMED_SLOPED_STAIRS.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.TOP, top),
                FBContent.BLOCK_FRAMED_VERTICAL_HALF_SLOPE.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(FramedProperties.TOP, !top)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state) {
        boolean top = state.getValue(FramedProperties.TOP);
        return top ? DoubleBlockTopInteractionMode.FIRST : DoubleBlockTopInteractionMode.BOTH;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;

        if (side == facing || side == facing.getCounterClockWise() || side == dirTwo) {
            return SolidityCheck.FIRST;
        }
        return SolidityCheck.BOTH;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;

        if (side == facing || side == facing.getCounterClockWise() || side == dirTwo) {
            return CamoGetter.FIRST;
        }
        if (side == facing.getOpposite() || side == facing.getClockWise()) {
            if (edge == dirTwo) {
                return CamoGetter.FIRST;
            }
            if (edge == dirTwo.getOpposite()) {
                return CamoGetter.SECOND;
            }
            return CamoGetter.NONE;
        }
        if (side == dirTwo.getOpposite()) {
            if (edge == facing || edge == facing.getCounterClockWise()) {
                return CamoGetter.FIRST;
            }
            if (edge == facing.getOpposite() || edge == facing.getClockWise()) {
                return CamoGetter.SECOND;
            }
        }
        return CamoGetter.NONE;
    }
}
