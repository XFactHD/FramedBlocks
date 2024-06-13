package xfacthd.framedblocks.common.compat.modernfix;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.embeddedt.modernfix.api.entrypoint.ModernFixClientIntegration;
import xfacthd.framedblocks.api.model.wrapping.ModelLookup;
import xfacthd.framedblocks.api.model.wrapping.TextureLookup;
import xfacthd.framedblocks.client.model.FramedBlockModel;
import xfacthd.framedblocks.client.modelwrapping.ModelWrappingManager;

import java.util.function.Function;

public final class FramedModernFixClientIntegration implements ModernFixClientIntegration
{
    public FramedModernFixClientIntegration() { }

    @Override
    public void onDynamicResourcesStatusChange(boolean enabled)
    {
        ModernFixCompat.dynamicResources = enabled;
    }

    @Override
    public BakedModel onBakedModelLoad(
            ResourceLocation location,
            UnbakedModel unbakedModel,
            BakedModel originalModel,
            ModelState state,
            ModelBakery bakery,
            Function<Material, TextureAtlasSprite> textureGetter
    )
    {
        /*if (ModernFixCompat.dynamicResources)
        {
            BakedModel baseModel = originalModel;
            if (originalModel instanceof FramedBlockModel framedModel)
            {
                // Due to the way the dynamic baking works, the original model may already be wrapped
                // -> unwrap it to be consistent with vanilla behaviour and avoid double wrapping
                baseModel = framedModel.getBaseModel();
            }

            ModelLookup accessor = bakery.getBakedTopLevelModels()::get;
            TextureLookup textureLookup = TextureLookup.bindBlockAtlas(textureGetter);
            BakedModel wrappedModel = ModelWrappingManager.handle(location, baseModel, accessor, textureLookup);

            // Return incoming original model instead of the potentially unwrapped model if no wrapping was done
            return wrappedModel != baseModel ? wrappedModel : originalModel;
        }*/
        return originalModel;
    }
}
