package io.github.xfacthd.framedblocks.common.block.stairs.standard;

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
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import org.jspecify.annotations.Nullable;

public class FramedSlicedStairsSlabBlock extends FramedStairsBlock implements IFramedDoubleBlockInternal
{
    public FramedSlicedStairsSlabBlock(Properties props)
    {
        super(BlockType.FRAMED_SLICED_STAIRS_SLAB, props);
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        if (state.getValue(HALF) != Half.TOP)
        {
            return DoubleBlockTopInteractionMode.EITHER;
        }
        return DoubleBlockTopInteractionMode.FIRST;
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state)
    {
        Direction dir = state.getValue(FACING);
        boolean top = state.getValue(HALF) == Half.TOP;

        BlockState partTwo = switch (state.getValue(SHAPE))
        {
            case STRAIGHT -> FBContent.BLOCK_FRAMED_SLAB_EDGE.value()
                    .defaultBlockState()
                    .setValue(FramedProperties.FACING_HOR, dir)
                    .setValue(FramedProperties.TOP, !top);
            case INNER_LEFT -> FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value()
                    .defaultBlockState()
                    .setValue(FramedProperties.FACING_HOR, dir)
                    .setValue(FramedProperties.TOP, !top);
            case INNER_RIGHT -> FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value()
                    .defaultBlockState()
                    .setValue(FramedProperties.FACING_HOR, dir.getClockWise())
                    .setValue(FramedProperties.TOP, !top);
            case OUTER_LEFT -> FBContent.BLOCK_FRAMED_SLAB_CORNER.value()
                    .defaultBlockState()
                    .setValue(FramedProperties.FACING_HOR, dir)
                    .setValue(FramedProperties.TOP, !top);
            case OUTER_RIGHT -> FBContent.BLOCK_FRAMED_SLAB_CORNER.value()
                    .defaultBlockState()
                    .setValue(FramedProperties.FACING_HOR, dir.getClockWise())
                    .setValue(FramedProperties.TOP, !top);
        };
        return new DoubleBlockParts(
                FBContent.BLOCK_FRAMED_SLAB.value()
                        .defaultBlockState()
                        .setValue(FramedProperties.TOP, top),
                partTwo
        );
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        Direction dir = state.getValue(FACING);
        StairsShape shape = state.getValue(SHAPE);
        boolean top = state.getValue(HALF) == Half.TOP;
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == dirTwo)
        {
            return SolidityCheck.FIRST;
        }
        else if (side == dir && shape != StairsShape.OUTER_LEFT && shape != StairsShape.OUTER_RIGHT)
        {
            return SolidityCheck.BOTH;
        }
        else if (side == dir.getCounterClockWise() && shape == StairsShape.INNER_LEFT)
        {
            return SolidityCheck.BOTH;
        }
        else if (side == dir.getClockWise() && shape == StairsShape.INNER_RIGHT)
        {
            return SolidityCheck.BOTH;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FACING);
        boolean top = state.getValue(HALF) == Half.TOP;
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == dirTwo || (!DirUtils.isY(side) && edge == dirTwo))
        {
            return CamoGetter.FIRST;
        }

        return switch (state.getValue(SHAPE))
        {
            case STRAIGHT ->
            {
                if (side == dir && edge == dirTwo.getOpposite())
                {
                    yield CamoGetter.SECOND;
                }
                else if (side == dirTwo.getOpposite() && edge == dir)
                {
                    yield CamoGetter.SECOND;
                }
                yield CamoGetter.NONE;
            }
            case INNER_LEFT ->
            {
                if (side == dirTwo.getOpposite() && (edge == dir || edge == dir.getCounterClockWise()))
                {
                    yield CamoGetter.SECOND;
                }
                if ((side == dir || side == dir.getCounterClockWise()) && edge == dirTwo.getOpposite())
                {
                    yield CamoGetter.SECOND;
                }
                yield CamoGetter.NONE;
            }
            case INNER_RIGHT ->
            {
                if (side == dirTwo.getOpposite() && (edge == dir || edge == dir.getClockWise()))
                {
                    yield CamoGetter.SECOND;
                }
                if ((side == dir || side == dir.getClockWise()) && edge == dirTwo.getOpposite())
                {
                    yield CamoGetter.SECOND;
                }
                yield CamoGetter.NONE;
            }
            case OUTER_LEFT, OUTER_RIGHT -> CamoGetter.NONE;
        };
    }

    @Override
    public FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return IFramedDoubleBlockInternal.super.newBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state)
    {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return getItemModelSource();
    }
}
