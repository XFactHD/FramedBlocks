package xfacthd.framedblocks.api.model.wrapping;

import net.minecraft.client.Minecraft;
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

    /**
     * {@return a lookup that is only usable at the end of or outside of a resource reload}
     */
    @SuppressWarnings("deprecation")
    static TextureLookup runtime()
    {
        return id -> Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(id);
    }
}
