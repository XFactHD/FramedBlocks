package xfacthd.framedblocks.client.loader.fallback;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

final class FallbackGeometry implements IUnbakedGeometry<FallbackGeometry>
{
    private final BlockModel model;

    public FallbackGeometry(BlockModel model)
    {
        this.model = model;
    }

    @Override
    public BakedModel bake(
            IGeometryBakingContext ctx,
            ModelBaker baker,
            Function<Material, TextureAtlasSprite> spriteGetter,
            ModelState modelState,
            ItemOverrides overrides
    )
    {
        return model.bake(baker, model, spriteGetter, modelState, true);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context)
    {
        model.resolveParents(modelGetter);
    }
}
