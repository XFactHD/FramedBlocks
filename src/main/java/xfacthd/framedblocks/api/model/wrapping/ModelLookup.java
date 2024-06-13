package xfacthd.framedblocks.api.model.wrapping;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;

public interface ModelLookup
{
    BakedModel get(ModelResourceLocation id);
}
