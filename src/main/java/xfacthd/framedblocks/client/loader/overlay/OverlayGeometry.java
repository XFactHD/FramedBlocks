package xfacthd.framedblocks.client.loader.overlay;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

record OverlayGeometry(BlockModel wrapped, Vector3f offset, Vector3f scale) implements IModelGeometry<OverlayGeometry>
{
    @Override
    public BakedModel bake(IModelConfiguration cfg, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        Transformation transformation = transform.getRotation().compose(new Transformation(offset, null, scale, null));
        transform = new SimpleModelState(transformation);

        BakedModel model = wrapped.bake(bakery, wrapped, spriteGetter, transform, modelLocation, true);
        return offset.equals(Vector3f.ZERO) ? model : new OverlayModel(model, offset, scale);
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> errors)
    {
        return wrapped.getMaterials(modelGetter, errors);
    }
}
