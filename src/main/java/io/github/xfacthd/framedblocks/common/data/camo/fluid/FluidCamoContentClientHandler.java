package io.github.xfacthd.framedblocks.common.data.camo.fluid;

import io.github.xfacthd.framedblocks.api.camo.CamoContentClientHandler;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.client.model.FluidCubeModel;
import io.github.xfacthd.framedblocks.client.render.particle.FluidSpriteParticle;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.fluid.FluidTintSource;

public final class FluidCamoContentClientHandler extends CamoContentClientHandler<FluidCamoContent>
{
    public static final CamoContentClientHandler<FluidCamoContent> INSTANCE = new FluidCamoContentClientHandler();

    private FluidCamoContentClientHandler() { }

    @Override
    public BlockStateModel getOrCreateModel(FluidCamoContent camo)
    {
        return FluidCubeModel.getOrCreate(camo);
    }

    @Override
    public Particle makeHitDestroyParticle(
            ClientLevel level, double x, double y, double z, double sx, double sy, double sz, FluidCamoContent camo, BlockPos pos
    )
    {
        return new FluidSpriteParticle(level, x, y, z, sx, sy, sz, camo.getFluid());
    }

    @Override
    public int getTintCount(FluidCamoContent camo)
    {
        FluidTintSource tintSource = ModelUtils.getFluidModel(camo.getFluid().defaultFluidState()).fluidTintSource();
        return tintSource != null ? 1 : 0;
    }

    @Override
    public void collectTintValues(FluidCamoContent camo, BlockAndTintGetter level, BlockPos pos, IntList tintList)
    {
        FluidState fluidState = camo.getFluid().defaultFluidState();
        FluidTintSource tintSource = ModelUtils.getFluidModel(fluidState).fluidTintSource();
        if (tintSource != null)
        {
            tintList.add(tintSource.colorInWorld(fluidState, fluidState.createLegacyBlock(), level, pos));
        }
    }

    @Override
    public void collectTintValues(FluidCamoContent camo, ItemStack stack, IntList tintList)
    {
        FluidState fluidState = camo.getFluid().defaultFluidState();
        FluidTintSource tintSource = ModelUtils.getFluidModel(fluidState).fluidTintSource();
        if (tintSource != null)
        {
            tintList.add(tintSource.color(fluidState));
        }
    }
}
