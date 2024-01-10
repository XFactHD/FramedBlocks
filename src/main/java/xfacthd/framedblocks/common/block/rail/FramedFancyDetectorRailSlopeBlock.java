package xfacthd.framedblocks.common.block.rail;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.*;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.doubleblock.*;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.util.FramedUtils;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

class FramedFancyDetectorRailSlopeBlock extends FramedDetectorRailSlopeBlock implements IFramedDoubleBlock, ISlopeBlock.IRailSlopeBlock
{
    FramedFancyDetectorRailSlopeBlock(BiFunction<BlockPos, BlockState, FramedBlockEntity> beFactory)
    {
        super(BlockType.FRAMED_FANCY_DETECTOR_RAIL_SLOPE, beFactory);
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

        Direction facing = FramedUtils.getDirectionFromAscendingRailShape(shape);

        return new Tuple<>(
                FBContent.BLOCK_FRAMED_SLOPE.value().defaultBlockState()
                        .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.BOTTOM)
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.Y_SLOPE, ySlope),
                FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL.value().defaultBlockState()
                        .setValue(BlockStateProperties.RAIL_SHAPE_STRAIGHT, shape)
        );
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        return CamoGetter.FIRST;
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

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer)
    {
        consumer.accept(FramedDoubleBlockRenderProperties.INSTANCE);
    }
}
