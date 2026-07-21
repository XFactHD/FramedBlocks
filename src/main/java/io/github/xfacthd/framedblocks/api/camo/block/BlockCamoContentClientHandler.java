package io.github.xfacthd.framedblocks.api.camo.block;

import io.github.xfacthd.framedblocks.api.camo.CamoContentClientHandler;
import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.model.util.TintUtils;
import io.github.xfacthd.framedblocks.api.render.fakelevel.ColorResolvingLevel;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.List;

final class BlockCamoContentClientHandler extends CamoContentClientHandler<BlockCamoContent> {
    static final CamoContentClientHandler<BlockCamoContent> INSTANCE = new BlockCamoContentClientHandler();

    private BlockCamoContentClientHandler() { }

    @Override
    public BlockStateModel getOrCreateModel(BlockCamoContent camo) {
        return ModelUtils.getModel(camo.getState());
    }

    @Override
    public Particle createParticle(ClientLevel level, double x, double y, double z, double sx, double sy, double sz, BlockPos pos, BlockCamoContent camo, int tintColor) {
        return InternalClientAPI.INSTANCE.createBlockBreakParticle(level, x, y, z, 0.0D, 0.0D, 0.0D, pos, camo.getState(), tintColor);
    }

    @Override
    public int getTintCount(BlockCamoContent camo) {
        return TintUtils.getTintSources(camo).size();
    }

    @Override
    public void collectTintValues(BlockCamoContent camo, BlockAndTintGetter level, BlockPos pos, IntList tintList) {
        for (BlockTintSource tintSource : TintUtils.getTintSources(camo)) {
            tintList.add(tintSource.colorInWorld(camo.getState(), level, pos));
        }
    }

    @Override
    public void collectTintValues(BlockCamoContent camo, ItemStack stack, IntList tintList) {
        for (BlockTintSource tintSource : TintUtils.getTintSources(camo)) {
            tintList.add(tintSource.color(camo.getState()));
        }
    }

    @Override
    public int getParticleTintValue(BlockCamoContent camo, BlockAndTintGetter level, BlockPos pos) {
        List<BlockTintSource> sources = TintUtils.getTintSources(camo);
        return sources.isEmpty() ? -1 : sources.getFirst().colorAsTerrainParticle(camo.getState(), level, pos);
    }

    @Override
    public int getDefaultTintValue(BlockCamoContent camo) {
        List<BlockTintSource> tintSources = TintUtils.getTintSources(camo);
        if (!tintSources.isEmpty()) {
            return tintSources.getFirst().colorAsTerrainParticle(camo.getState(), ColorResolvingLevel.INSTANCE, BlockPos.ZERO);
        }
        return -1;
    }
}
