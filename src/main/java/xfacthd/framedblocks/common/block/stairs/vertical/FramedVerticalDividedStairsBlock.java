package xfacthd.framedblocks.common.block.stairs.vertical;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.doubleblock.*;
import xfacthd.framedblocks.common.data.property.StairsType;

public class FramedVerticalDividedStairsBlock extends FramedVerticalStairsBlock implements IFramedDoubleBlock
{
    public FramedVerticalDividedStairsBlock()
    {
        super(BlockType.FRAMED_VERTICAL_DIVIDED_STAIRS);
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);

        return switch (state.getValue(PropertyHolder.STAIRS_TYPE))
        {
            case VERTICAL -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, false),
                    FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, true)
            );
            case TOP_FWD -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, false),
                    FBContent.BLOCK_FRAMED_SLAB_EDGE.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing.getCounterClockWise())
                            .setValue(FramedProperties.TOP, true)
            );
            case TOP_CCW -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, false),
                    FBContent.BLOCK_FRAMED_SLAB_EDGE.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, true)
            );
            case TOP_BOTH -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, false),
                    FBContent.BLOCK_FRAMED_SLAB_CORNER.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, true)
            );
            case BOTTOM_FWD -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_SLAB_EDGE.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing.getCounterClockWise())
                            .setValue(FramedProperties.TOP, false),
                    FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, true)
            );
            case BOTTOM_CCW -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_SLAB_EDGE.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, false),
                    FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, true)
            );
            case BOTTOM_BOTH -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_SLAB_CORNER.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, false),
                    FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, true)
            );
        };
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.SECOND;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);

        if ((side == Direction.DOWN && edge == facing) || (side == facing && edge == Direction.DOWN))
        {
            return !type.isBottom() || type == StairsType.BOTTOM_CCW ? CamoGetter.FIRST : CamoGetter.NONE;
        }
        if ((side == Direction.UP && edge == facing) || (side == facing && edge == Direction.UP))
        {
            return !type.isTop() || type == StairsType.TOP_CCW ? CamoGetter.SECOND : CamoGetter.NONE;
        }

        Direction facingCcw = facing.getCounterClockWise();
        if ((side == Direction.DOWN && edge == facingCcw) || (side == facingCcw && edge == Direction.DOWN))
        {
            return !type.isBottom() || type == StairsType.BOTTOM_FWD ? CamoGetter.FIRST : CamoGetter.NONE;
        }
        if ((side == Direction.UP && edge == facingCcw) || (side == facingCcw && edge == Direction.UP))
        {
            return !type.isTop() || type == StairsType.TOP_FWD ? CamoGetter.SECOND : CamoGetter.NONE;
        }
        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);
        if (side == facing && !type.isForward())
        {
            return SolidityCheck.BOTH;
        }
        if (side == facing.getCounterClockWise() && !type.isCounterClockwise())
        {
            return SolidityCheck.BOTH;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleBlockEntity(pos, state);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }
}
