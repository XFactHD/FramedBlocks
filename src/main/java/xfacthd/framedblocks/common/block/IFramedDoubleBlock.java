package xfacthd.framedblocks.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.cache.StateCache;
import xfacthd.framedblocks.api.block.render.ParticleHelper;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.util.CamoList;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.doubleblock.*;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;

import java.util.Objects;
import java.util.Optional;

public interface IFramedDoubleBlock extends IFramedBlock
{
    @Override
    @SuppressWarnings("deprecation")
    default SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity)
    {
        if (level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)
        {
            return be.getSoundType();
        }
        return state.getSoundType();
    }

    @Override
    default boolean isCamoEmissiveRendering(BlockState state, BlockGetter level, BlockPos pos)
    {
        ModelData modelData = level.getModelData(pos);
        if (modelData == ModelData.EMPTY) return false;

        return IFramedBlock.isCamoEmissiveRendering(modelData.get(FramedDoubleBlockEntity.DATA_LEFT)) ||
               IFramedBlock.isCamoEmissiveRendering(modelData.get(FramedDoubleBlockEntity.DATA_RIGHT));
    }

    @Override
    BlockEntity newBlockEntity(BlockPos pos, BlockState state);

    @ApiStatus.OverrideOnly
    DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state);

    @ApiStatus.OverrideOnly
    Tuple<BlockState, BlockState> calculateBlockPair(BlockState state);

    @ApiStatus.OverrideOnly
    SolidityCheck calculateSolidityCheck(BlockState state, Direction side);

    @ApiStatus.OverrideOnly
    CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge);

    @ApiStatus.NonExtendable
    default DoubleBlockTopInteractionMode getTopInteractionMode(BlockState state)
    {
        return getCache(state).getTopInteractionMode();
    }

    @ApiStatus.NonExtendable
    default Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        return getCache(state).getBlockPair();
    }

    @ApiStatus.NonExtendable
    default SolidityCheck getSolidityCheck(BlockState state, Direction side)
    {
        return getCache(state).getSolidityCheck(side);
    }

    @ApiStatus.NonExtendable
    default CamoGetter getCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        return getCache(state).getCamoGetter(side, edge);
    }

    @Override
    default StateCache initCache(BlockState state)
    {
        return new DoubleBlockStateCache(state, getBlockType());
    }

    @Override
    default DoubleBlockStateCache getCache(BlockState state)
    {
        return (DoubleBlockStateCache) IFramedBlock.super.getCache(state);
    }

    @Override
    @Nullable
    default BlockState runOcclusionTestAndGetLookupState(
            SideSkipPredicate pred, BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> statePair = getBlockPair(adjState);
        if (pred.test(level, pos, state, statePair.getA(), side))
        {
            return statePair.getA();
        }
        if (pred.test(level, pos, state, statePair.getB(), side))
        {
            return statePair.getB();
        }
        return null;
    }

    @Override
    @Nullable
    default BlockState getComponentAtEdge(
            BlockGetter level, BlockPos pos, BlockState state, Direction side, @Nullable Direction edge
    )
    {
        DoubleBlockStateCache cache = getCache(state);
        return cache.getCamoGetter(side, edge).getComponent(cache.getBlockPair());
    }

    @Override
    @Nullable
    default BlockState getComponentBySkipPredicate(
            BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction side
    )
    {
        Tuple<BlockState, BlockState> blockPair = getBlockPair(state);
        BlockState compA = blockPair.getA();
        if (testComponent(level, pos, compA, neighborState, side))
        {
            return compA;
        }
        BlockState compB = blockPair.getB();
        if (testComponent(level, pos, compB, neighborState, side))
        {
            return compB;
        }
        return null;
    }

    static boolean testComponent(BlockGetter ctLevel, BlockPos pos, BlockState component, BlockState neighborState, Direction side)
    {
        IFramedBlock block = (IFramedBlock) component.getBlock();
        return block.getBlockType().getSideSkipPredicate().test(ctLevel, pos, component, neighborState, side);
    }

    @Override
    default ModelData unpackNestedModelData(ModelData data, BlockState state, BlockState componentState)
    {
        Tuple<BlockState, BlockState> blockPair = getBlockPair(state);
        if (componentState == blockPair.getA())
        {
            return Objects.requireNonNullElse(data.get(FramedDoubleBlockEntity.DATA_LEFT), ModelData.EMPTY);
        }
        if (componentState == blockPair.getB())
        {
            return Objects.requireNonNullElse(data.get(FramedDoubleBlockEntity.DATA_RIGHT), ModelData.EMPTY);
        }
        return ModelData.EMPTY;
    }

    @Override
    default boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if (level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)
        {
            Tuple<BlockState, BlockState> statePair = getBlockPair(state);
            switch (getTopInteractionMode(state))
            {
                case FIRST -> ParticleHelper.spawnRunningParticles(be.getCamo(statePair.getA()), level, pos, entity);
                case SECOND -> ParticleHelper.spawnRunningParticles(be.getCamo(statePair.getB()), level, pos, entity);
                case EITHER ->
                {
                    ParticleHelper.spawnRunningParticles(be.getCamo(statePair.getA()), level, pos, entity);
                    ParticleHelper.spawnRunningParticles(be.getCamo(statePair.getB()), level, pos, entity);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    default boolean addLandingEffects(
            BlockState state, ServerLevel level, BlockPos pos, BlockState sameState, LivingEntity entity, int count
    )
    {
        if (level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)
        {
            Tuple<BlockState, BlockState> statePair = getBlockPair(state);
            switch (getTopInteractionMode(state))
            {
                case FIRST -> ParticleHelper.spawnLandingParticles(be.getCamo(statePair.getA()), level, pos, entity, count);
                case SECOND -> ParticleHelper.spawnLandingParticles(be.getCamo(statePair.getB()), level, pos, entity, count);
                case EITHER ->
                {
                    ParticleHelper.spawnLandingParticles(be.getCamo(statePair.getA()), level, pos, entity, count);
                    ParticleHelper.spawnLandingParticles(be.getCamo(statePair.getB()), level, pos, entity, count);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    default Optional<MutableComponent> printCamoData(CamoList camos, boolean blueprint)
    {
        return printCamoData(camos.getCamo(0), camos.getCamo(1), blueprint);
    }

    static Optional<MutableComponent> printCamoData(CamoContainer<?, ?> camoContainer, CamoContainer<?, ?> camoContainerTwo, boolean force)
    {
        if (force || !camoContainer.isEmpty() || !camoContainerTwo.isEmpty())
        {
            MutableComponent component = getCamoComponent(camoContainer);
            component.append(Component.literal(" | ").withStyle(ChatFormatting.GOLD));
            component.append(getCamoComponent(camoContainerTwo));

            return Optional.of(component);
        }
        return Optional.empty();
    }

    static MutableComponent getCamoComponent(CamoContainer<?, ?> camoContainer)
    {
        if (!camoContainer.isEmpty())
        {
            return camoContainer.getContent().getCamoName().withStyle(ChatFormatting.WHITE);
        }
        return FramedBlueprintItem.BLOCK_NONE.copy();
    }
}
