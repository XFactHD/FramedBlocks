package io.github.xfacthd.framedblocks.api.model.wrapping;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBakery;

public interface AuxModelProvider {
    BlockStateModel getModel(String key);

    static AuxModelProvider empty(ModelBakery.BakingResult bakingResult) {
        return _ -> bakingResult.missingModels().block();
    }

    static AuxModelProvider invalid() {
        return _ -> { throw new UnsupportedOperationException(""); };
    }
}
