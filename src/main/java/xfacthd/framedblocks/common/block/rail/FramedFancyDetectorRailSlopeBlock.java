package xfacthd.framedblocks.common.block.rail;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;
import xfacthd.framedblocks.common.util.FramedUtils;

import java.util.function.BiFunction;
import java.util.function.Consumer;

class FramedFancyDetectorRailSlopeBlock extends FramedDetectorRailSlopeBlock implements IFramedDoubleBlock
{
    FramedFancyDetectorRailSlopeBlock(BlockType type, BiFunction<BlockPos, BlockState, FramedBlockEntity> beFactory)
    {
        super(type, beFactory);
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        return addCamoRunningEffects(state, level, pos, entity);
    }

    @Override
    public boolean addLandingEffects(
            BlockState state, ServerLevel level, BlockPos pos, BlockState sameState, LivingEntity entity, int count
    )
    {
        return addCamoLandingEffects(state, level, pos, entity, count);
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
                FBContent.BLOCK_FRAMED_SLOPE.get().defaultBlockState()
                        .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.BOTTOM)
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.Y_SLOPE, ySlope),
                FBContent.BLOCK_FRAMED_FANCY_DETECTOR_RAIL.get().defaultBlockState()
                        .setValue(BlockStateProperties.RAIL_SHAPE_STRAIGHT, shape)
        );
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer)
    {
        consumer.accept(new FramedDoubleBlockRenderProperties());
    }
}
