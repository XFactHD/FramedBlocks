package xfacthd.framedblocks.common.compat.modernfix;

import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.embeddedt.modernfix.api.entrypoint.ModernFixClientIntegration;
import xfacthd.framedblocks.api.model.wrapping.ModelAccessor;
import xfacthd.framedblocks.client.model.FramedBlockModel;
import xfacthd.framedblocks.client.modelwrapping.ModelWrappingManager;

public final class FramedModernFixClientIntegration implements ModernFixClientIntegration
{
    public FramedModernFixClientIntegration() { }

    @Override
    public void onDynamicResourcesStatusChange(boolean enabled)
    {
        ModernFixCompat.dynamicResources = enabled;
    }

    @Override
    public BakedModel onBakedModelLoad(ResourceLocation location, UnbakedModel unbakedModel, BakedModel originalModel, ModelState state, ModelBakery bakery)
    {
        if (ModernFixCompat.dynamicResources)
        {
            BakedModel baseModel = originalModel;
            if (originalModel instanceof FramedBlockModel framedModel)
            {
                // Due to the way the dynamic baking works, the original model may already be wrapped
                // -> unwrap it to be consistent with vanilla behaviour and avoid double wrapping
                baseModel = framedModel.getBaseModel();
            }
            ModelAccessor accessor = bakery.getBakedTopLevelModels()::get;
            BakedModel wrappedModel = ModelWrappingManager.handle(location, baseModel, accessor);
            // Return incoming original model instead of the potentially unwrapped model if no wrapping was done
            return wrappedModel != baseModel ? wrappedModel : originalModel;
        }
        return originalModel;
    }
}
