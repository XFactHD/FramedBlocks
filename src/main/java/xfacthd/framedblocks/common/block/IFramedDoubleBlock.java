package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.cache.StateCache;
import xfacthd.framedblocks.api.block.render.ParticleHelper;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

public interface IFramedDoubleBlock extends IFramedBlock
{
    @ApiStatus.OverrideOnly
    DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state);

    @ApiStatus.OverrideOnly
    Tuple<BlockState, BlockState> calculateBlockPair(BlockState state);

    default DoubleBlockTopInteractionMode getTopInteractionMode(BlockState state)
    {
        return getCache(state).getTopInteractionMode();
    }

    default Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        return getCache(state).getBlockPair();
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
}
