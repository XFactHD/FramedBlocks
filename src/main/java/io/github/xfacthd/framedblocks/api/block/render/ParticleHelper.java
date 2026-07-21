package io.github.xfacthd.framedblocks.api.block.render;

import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerClientHandler;
import io.github.xfacthd.framedblocks.api.camo.CamoContentClientHandler;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import io.github.xfacthd.framedblocks.api.render.particle.CamoParticleOptions;
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

/// Various helpers for spawning particles on framed blocks.
public final class ParticleHelper {
    /// Spawn particles when an entity falls onto a framed block.
    ///
    /// @param camo    The camo applied to the top face of the block
    /// @param overlay The block overlay applied to the block
    /// @param state   The state of the block
    /// @param level   The level which the block is in
    /// @param pos     The position of the block
    /// @param entity  The entity which fell onto the block
    /// @param count   How many particles should be spawned
    public static void spawnLandingParticles(
            CamoContainer<?, ?> camo,
            @Nullable Holder<BlockOverlay> overlay,
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            LivingEntity entity,
            int count
    ) {
        if (overlay != null && overlay.value().isSideSolid(state, Direction.UP)) {
            spawnLandingParticles(new BlockOverlayParticleOptions(overlay), level, pos, entity, count);
            return;
        }
        spawnLandingParticles(camo, level, pos, entity, count);
    }

    /// Spawn particles when an entity falls onto a framed block.
    ///
    /// @param camo    The camo applied to the top face of the block
    /// @param level   The level the block is in
    /// @param pos     The position of the block
    /// @param entity  The entity which fell onto the block
    /// @param count   How many particles should be spawned
    public static void spawnLandingParticles(CamoContainer<?, ?> camo, ServerLevel level, BlockPos pos, LivingEntity entity, int count) {
        spawnLandingParticles(new CamoParticleOptions(camo), level, pos, entity, count);
    }

    private static void spawnLandingParticles(ParticleOptions options, ServerLevel level, BlockPos pos, LivingEntity entity, int count) {
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        BlockPos entityPos = entity.blockPosition();
        if (pos.getX() != entityPos.getX() || pos.getZ() != entityPos.getZ()) {
            double offX = x - (double)pos.getX() - 0.5D;
            double offZ = z - (double)pos.getZ() - 0.5D;
            double maxOff = Math.max(Math.abs(offX), Math.abs(offZ));
            x = (double) pos.getX() + 0.5D + offX / maxOff * 0.5D;
            z = (double) pos.getZ() + 0.5D + offZ / maxOff * 0.5D;
        }

        level.sendParticles(options, x, y, z, count, 0D, 0D, 0D, 0.15D);
    }

    /// Spawn particles when an entity sprints across a framed block.
    ///
    /// @param camo    The camo applied to the framed block
    /// @param overlay The block overlay applied to the framed block
    /// @param state   The state of the block
    /// @param level   The level the block is in
    /// @param pos     The position of the block
    /// @param entity  The entity sprinting across the block
    public static void spawnRunningParticles(
            CamoContainer<?, ?> camo,
            @Nullable Holder<BlockOverlay> overlay,
            BlockState state,
            Level level,
            BlockPos pos,
            Entity entity
    ) {
        if (overlay != null && overlay.value().isSideSolid(state, Direction.UP)) {
            spawnRunningParticles(new BlockOverlayParticleOptions(overlay), level, pos, entity);
            return;
        }
        spawnRunningParticles(camo, level, pos, entity);
    }

    /// Spawn particles when an entity sprints across a framed block.
    ///
    /// @param camo    The camo applied to the framed block
    /// @param level   The level the block is in
    /// @param pos     The position of the block
    /// @param entity  The entity sprinting across the block
    public static void spawnRunningParticles(CamoContainer<?, ?> camo, Level level, BlockPos pos, Entity entity) {
        spawnRunningParticles(new CamoParticleOptions(camo), level, pos, entity);
    }

    private static void spawnRunningParticles(ParticleOptions options, Level level, BlockPos pos, Entity entity) {
        Vec3 delta = entity.getDeltaMovement();
        BlockPos enityPos = entity.blockPosition();

        double x = entity.getRandomX(0.5D);
        double z = entity.getRandomZ(0.5D);
        if (enityPos.getX() != pos.getX()) {
            x = Mth.clamp(x, pos.getX(), pos.getX() + 1D);
        }
        if (enityPos.getZ() != pos.getZ()) {
            z = Mth.clamp(z, pos.getZ(), pos.getZ() + 1D);
        }

        level.addParticle(options, x, entity.getY() + 0.1D, z, delta.x * -4D, 1.5D, delta.z * -4D);
    }

