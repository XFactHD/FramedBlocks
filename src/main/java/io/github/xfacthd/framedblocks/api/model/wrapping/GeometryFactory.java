package io.github.xfacthd.framedblocks.api.model.wrapping;

import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.state.BlockState;

public interface GeometryFactory {
    Geometry create(Context ctx);

    record Context(BlockState state, BlockStateModel baseModel, AuxModelProvider auxModels, MaterialLookup materialLookup) {
        public Context withState(BlockState newState) {
            return new Context(newState, baseModel, auxModels, materialLookup);
        }
    }
}
