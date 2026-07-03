package io.github.xfacthd.framedblocks.api.model.wrapping;

import io.github.xfacthd.framedblocks.client.model.unbaked.FramedBlockModelDefinition;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBakery;
import org.jetbrains.annotations.ApiStatus;

/// Provides access to auxiliary models specified in a framed block's [FramedBlockModelDefinition].
@ApiStatus.NonExtendable
public interface AuxModelProvider {
    /// {@return the blockstate model associated with the given key or the missing model if absent}
    BlockStateModel getModel(String key);

    /// {@return an aux model provider which always returns the missing model}
    static AuxModelProvider empty(ModelBakery.BakingResult bakingResult) {
        return _ -> bakingResult.missingModels().block();
    }

    /// {@return an aux model provider from which no models may be queried}
    static AuxModelProvider invalid() {
        return _ -> { throw new UnsupportedOperationException(""); };
    }
}
