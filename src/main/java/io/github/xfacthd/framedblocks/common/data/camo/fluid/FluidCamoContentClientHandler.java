package io.github.xfacthd.framedblocks.common.data.camo.fluid;

import io.github.xfacthd.framedblocks.api.camo.CamoContentClientHandler;
import io.github.xfacthd.framedblocks.api.camo.resource.ResourceCamoContentClientHandler;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.model.util.TintUtils;
import io.github.xfacthd.framedblocks.client.render.particle.FluidSpriteParticle;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public final class FluidCamoContentClientHandler extends ResourceCamoContentClientHandler<FluidResource, FluidCamoContent> {
    public static final CamoContentClientHandler<FluidCamoContent> INSTANCE = new FluidCamoContentClientHandler();

    private FluidCamoContentClientHandler() { }

    @Override
    public ResourceModelSpec getModelSpec(FluidCamoContent camo) {
        FluidModel fluidModel = ModelUtils.getFluidModel(camo.getFluid().defaultFluidState());
        Material.Baked stillMaterial = fluidModel.stillMaterial();
        Material.Baked flowingMaterial = fluidModel.flowingMaterial();
        return new ResourceModelSpec(stillMaterial, flowingMaterial, fluidModel.fluidTintSource() != null, camo.getFlowDirection());
    }

    @Override
    public Particle createParticle(ClientLevel level, double x, double y, double z, double sx, double sy, double sz, BlockPos pos, FluidCamoContent camo, int tintColor) {
        return new FluidSpriteParticle(level, x, y, z, sx, sy, sz, pos, camo.getFluid(), tintColor);
    }

    @Override
    public int getTintCount(FluidCamoContent camo) {
        FluidTintSource tintSource = ModelUtils.getFluidModel(camo.getFluid().defaultFluidState()).fluidTintSource();
        return tintSource != null ? 1 : 0;
    }

    @Override
    public void collectTintValues(FluidCamoContent camo, BlockAndTintGetter level, BlockPos pos, IntList tintList) {
        FluidState fluidState = camo.getFluid().defaultFluidState();
        FluidTintSource tintSource = ModelUtils.getFluidModel(fluidState).fluidTintSource();
        if (tintSource != null) {
            tintList.add(tintSource.colorInWorld(fluidState, fluidState.createLegacyBlock(), level, pos));
        }
    }

    @Override
    public void collectTintValues(FluidCamoContent camo, ItemStack stack, IntList tintList) {
        FluidState fluidState = camo.getFluid().defaultFluidState();
        FluidTintSource tintSource = ModelUtils.getFluidModel(fluidState).fluidTintSource();
        if (tintSource != null) {
            tintList.add(tintSource.color(fluidState));
        }
    }

    @Override
    public int getParticleTintValue(FluidCamoContent camo, BlockAndTintGetter level, BlockPos pos) {
        return TintUtils.getFluidColor(level, pos, camo.getFluid().defaultFluidState());
    }

    @Override
    public int getDefaultTintValue(FluidCamoContent camo) {
        FluidState fluidState = camo.getFluid().defaultFluidState();
        FluidTintSource tintSource = ModelUtils.getFluidModel(fluidState).fluidTintSource();
        return tintSource != null ? tintSource.color(fluidState) : -1;
    }
}
