package io.github.xfacthd.framedblocks.mixin.client;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelManager.class)
public interface AccessorModelManager {
    @Accessor("missingModels")
    ModelBakery.MissingModels framedblocks$getMissingModels();
}
