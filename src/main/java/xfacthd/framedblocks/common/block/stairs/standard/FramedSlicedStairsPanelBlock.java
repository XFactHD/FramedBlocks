package xfacthd.framedblocks.common.block.stairs.standard;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.doubleblock.*;

public class FramedSlicedStairsPanelBlock extends FramedStairsBlock implements IFramedDoubleBlock
{
    public FramedSlicedStairsPanelBlock()
    {
        super(BlockType.FRAMED_SLICED_STAIRS_PANEL);
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction dir = state.getValue(FACING);
        boolean top = state.getValue(HALF) == Half.TOP;

        return switch (state.getValue(SHAPE))
        {
            case STRAIGHT -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_PANEL.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir),
                    FBContent.BLOCK_FRAMED_SLAB_EDGE.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir.getOpposite())
                            .setValue(FramedProperties.TOP, top)
            );
            case INNER_LEFT -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_VERTICAL_STAIRS.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir),
                    FBContent.BLOCK_FRAMED_SLAB_CORNER.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir.getOpposite())
                            .setValue(FramedProperties.TOP, top)
            );
            case INNER_RIGHT -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_VERTICAL_STAIRS.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir.getClockWise()),
                    FBContent.BLOCK_FRAMED_SLAB_CORNER.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir.getCounterClockWise())
                            .setValue(FramedProperties.TOP, top)
            );
            case OUTER_LEFT -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_CORNER_PILLAR.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir),
                    FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir.getOpposite())
                            .setValue(FramedProperties.TOP, top)
            );
            case OUTER_RIGHT -> new Tuple<>(
                    FBContent.BLOCK_FRAMED_CORNER_PILLAR.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir.getClockWise()),
                    FBContent.BLOCK_FRAMED_VERTICAL_HALF_STAIRS.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir.getCounterClockWise())
                            .setValue(FramedProperties.TOP, top)
            );
        };
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        Direction dir = state.getValue(FACING);
        StairsShape shape = state.getValue(SHAPE);
        boolean top = state.getValue(HALF) == Half.TOP;
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == dir && shape != StairsShape.OUTER_LEFT && shape != StairsShape.OUTER_RIGHT)
        {
            return SolidityCheck.FIRST;
        }
        else if (side == dir.getCounterClockWise() && shape == StairsShape.INNER_LEFT)
        {
            return SolidityCheck.FIRST;
        }
        else if (side == dir.getClockWise() && shape == StairsShape.INNER_RIGHT)
        {
            return SolidityCheck.FIRST;
        }
        else if (side == dirTwo)
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

        return switch (state.getValue(SHAPE))
        {
            case STRAIGHT ->
            {
                if (side == dir || (side.getAxis() != dir.getAxis() && edge == dir))
                {
                    yield CamoGetter.FIRST;
                }
                else if (side == dirTwo && edge == dir.getOpposite())
                {
                    yield CamoGetter.SECOND;
                }
                else if (side == dir.getOpposite() && edge == dirTwo)
                {
                    yield CamoGetter.SECOND;
                }
                yield CamoGetter.NONE;
            }
            case INNER_LEFT ->
            {
                if (side == dir || side == dir.getCounterClockWise())
                {
                    yield CamoGetter.FIRST;
                }
                if (Utils.isY(side) && (edge == dir || edge == dir.getCounterClockWise()))
                {
                    yield CamoGetter.FIRST;
                }
                if ((side == dir.getClockWise() && edge == dir) || (side == dir.getOpposite() && edge == dir.getCounterClockWise()))
                {
                    yield CamoGetter.FIRST;
                }
                yield CamoGetter.NONE;
            }
            case INNER_RIGHT ->
            {
                if (side == dir || side == dir.getClockWise())
                {
                    yield CamoGetter.FIRST;
                }
                if (Utils.isY(side) && (edge == dir || edge == dir.getClockWise()))
                {
                    yield CamoGetter.FIRST;
                }
                if ((side == dir.getCounterClockWise() && edge == dir) || (side == dir.getOpposite() && edge == dir.getClockWise()))
                {
                    yield CamoGetter.FIRST;
                }
                yield CamoGetter.NONE;
            }
            case OUTER_LEFT ->
            {
                if ((side == dir && edge == dir.getCounterClockWise()) || (side == dir.getCounterClockWise() && edge == dir))
                {
                    yield CamoGetter.FIRST;
                }
                if (side == dirTwo && (edge == dir.getClockWise() || edge == dir.getOpposite()))
                {
                    yield CamoGetter.SECOND;
                }
                if ((side == dir.getClockWise() || side == dir.getOpposite()) && edge == dirTwo)
                {
                    yield CamoGetter.SECOND;
                }
                yield CamoGetter.NONE;
            }
            case OUTER_RIGHT ->
            {
                if ((side == dir && edge == dir.getClockWise()) || (side == dir.getClockWise() && edge == dir))
                {
                    yield CamoGetter.FIRST;
                }
                if (side == dirTwo && (edge == dir.getCounterClockWise() || edge == dir.getOpposite()))
                {
                    yield CamoGetter.SECOND;
                }
                if ((side == dir.getCounterClockWise() || side == dir.getOpposite()) && edge == dirTwo)
                {
                    yield CamoGetter.SECOND;
                }
                yield CamoGetter.NONE;
            }
        };
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return getItemModelSource();
    }
}
