package xfacthd.framedblocks.api.model.wrapping;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

public interface ModelAccessor
{
    BakedModel get(ResourceLocation id);
}
