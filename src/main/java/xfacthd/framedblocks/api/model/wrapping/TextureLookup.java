package xfacthd.framedblocks.api.model.wrapping;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public interface TextureLookup
{
    TextureAtlasSprite get(ResourceLocation id);



    @SuppressWarnings("deprecation")
    static TextureLookup bindBlockAtlas(Function<Material, TextureAtlasSprite> getter)
    {
        return id -> getter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, id));
    }
}
