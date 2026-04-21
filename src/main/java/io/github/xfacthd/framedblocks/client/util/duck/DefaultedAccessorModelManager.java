package io.github.xfacthd.framedblocks.client.util.duck;

import io.github.xfacthd.framedblocks.mixin.client.AccessorModelManager;
import net.minecraft.client.resources.model.ModelBakery;

@SuppressWarnings("unused") // Used via interface injection
public interface DefaultedAccessorModelManager extends AccessorModelManager {
    @Override
    default ModelBakery.MissingModels framedblocks$getMissingModels() {
        throw new AssertionError();
    }
}
