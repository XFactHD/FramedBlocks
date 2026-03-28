package io.github.xfacthd.framedblocks.client.util.duck;

import io.github.xfacthd.framedblocks.mixin.client.AccessorModelManager;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.Identifier;

import java.util.Map;

@SuppressWarnings("unused") // Used via interface injection
public interface DefaultedAccessorModelManager extends AccessorModelManager {
    @Override
    default Map<Identifier, ItemModel> framedblocks$getBakedItemStackModels() {
        throw new AssertionError();
    }

    @Override
    default ModelBakery.MissingModels framedblocks$getMissingModels() {
        throw new AssertionError();
    }
}
