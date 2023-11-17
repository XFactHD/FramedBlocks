package xfacthd.framedblocks.common.compat.modernfix;

import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
//import org.embeddedt.modernfix.api.entrypoint.ModernFixClientIntegration;
import xfacthd.framedblocks.api.model.wrapping.ModelAccessor;
import xfacthd.framedblocks.client.modelwrapping.ModelWrappingManager;

public final class FramedModernFixClientIntegration// implements ModernFixClientIntegration
{
    public FramedModernFixClientIntegration() { }

    //@Override
    public void onDynamicResourcesStatusChange(boolean enabled)
    {
        ModernFixCompat.dynamicResources = enabled;
    }

    //@Override
    public BakedModel onBakedModelLoad(ResourceLocation location, UnbakedModel baseModel, BakedModel originalModel, ModelState state, ModelBakery bakery)
    {
        if (ModernFixCompat.dynamicResourcesEnabled())
        {
            ModelAccessor accessor = bakery.getBakedTopLevelModels()::get;
            return ModelWrappingManager.handle(location, originalModel, accessor);
        }
        return originalModel;
    }
}
