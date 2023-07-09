package xfacthd.framedblocks.common.block;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import xfacthd.framedblocks.api.block.render.FramedBlockRenderProperties;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class FramedDoubleBlockRenderProperties extends FramedBlockRenderProperties
{
    @Override
    public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine engine)
    {
        boolean suppressed = super.addHitEffects(state, level, target, engine);
        BlockHitResult hit = (BlockHitResult) target;
        if (!suppressed && level.getBlockEntity(hit.getBlockPos()) instanceof FramedDoubleBlockEntity be)
        {
            addHitEffects(state, level, hit, be.getCamo().getState(), engine);
            addHitEffects(state, level, hit, be.getCamoTwo().getState(), engine);
            return true;
        }
        return suppressed;
    }

    private static void addHitEffects(
            BlockState state, Level level, BlockHitResult target, BlockState camoState, ParticleEngine engine
    )
    {
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

        engine.add(new TerrainParticle((ClientLevel) level, x, y, z, 0.0D, 0.0D, 0.0D, camoState, pos)
                .setPower(0.2F)
                .scale(0.6F)
        );
    }
}
