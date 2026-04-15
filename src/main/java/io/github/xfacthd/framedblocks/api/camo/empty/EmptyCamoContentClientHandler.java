package io.github.xfacthd.framedblocks.api.camo.empty;

import io.github.xfacthd.framedblocks.api.FramedBlocksAPI;
import io.github.xfacthd.framedblocks.api.camo.CamoContentClientHandler;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

final class EmptyCamoContentClientHandler extends CamoContentClientHandler<EmptyCamoContent> {
    static final CamoContentClientHandler<EmptyCamoContent> INSTANCE = new EmptyCamoContentClientHandler();

    private EmptyCamoContentClientHandler() { }

    @Override
    public BlockStateModel getOrCreateModel(EmptyCamoContent camo) {
        return ModelUtils.getModel(FramedBlocksAPI.INSTANCE.getDefaultModelState());
    }

    @Override
    public Particle makeHitDestroyParticle(ClientLevel level, double x, double y, double z, double sx, double sy, double sz, EmptyCamoContent camo, BlockPos pos) {
        BlockState state = FramedBlocksAPI.INSTANCE.getDefaultModelState();
        return new TerrainParticle(level, x, y, z, 0.0D, 0.0D, 0.0D, state, pos);
    }

    @Override
    public int getTintCount(EmptyCamoContent camo) {
        return 0;
    }

    @Override
    public void collectTintValues(EmptyCamoContent camo, BlockAndTintGetter level, BlockPos pos, IntList tintList) { }

    @Override
    public void collectTintValues(EmptyCamoContent camo, ItemStack stack, IntList tintList) { }

    @Override
    public int getParticleTintValue(EmptyCamoContent camo) {
        return -1;
    }
}