    public static final class Client {
        /// Spawn particles when a player hits a framed block.
        ///
        /// @param state   The state of the block
        /// @param target  The position the block was hit at
        /// @param camo    The camo applied to the block
        /// @param overlay The block overlay applied to the block
        /// @param engine  The particle engine to use for spawning the particles
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public static void addHitEffects(BlockState state, Level level, BlockHitResult target, CamoContainer<?, ?> camo, @Nullable Holder<BlockOverlay> overlay, ParticleEngine engine) {
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

            switch (side) {
                case DOWN ->  y = by + aabb.minY - 0.1D;
                case UP ->    y = by + aabb.maxY + 0.1D;
                case NORTH -> z = bz + aabb.minZ - 0.1D;
                case SOUTH -> z = bz + aabb.maxZ + 0.1D;
                case WEST ->  x = bx + aabb.minX - 0.1D;
                case EAST ->  x = bx + aabb.maxX + 0.1D;
            }

            CamoContent<?> content = camo.getContent();
            CamoContainerClientHandler containerHandler = camo.getClientHandler();
            CamoContentClientHandler contentHandler = content.getClientHandler();
            int tintColor = containerHandler.getParticleTintValue(camo, clientLevel, pos);
            engine.add(contentHandler.createParticle(clientLevel, x, y, z, 0.0D, 0.0D, 0.0D, pos, content, tintColor)
                    .setPower(0.2F)
                    .scale(0.6F)
            );
            if (overlay != null) {
                engine.add(InternalClientAPI.INSTANCE.createBlockOverlayParticle(clientLevel, x, y, z, 0.0D, 0.0D, 0.0D, pos, overlay.value()));
            }
        }

        /// Spawn particles when a player breaks a framed block.
        ///
        /// @param state   The state of the block
        /// @param level   The level the block is in
        /// @param pos     The position of the block
        /// @param camo    The camo applied to the block
        /// @param overlay The block overlay applied to the block
        /// @param engine  The particle engine to use for spawning the particles
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public static void addDestroyEffects(BlockState state, Level level, BlockPos pos, CamoContainer<?, ?> camo, @Nullable Holder<BlockOverlay> overlay, ParticleEngine engine) {
            CamoContent<?> content = camo.getContent();
            CamoContainerClientHandler containerHandler = camo.getClientHandler();
            CamoContentClientHandler contentHandler = content.getClientHandler();
            ClientLevel clientLevel = (ClientLevel) level;
            int tintColor = containerHandler.getParticleTintValue(camo, clientLevel, pos);

            List<AABB> boxes = state.getShape(level, pos).toAabbs();
            double countMult = 1D / boxes.size();
            for (AABB aabb : boxes) {
                double sizeX = Math.min(1D, aabb.maxX - aabb.minX);
                double sizeY = Math.min(1D, aabb.maxY - aabb.minY);
                double sizeZ = Math.min(1D, aabb.maxZ - aabb.minZ);
                int xCount = Math.max(2, Mth.ceil(sizeX / 0.25D * countMult));
                int yCount = Math.max(2, Mth.ceil(sizeY / 0.25D * countMult));
                int zCount = Math.max(2, Mth.ceil(sizeZ / 0.25D * countMult));

                for (int iX = 0; iX < xCount; ++iX) {
                    for (int iY = 0; iY < yCount; ++iY) {
                        for (int iZ = 0; iZ < zCount; ++iZ) {
                            double offX = ((double) iX + 0.5D) / (double) xCount;
                            double offY = ((double) iY + 0.5D) / (double) yCount;
                            double offZ = ((double) iZ + 0.5D) / (double) zCount;
                            double x = pos.getX() + offX * sizeX + aabb.minX;
                            double y = pos.getY() + offY * sizeY + aabb.minY;
                            double z = pos.getZ() + offZ * sizeZ + aabb.minZ;
                            double sx = offX - 0.5D;
                            double sy = offY - 0.5D;
                            double sz = offZ - 0.5D;
                            engine.add(contentHandler.createParticle(clientLevel, x, y, z, sx, sy, sz, pos, content, tintColor));
                            if (overlay != null) {
                                engine.add(InternalClientAPI.INSTANCE.createBlockOverlayParticle(clientLevel, x, y, z, sx, sy, sz, pos, overlay.value()));
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
