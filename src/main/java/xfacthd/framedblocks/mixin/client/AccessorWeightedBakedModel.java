package xfacthd.framedblocks.mixin.client;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WeightedBakedModel.class)
public interface AccessorWeightedBakedModel
{
    @Accessor("wrapped")
    BakedModel framedblocks$getWrappedModel();
}
