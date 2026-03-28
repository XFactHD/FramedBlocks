package io.github.xfacthd.framedblocks.common.block.stairs.vertical;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.IFramedDoubleBlockInternal;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.stairs.FramedVerticalSlicedSlopedStairsSlopeBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class FramedVerticalSlicedSlopedStairsSlopeBlock extends FramedVerticalSlopedStairsBlock implements IFramedDoubleBlockInternal {
    public FramedVerticalSlicedSlopedStairsSlopeBlock(Properties props) {
        super(BlockType.FRAMED_VERTICAL_SLICED_SLOPED_STAIRS_SLOPE, props);
    }

    @Override
    public FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FramedVerticalSlicedSlopedStairsSlopeBlockEntity(pos, state);
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        boolean altSlope = state.getValue(FramedProperties.ALT_SLOPE);

        boolean top = rot == HorizontalRotation.LEFT || rot == HorizontalRotation.DOWN;
        boolean right = rot == HorizontalRotation.LEFT || rot == HorizontalRotation.UP;
        Direction facingTwo = right ? facing.getClockWise() : facing.getCounterClockWise();

        return new DoubleBlockParts(
                FBContent.BLOCK_FRAMED_SLOPE.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facingTwo)
                        .setValue(PropertyHolder.SLOPE_TYPE, top ? SlopeType.TOP : SlopeType.BOTTOM)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope),
                FBContent.BLOCK_FRAMED_HALF_SLOPE.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facingTwo.getOpposite())
                        .setValue(FramedProperties.TOP, !top)
                        .setValue(PropertyHolder.RIGHT, right)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state) {
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        boolean top = rot == HorizontalRotation.LEFT || rot == HorizontalRotation.DOWN;
        return top ? DoubleBlockTopInteractionMode.FIRST : DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction dirTwo = rot.getOpposite().withFacing(facing);
        Direction dirThree = rot.rotate(Rotation.CLOCKWISE_90).withFacing(facing);

        if (side == facing) {
            return SolidityCheck.BOTH;
        }
        if (side == dirTwo || side == dirThree) {
            return SolidityCheck.FIRST;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        Direction dirTwo = rot.getOpposite().withFacing(facing);
        Direction dirThree = rot.rotate(Rotation.CLOCKWISE_90).withFacing(facing);

        if (side == facing) {
            if (edge == dirTwo || edge == dirThree) {
                return CamoGetter.FIRST;
            }
            if (edge == dirTwo.getOpposite() || edge == dirThree.getOpposite()) {
                return CamoGetter.SECOND;
            }
            return CamoGetter.NONE;
        }
        if (side == facing.getOpposite()) {
            if (edge == dirTwo || edge == dirThree) {
                return CamoGetter.FIRST;
            }
            return CamoGetter.NONE;
        }
        if (side == dirTwo || side == dirThree) {
            return CamoGetter.FIRST;
        }
        if (side == dirTwo.getOpposite() || side == dirThree.getOpposite()) {
            return edge == facing ? CamoGetter.SECOND : CamoGetter.NONE;
        }
        return CamoGetter.NONE;
    }
}
