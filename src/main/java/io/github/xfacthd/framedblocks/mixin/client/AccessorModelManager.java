package io.github.xfacthd.framedblocks.mixin.client;

import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ModelManager.class)
public interface AccessorModelManager {
    @Accessor("bakedItemStackModels")
    Map<Identifier, ItemModel> framedblocks$getBakedItemStackModels();

    @Accessor("missingModels")
    ModelBakery.MissingModels framedblocks$getMissingModels();
}
