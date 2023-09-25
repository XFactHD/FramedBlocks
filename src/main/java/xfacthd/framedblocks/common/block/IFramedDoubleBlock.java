package xfacthd.framedblocks.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.cache.StateCache;
import xfacthd.framedblocks.api.block.render.ParticleHelper;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.doubleblock.*;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

import java.util.Optional;

public interface IFramedDoubleBlock extends IFramedBlock
{
    @ApiStatus.OverrideOnly
    DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state);

    @ApiStatus.OverrideOnly
    Tuple<BlockState, BlockState> calculateBlockPair(BlockState state);

    @ApiStatus.OverrideOnly
    SolidityCheck calculateSolidityCheck(BlockState state, Direction side);

    @ApiStatus.OverrideOnly
    CamoGetter calculateCamoGetter(BlockState state, Direction side, Direction edge);

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
    default boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if (level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)
        {
            Tuple<BlockState, BlockState> statePair = getBlockPair(state);
            switch (getTopInteractionMode(state))
            {
                case FIRST -> ParticleHelper.spawnRunningParticles(
                        be.getCamo(statePair.getA()).getState(), level, pos, entity
                );
                case SECOND -> ParticleHelper.spawnRunningParticles(
                        be.getCamo(statePair.getB()).getState(), level, pos, entity
                );
                case EITHER ->
                {
                    ParticleHelper.spawnRunningParticles(
                            be.getCamo(statePair.getA()).getState(), level, pos, entity
                    );
                    ParticleHelper.spawnRunningParticles(
                            be.getCamo(statePair.getB()).getState(), level, pos, entity
                    );
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
                case FIRST -> ParticleHelper.spawnLandingParticles(
                        be.getCamo(statePair.getA()).getState(), level, pos, entity, count
                );
                case SECOND -> ParticleHelper.spawnLandingParticles(
                        be.getCamo(statePair.getB()).getState(), level, pos, entity, count
                );
                case EITHER ->
                {
                    ParticleHelper.spawnLandingParticles(
                            be.getCamo(statePair.getA()).getState(), level, pos, entity, count
                    );
                    ParticleHelper.spawnLandingParticles(
                            be.getCamo(statePair.getB()).getState(), level, pos, entity, count
                    );
                }
            }
            return true;
        }
        return false;
    }

    @Override
    default Optional<MutableComponent> printCamoBlock(CompoundTag beTag)
    {
        BlockState camoState = CamoContainer.load(beTag.getCompound("camo")).getState();
        BlockState camoStateTwo = CamoContainer.load(beTag.getCompound("camo_two")).getState();

        MutableComponent component = getCamoComponent(camoState);
        component.append(Component.literal(" | ").withStyle(ChatFormatting.GOLD));
        component.append(getCamoComponent(camoStateTwo));

        return Optional.of(component);
    }

    private static MutableComponent getCamoComponent(BlockState camoState)
    {
        if (!camoState.isAir())
        {
            return camoState.getBlock().getName().withStyle(ChatFormatting.WHITE);
        }
        return FramedBlueprintItem.BLOCK_NONE.copy();
    }
}
