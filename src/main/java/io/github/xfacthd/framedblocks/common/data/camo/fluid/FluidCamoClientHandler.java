package io.github.xfacthd.framedblocks.common.data.camo.fluid;

import io.github.xfacthd.framedblocks.api.camo.CamoClientHandler;
import io.github.xfacthd.framedblocks.client.model.FluidModel;
import io.github.xfacthd.framedblocks.client.render.particle.FluidSpriteParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class FluidCamoClientHandler extends CamoClientHandler<FluidCamoContent>
{
    public static final CamoClientHandler<FluidCamoContent> INSTANCE = new FluidCamoClientHandler();
    private static final Map<FluidCamoContent, BlockStateModel> FLUID_MODEL_CACHE = new ConcurrentHashMap<>();

    private FluidCamoClientHandler() { }

    @Override
    public BlockStateModel getOrCreateModel(FluidCamoContent camo)
    {
        return FLUID_MODEL_CACHE.computeIfAbsent(camo, FluidModel::create);
    }

    @Override
    public Particle makeHitDestroyParticle(
            ClientLevel level, double x, double y, double z, double sx, double sy, double sz, FluidCamoContent camo, BlockPos pos
    )
    {
        return new FluidSpriteParticle(level, x, y, z, sx, sy, sz, camo.getFluid());
    }

    public static void clearModelCache()
    {
        FLUID_MODEL_CACHE.clear();
    }
}
