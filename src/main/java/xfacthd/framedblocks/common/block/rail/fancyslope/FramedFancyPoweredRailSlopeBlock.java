package xfacthd.framedblocks.common.block.rail.fancyslope;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.*;
import xfacthd.framedblocks.common.block.rail.vanillaslope.FramedPoweredRailSlopeBlock;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.doubleblock.*;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.util.FramedUtils;

import java.util.Objects;
import java.util.function.BiFunction;

public class FramedFancyPoweredRailSlopeBlock extends FramedPoweredRailSlopeBlock implements IFramedDoubleBlock, ISlopeBlock.IRailSlopeBlock
{
    public FramedFancyPoweredRailSlopeBlock(
            BlockType type, boolean isPoweredRail, BiFunction<BlockPos, BlockState, FramedBlockEntity> beFactory
    )
    {
        super(type, isPoweredRail, beFactory);
    }

    @Override
    @Nullable
    public BlockState runOcclusionTestAndGetLookupState(
            SideSkipPredicate pred, BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> statePair = getBlockPair(adjState);
        return super.runOcclusionTestAndGetLookupState(pred, level, pos, state, statePair.getA(), side);
    }

    @Override
    @Nullable
    public BlockState getComponentBySkipPredicate(
            BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction side
    )
    {
        BlockState slopeState = getBlockPair(state).getA();
        if (IFramedDoubleBlock.testComponent(level, pos, slopeState, neighborState, side))
        {
            return slopeState;
        }
        return null;
    }

    @Override
    public ModelData unpackNestedModelData(ModelData data, BlockState state, BlockState componentState)
    {
        return Objects.requireNonNullElse(data.get(FramedDoubleBlockEntity.DATA_LEFT), ModelData.EMPTY);
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.FIRST;
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        RailShape shape = state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        BlockState railState = (switch (getBlockType())
        {
            case FRAMED_FANCY_POWERED_RAIL_SLOPE -> FBContent.BLOCK_FRAMED_FANCY_POWERED_RAIL;
            case FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE -> FBContent.BLOCK_FRAMED_FANCY_ACTIVATOR_RAIL;
            default -> throw new IllegalArgumentException("Invalid block type");
        }).value().defaultBlockState();
        Direction facing = FramedUtils.getDirectionFromAscendingRailShape(shape);

        return new Tuple<>(
                FBContent.BLOCK_FRAMED_SLOPE.value().defaultBlockState()
                        .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.BOTTOM)
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.Y_SLOPE, ySlope),
                railState.setValue(BlockStateProperties.RAIL_SHAPE_STRAIGHT, shape)
        );
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = getFacing(state);
        if (side == facing || side == Direction.DOWN)
        {
            return CamoGetter.FIRST;
        }
        else if (side.getAxis() != facing.getAxis() && !Utils.isY(side))
        {
            if (edge == facing || edge == Direction.DOWN)
            {
                return CamoGetter.FIRST;
            }
        }
        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        if (side == Direction.DOWN || side == getFacing(state))
        {
            return SolidityCheck.FIRST;
        }
        return SolidityCheck.NONE;
    }
}
