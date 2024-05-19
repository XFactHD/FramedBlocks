package xfacthd.framedblocks.common.block.stairs.standard;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.doubleblock.*;

import java.util.function.Consumer;

public class FramedDividedStairsBlock extends FramedStairsBlock implements IFramedDoubleBlock
{
    public FramedDividedStairsBlock()
    {
        super(BlockType.FRAMED_DIVIDED_STAIRS);
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(BlockStateProperties.HALF) == Half.TOP;

        return switch (state.getValue(SHAPE))
        {
            case STRAIGHT -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_HALF_STAIRS.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(PropertyHolder.RIGHT, false),
                    FBContent.BLOCK_FRAMED_HALF_STAIRS.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(PropertyHolder.RIGHT, true)
            );
            case INNER_LEFT -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_PANEL.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing.getCounterClockWise()),
                    FBContent.BLOCK_FRAMED_HALF_STAIRS.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(PropertyHolder.RIGHT, true)
            );
            case INNER_RIGHT -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_HALF_STAIRS.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(PropertyHolder.RIGHT, false),
                    FBContent.BLOCK_FRAMED_PANEL.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing.getClockWise())
            );
            case OUTER_LEFT -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_HALF_STAIRS.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(PropertyHolder.RIGHT, false),
                    FBContent.BLOCK_FRAMED_SLAB_EDGE.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing.getClockWise())
                            .setValue(FramedProperties.TOP, top)
            );
            case OUTER_RIGHT -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_SLAB_EDGE.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing.getCounterClockWise())
                            .setValue(FramedProperties.TOP, top),
                    FBContent.BLOCK_FRAMED_HALF_STAIRS.value().defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(PropertyHolder.RIGHT, true)
            );
        };
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        StairsShape shape = state.getValue(SHAPE);
        boolean top = state.getValue(BlockStateProperties.HALF) == Half.TOP;
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == facing)
        {
            if (edge == facing.getCounterClockWise() && shape != StairsShape.OUTER_RIGHT)
            {
                return CamoGetter.FIRST;
            }
            if (edge == facing.getClockWise() && shape != StairsShape.OUTER_LEFT)
            {
                return CamoGetter.SECOND;
            }
            return CamoGetter.NONE;
        }
        if (side == dirTwo)
        {
            if (edge == facing.getCounterClockWise())
            {
                return CamoGetter.FIRST;
            }
            if (edge == facing.getClockWise())
            {
                return CamoGetter.SECOND;
            }
            return CamoGetter.NONE;
        }
        if (side == dirTwo.getOpposite() || side == facing.getOpposite())
        {
            if (shape == StairsShape.INNER_LEFT && edge == facing.getCounterClockWise())
            {
                return CamoGetter.FIRST;
            }
            if (shape == StairsShape.INNER_RIGHT && edge == facing.getClockWise())
            {
                return CamoGetter.SECOND;
            }
            return CamoGetter.NONE;
        }
        if (side == facing.getCounterClockWise())
        {
            if (shape == StairsShape.INNER_LEFT)
            {
                return CamoGetter.FIRST;
            }
            if (shape != StairsShape.OUTER_RIGHT && (edge == facing || edge == dirTwo))
            {
                return CamoGetter.FIRST;
            }
            return CamoGetter.NONE;
        }
        if (side == facing.getClockWise())
        {
            if (shape == StairsShape.INNER_RIGHT)
            {
                return CamoGetter.SECOND;
            }
            if (shape != StairsShape.OUTER_LEFT && (edge == facing || edge == dirTwo))
            {
                return CamoGetter.SECOND;
            }
            return CamoGetter.NONE;
        }
        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        StairsShape shape = state.getValue(SHAPE);
        boolean top = state.getValue(BlockStateProperties.HALF) == Half.TOP;
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == facing && shape != StairsShape.OUTER_LEFT && shape != StairsShape.OUTER_RIGHT)
        {
            return SolidityCheck.BOTH;
        }
        if (side == dirTwo)
        {
            return SolidityCheck.BOTH;
        }
        if (shape == StairsShape.INNER_LEFT && side == facing.getCounterClockWise())
        {
            return SolidityCheck.FIRST;
        }
        if (shape == StairsShape.INNER_RIGHT && side == facing.getClockWise())
        {
            return SolidityCheck.FIRST;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleBlockEntity(pos, state);
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer)
    {
        consumer.accept(FramedDoubleBlockRenderProperties.INSTANCE);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
