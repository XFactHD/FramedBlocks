package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

public interface IFramedDoubleBlock extends IFramedBlock
{
    DoubleBlockTopInteractionMode getTopInteractionModeRaw(BlockState state);

    default DoubleBlockTopInteractionMode getTopInteractionMode(BlockState state)
    {
        return getTopInteractionModeRaw(state);
    }

    default boolean addCamoRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if (level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)
        {
            Tuple<BlockState, BlockState> statePair = AbstractFramedDoubleBlock.getStatePair(state);
            switch (getTopInteractionMode(state))
            {
                case FIRST -> IFramedDoubleBlock.spawnRunningParticles(
                        be.getCamo(statePair.getA()).getState(), level, pos, entity
                );
                case SECOND -> IFramedDoubleBlock.spawnRunningParticles(
                        be.getCamo(statePair.getB()).getState(), level, pos, entity
                );
                case EITHER ->
                {
                    IFramedDoubleBlock.spawnRunningParticles(
                            be.getCamo(statePair.getA()).getState(), level, pos, entity
                    );
                    IFramedDoubleBlock.spawnRunningParticles(
                            be.getCamo(statePair.getB()).getState(), level, pos, entity
                    );
                }
            }
            return true;
        }
        return false;
    }

    default boolean addCamoLandingEffects(BlockState state, ServerLevel level, BlockPos pos, LivingEntity entity, int count)
    {
        if (level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)
        {
            Tuple<BlockState, BlockState> statePair = AbstractFramedDoubleBlock.getStatePair(state);
            switch (getTopInteractionMode(state))
            {
                case FIRST -> IFramedDoubleBlock.spawnLandingParticles(
                        be.getCamo(statePair.getA()).getState(), level, pos, entity, count
                );
                case SECOND -> IFramedDoubleBlock.spawnLandingParticles(
                        be.getCamo(statePair.getB()).getState(), level, pos, entity, count
                );
                case EITHER ->
                {
                    IFramedDoubleBlock.spawnLandingParticles(
                            be.getCamo(statePair.getA()).getState(), level, pos, entity, count
                    );
                    IFramedDoubleBlock.spawnLandingParticles(
                            be.getCamo(statePair.getB()).getState(), level, pos, entity, count
                    );
                }
            }
            return true;
        }
        return false;
    }



    static void spawnLandingParticles(BlockState state, ServerLevel level, BlockPos pos, LivingEntity entity, int count)
    {
        if (state.isAir())
        {
            state = FBContent.BLOCK_FRAMED_CUBE.get().defaultBlockState();
        }

        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        BlockPos entityPos = entity.blockPosition();
        if (pos.getX() != entityPos.getX() || pos.getZ() != entityPos.getZ())
        {
            double offX = x - (double)pos.getX() - 0.5D;
            double offZ = z - (double)pos.getZ() - 0.5D;
            double maxOff = Math.max(Math.abs(offX), Math.abs(offZ));
            x = (double)pos.getX() + 0.5D + offX / maxOff * 0.5D;
            z = (double)pos.getZ() + 0.5D + offZ / maxOff * 0.5D;
        }

        level.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, state),
                x,
                y,
                z,
                count,
                0D,
                0D,
                0D,
                0.15D
        );
    }

    static void spawnRunningParticles(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if (state.isAir())
        {
            state = FBContent.BLOCK_FRAMED_CUBE.get().defaultBlockState();
        }

        Vec3 delta = entity.getDeltaMovement();
        BlockPos enityPos = entity.blockPosition();

        double x = entity.getRandomX(0.5D);
        double z = entity.getRandomZ(0.5D);
        if (enityPos.getX() != pos.getX())
        {
            x = Mth.clamp(x, pos.getX(), pos.getX() + 1D);
        }
        if (enityPos.getZ() != pos.getZ())
        {
            z = Mth.clamp(z, pos.getZ(), pos.getZ() + 1D);
        }

        // Don't set the position to prevent a redundant lookup on the model
        level.addParticle(
                new BlockParticleOption(ParticleTypes.BLOCK, state),
                x,
                entity.getY() + 0.1D,
                z,
                delta.x * -4D,
                1.5D,
                delta.z * -4D
        );
    }
}
