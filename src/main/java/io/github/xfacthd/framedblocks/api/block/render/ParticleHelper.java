package io.github.xfacthd.framedblocks.api.block.render;

import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.camo.CamoClientHandler;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import io.github.xfacthd.framedblocks.client.render.particle.BlockOverlayParticle;
import io.github.xfacthd.framedblocks.common.particle.BlockOverlayParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class ParticleHelper
{
    public static void spawnLandingParticles(
            CamoContainer<?, ?> camo,
            @Nullable Holder<BlockOverlay> overlay,
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            LivingEntity entity,
            int count
    )
    {
        if (overlay != null && overlay.value().isSideSolid(state, Direction.UP))
        {
            spawnLandingParticles(new BlockOverlayParticleOptions(overlay), level, pos, entity, count);
            return;
        }
        spawnLandingParticles(camo.getContent(), level, pos, entity, count);
    }

    public static void spawnLandingParticles(CamoContent<?> camo, ServerLevel level, BlockPos pos, LivingEntity entity, int count)
    {
        spawnLandingParticles(camo.makeRunningLandingParticles(pos), level, pos, entity, count);
    }

    private static void spawnLandingParticles(ParticleOptions options, ServerLevel level, BlockPos pos, LivingEntity entity, int count)
    {
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        BlockPos entityPos = entity.blockPosition();
        if (pos.getX() != entityPos.getX() || pos.getZ() != entityPos.getZ())
        {
            double offX = x - (double)pos.getX() - 0.5D;
            double offZ = z - (double)pos.getZ() - 0.5D;
            double maxOff = Math.max(Math.abs(offX), Math.abs(offZ));
            x = (double) pos.getX() + 0.5D + offX / maxOff * 0.5D;
            z = (double) pos.getZ() + 0.5D + offZ / maxOff * 0.5D;
        }

        level.sendParticles(options, x, y, z, count, 0D, 0D, 0D, 0.15D);
    }

    public static void spawnRunningParticles(
            CamoContainer<?, ?> camo,
            @Nullable Holder<BlockOverlay> overlay,
            BlockState state,
            Level level,
            BlockPos pos,
            Entity entity
    )
    {
        if (overlay != null && overlay.value().isSideSolid(state, Direction.UP))
        {
            spawnRunningParticles(new BlockOverlayParticleOptions(overlay), level, pos, entity);
            return;
        }
        spawnRunningParticles(camo.getContent(), level, pos, entity);
    }

    public static void spawnRunningParticles(CamoContent<?> camo, Level level, BlockPos pos, Entity entity)
    {
        spawnRunningParticles(camo.makeRunningLandingParticles(pos), level, pos, entity);
    }

    private static void spawnRunningParticles(ParticleOptions options, Level level, BlockPos pos, Entity entity)
    {
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

        level.addParticle(options, x, entity.getY() + 0.1D, z, delta.x * -4D, 1.5D, delta.z * -4D);
    }



    public static final class Client
    {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public static void addHitEffects(BlockState state, Level level, BlockHitResult target, CamoContent<?> camo, @Nullable Holder<BlockOverlay> overlay, ParticleEngine engine)
        {
            ClientLevel clientLevel = (ClientLevel) level;
            BlockPos pos = target.getBlockPos();
            Direction side = target.getDirection();

            double bx = pos.getX();
            double by = pos.getY();
            double bz = pos.getZ();

            AABB aabb = state.getShape(level, pos).bounds();
            double x = bx + engine.random.nextDouble() * (aabb.maxX - aabb.minX - 0.2) + 0.1D + aabb.minX;
            double y = by + engine.random.nextDouble() * (aabb.maxY - aabb.minY - 0.2) + 0.1D + aabb.minY;
            double z = bz + engine.random.nextDouble() * (aabb.maxZ - aabb.minZ - 0.2) + 0.1D + aabb.minZ;

            switch (side)
            {
                case DOWN ->  y = by + aabb.minY - (double)0.1F;
                case UP ->    y = by + aabb.maxY + (double)0.1F;
                case NORTH -> z = bz + aabb.minZ - (double)0.1F;
                case SOUTH -> z = bz + aabb.maxZ + (double)0.1F;
                case WEST ->  x = bx + aabb.minX - (double)0.1F;
                case EAST ->  x = bx + aabb.maxX + (double)0.1F;
            }

            CamoClientHandler handler = camo.getClientHandler();
            engine.add(handler.makeHitDestroyParticle(clientLevel, x, y, z, 0.0D, 0.0D, 0.0D, camo, pos)
                    .setPower(0.2F)
                    .scale(0.6F)
            );
            if (overlay != null)
            {
                engine.add(new BlockOverlayParticle(clientLevel, x, y, z, 0.0D, 0.0D, 0.0D, overlay.value()));
            }
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        public static void addDestroyEffects(BlockState state, Level level, BlockPos pos, CamoContent<?> camo, @Nullable Holder<BlockOverlay> overlay, ParticleEngine engine)
        {
            CamoClientHandler handler = camo.getClientHandler();
            ClientLevel clientLevel = (ClientLevel) level;

            List<AABB> boxes = state.getShape(level, pos).toAabbs();
            double countMult = 1D / boxes.size();
            for (AABB aabb : boxes)
            {
                double sizeX = Math.min(1D, aabb.maxX - aabb.minX);
                double sizeY = Math.min(1D, aabb.maxY - aabb.minY);
                double sizeZ = Math.min(1D, aabb.maxZ - aabb.minZ);
                int xCount = Math.max(2, Mth.ceil(sizeX / 0.25D * countMult));
                int yCount = Math.max(2, Mth.ceil(sizeY / 0.25D * countMult));
                int zCount = Math.max(2, Mth.ceil(sizeZ / 0.25D * countMult));

                for (int iX = 0; iX < xCount; ++iX)
                {
                    for (int iY = 0; iY < yCount; ++iY)
                    {
                        for (int iZ = 0; iZ < zCount; ++iZ)
                        {
                            double offX = ((double) iX + 0.5D) / (double) xCount;
                            double offY = ((double) iY + 0.5D) / (double) yCount;
                            double offZ = ((double) iZ + 0.5D) / (double) zCount;
                            double x = pos.getX() + offX * sizeX + aabb.minX;
                            double y = pos.getY() + offY * sizeY + aabb.minY;
                            double z = pos.getZ() + offZ * sizeZ + aabb.minZ;
                            double sx = offX - 0.5D;
                            double sy = offY - 0.5D;
                            double sz = offZ - 0.5D;
                            engine.add(handler.makeHitDestroyParticle(clientLevel, x, y, z, sx, sy, sz, camo, pos));
                            if (overlay != null)
                            {
                                engine.add(new BlockOverlayParticle(clientLevel, x, y, z, sx, sy, sz, overlay.value()));
                            }
                        }
                    }
                }
            }
        }



        private Client() { }
    }



    private ParticleHelper() { }
}
